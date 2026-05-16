# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK tools proguard configuration.

# Keep Compose
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
