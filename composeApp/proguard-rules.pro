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

# --- Kotlinx Serialization ---
-keepattributes *Annotation*, InnerClasses, EnclosingMethod, Signature, Exceptions
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keep,allowobfuscation,allowoptimization class com.pizzza.pizzzaapp.repository.network.model.** { *; }
-keep,allowobfuscation,allowoptimization class com.pizzza.pizzzaapp.repository.network.exception.CompleteErrorModel { *; }

# --- Ktor ---
-keep class io.ktor.** { *; }

# --- Room ---
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Entity
-keep class * extends androidx.room.Dao
-keep class * extends androidx.room.TypeConverter

# --- Koin ---
-keep class org.koin.** { *; }