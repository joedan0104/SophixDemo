package com.cdel.sophixdemo;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixApplication;
import com.taobao.sophix.SophixEntry;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;


/**
 * Sophix入口类，专门用于初始化Sophix，不应包含任何业务逻辑。
 * 此类必须继承自SophixApplication，onCreate方法不需要实现。
 * 此类不应与项目中的其他类有任何互相调用的逻辑，必须完全做到隔离。
 * AndroidManifest中设置application为此类，而SophixEntry中设为原先Application类。
 * 注意原先Application里不需要再重复初始化Sophix，并且需要避免混淆原先Application类。
 * 如有其它自定义改造，请咨询官方后妥善处理。
 *
 * @author wangxin
 * @time 19-1-10 上午10:55
 */
public class SophixStubApplication extends SophixApplication {
    private final String TAG = "SophixStubApplication";

    /**
     * 当前补丁包的版本号
     */
    public static int SOPHIX_VETSION = 0;

    /**
     * 补丁状态
     */
    public static boolean SOPHIX_STATUS_SUCCESS = false;

    /**
     * sophix热修复补丁修复结果,用户统计热修复状况
     * 由于热修复初始化在attachBaseContext中,因此采用静态变量
     * 默认值-1
     */
    public static int SOPHIX_STATISTICS_CODE = -1;

    /**
     * 热修复当前的状态
     */
    public static int SOPHIX_STATUS_CODE;


    // 此处SophixEntry应指定真正的Application，并且保证RealApplicationStub类名不被混淆。
    @Keep
    @SophixEntry(ModelApplication.class)
    static class RealApplicationStub {
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // 如果需要使用MultiDex，需要在此处调用。
        MultiDex.install(this);
        initSophix();
    }

    /**
     * 初始化热修复
     * 进行SDK初始化操作，初始化之前不能用到其他自定义类，否则极有可能导致崩溃
     * 非系统API的类不能使用,如果是SophixStubApplication内部直接定义的静态变量，可以使用
     */
    private void initSophix() {
        // 不能使用buildconfig
        String appVersion = "0.0.0";
        try {
            appVersion = this.getPackageManager()
                .getPackageInfo(this.getPackageName(), 0)
                .versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // initialize最好放在attachBaseContext最前面
        // 热修复初始化
        SophixManager.getInstance().setContext(this)
            // 设置应用的版本号
            .setAppVersion(appVersion)
            .setAesKey(null)
            // 是否调试模式,正式时改成false,false会对补丁做签名校验
            .setEnableDebug(false)
            // 热修复状态监听
            .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                @Override
                public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                    // 只能使用android自带的log
                    Log.i(TAG, "mode = " + mode + " code=" + code + "info = " + info);
                    SOPHIX_STATUS_CODE = code;
                    // 调用了query后,若没有补丁则会回调CODE_REQ_NOUPDATE
                    // 每次启动如果有补丁则会调用CODE_LOAD_SUCCESS,前提是加载成功
                    if (code == PatchStatus.CODE_REQ_NOUPDATE) {
                        Log.i(TAG, "没有最新补丁");
                    } else if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                        // 每次重启都会加载补丁
                        Log.i(TAG, "补丁加载成功");
                        SOPHIX_STATUS_SUCCESS = true;
                        SOPHIX_VETSION = handlePatchVersion;
                    } else if (code == PatchStatus.CODE_DOWNLOAD_SUCCESS) {
                        // 统计热修复下载成功后,统计为1个用户修复成功
                        SOPHIX_STATISTICS_CODE = code;
                        // 补丁状态设置为成功
                        SOPHIX_STATUS_SUCCESS = true;
                        Log.i(TAG, "补丁下载成功");
                    } else if (code == PatchStatus.CODE_LOAD_RELAUNCH || code == PatchStatus.CODE_PRELOAD_SUCCESS) {
                        Log.i(TAG, "补丁预加载");
                        SOPHIX_STATUS_SUCCESS = true;
                    } else {
                        // 其它错误信息, 查看PatchStatus类说明
                        SOPHIX_STATISTICS_CODE = code;
                        Log.i(TAG, " 其它错误信息, 查看PatchStatus类说明");
                    }
                }
            }).initialize();
    }
}
