# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

-dontobfuscate
# -dontpreverify
-allowaccessmodification

-keepattributes SourceFile, LineNumberTable, Exception, *Annotation*, InnerClasses, EnclosingMethod, Signature

-keepclassmembers, allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class Main {
	public static void main(java.lang.String[]);
}
-keep class org.fusesource.jansi.internal.* { *; }

-keepclassmembers, allowoptimization class * {
    *** INSTANCE;
}
