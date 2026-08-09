# Keep rules for models and entity classes
-keep class com.example.data.models.** { *; }
-keep class com.example.data.db.** { *; }

# Keep rules for reflection-based hardware calls
-keepclassmembers class * {
    public static java.lang.String get(java.lang.String);
    public static java.lang.String get(java.lang.String, java.lang.String);
}
