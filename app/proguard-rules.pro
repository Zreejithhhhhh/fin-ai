# MoneyMoment AI ProGuard Rules
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <fields>;
}
