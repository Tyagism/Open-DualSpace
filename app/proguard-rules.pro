# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep DeviceAdminReceiver
-keep class com.opendualspace.app.receiver.DeviceAdminReceiver { *; }

# Keep data classes
-keep class com.opendualspace.app.data.model.** { *; }

# Compose
-dontwarn androidx.compose.**

# Google Ads
-keep class com.google.android.gms.ads.** { *; }
