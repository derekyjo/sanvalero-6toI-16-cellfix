# Reglas de ProGuard para CellFix
-keep class com.sanvalero.cellfix.** { *; }
-keepattributes *Annotation*
-dontwarn org.jetbrains.annotations.**
