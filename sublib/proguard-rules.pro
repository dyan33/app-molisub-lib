# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}



# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.cp.log.** {*;}
#-keep class org.jsoup.** {*;}
#-keep class com.alibaba.** {*;}
#-keep class com.squareup.okhttp3.** {*;}
#-keep class com.blankj.** {*;}

-keep class org.jsoup.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.squareup.okhttp3.** {*;}
-keep class com.squareup.retrofit2.** {*;}
-keep class com.blankj.** {*;}
-keep class com.facebook.** {*;}
-keep class com.google.** {*;}
-keep class io.reactivex.rxjava2.** {*;}
-keep class androidx.appcompat.** {*;}
-keep class androidx.constraintlayout.** {*;}
-keep class androidx.core.** {*;}
-keep class com.blankj.** {*;}
-keep class androidx.recyclerview.** {*;}
-keep class androidx.legacy.** {*;}
-keep class androidx.cardview.** {*;}
-keep class com.orhanobut.** {*;}
-keep class com.github.megatronking.** {*;}
-keep class com.android.support.** {*;}
-keep class org.jetbrains.kotlinx.** {*;}
-keep class com.cp.log.CheckBean {*;}
-keep class com.cp.log.Log4js {*;}
-keep class com.cp.log.event.LogEvent {*;}


-flattenpackagehierarchy 'com.cp.log'


