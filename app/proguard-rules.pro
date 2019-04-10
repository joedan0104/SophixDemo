-optimizationpasses 5
# 是否使用大小写混合
-dontusemixedcaseclassnames
# 指定不去忽略包可见的库类
-dontskipnonpubliclibraryclasses
# 指定不去忽略包可见的库类的成员
-dontskipnonpubliclibraryclassmembers
# 混淆时是否做预校验
-dontpreverify
# 混淆时是否记录日志
-verbose
# 忽略所有警告
-ignorewarnings
# 指定混淆是采用的算法，后面的参数是一个过滤器(谷歌推荐的算法，一般不做更改)
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 保留反射
-keepattributes EnclosingMethod
# 避免混淆泛型
-keepattributes Signature
# 保留注解不混淆
-keepattributes *Annotation*
# 保留异常和内部类
-keepattributes Exceptions,InnerClasses
# 保留JavascriptInterface
-keepattributes *JavascriptInterface*
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.cdel.accmobile.app.allcatch.bean.**{ *; }#大数据采集

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keep public class * extends android.webkit.WebView {
    <methods>;
}

#基线包使用，生成mapping.txt
-printmapping mapping.txt
##生成的mapping.txt在app/build/outputs/mapping/release路径下，移动到/app路径下
##修复后的项目使用，保证混淆结果一致
#-applymapping mapping.txt

#hotfix
-keep class com.taobao.sophix.**{*;}
-keep class com.ta.utdid2.device.**{*;}
-dontwarn com.alibaba.sdk.android.utils.**
#防止inline
-dontoptimize
-keepclassmembers class com.cdel.sophixdemo.ModelApplication {
    public <init>();
}
# 如果不使用android.support.annotation.Keep则需加上此行
# -keep class com.my.pkg.SophixStubApplication$RealApplicationStub

