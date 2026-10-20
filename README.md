# آموزش تکواندو — Taekwondo Academy

اپ اندروید جامع آموزش تکواندو (فارسی، راست‌چین) بر اساس برنامه رسمی کوکی‌وان / WT:

- **فرم‌ها (پومسه):** هر ۸ فرم تگوک با نماد و فلسفه، سطح گئوپ، تعداد حرکت و توضیح گام‌به‌گام
- **تکنیک‌ها:** ۱۶ تکنیک (ضربات پا، دفاع‌ها، ضربات دست، ایستادن‌ها) با مراحل اجرا، خطاهای رایج و لینک فیلم آموزشی
- **برنامه تمرین:** ۳ برنامه (مبتدی ۴ هفته‌ای، متوسط، پیشرفته/مسابقه) با جدول هفتگی
- **کلاس آنلاین:** ورود به کلاس زنده روی Jitsi Meet (رایگان) + فرم ثبت‌نام کلاس با ذخیره روی گوشی
- **پالت رنگی ۶۰/۳۰/۱۰:** سفید و خاکستری خیلی روشن (پس‌زمینه)، مشکی/زغالی `#17171C` (نوارها، تیترها، منوها)، قرمز `#E30613` فقط برای دکمه‌های اصلی

## Build (English)

Requirements: JDK 21, Android SDK platform 36, build-tools 37.0.0.

```bash
export JAVA_HOME=/path/to/jdk-21
export ANDROID_HOME=/path/to/android-sdk
./gradlew assembleDebug     # app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease   # app/build/outputs/apk/release/app-release-unsigned.apk
```

- Package: `dev.taekwondo.academy` • Version: 1.0.0 (code 1)
- compileSdk 36 • minSdk 24 • targetSdk 36
- Kotlin + Jetpack Compose Material 3, single module, no backend.
- Online classes use Jitsi Meet: the app opens `https://meet.jit.si/<room>` in the browser/Jitsi app.
- Technique videos open a YouTube search for the official Kukkiwon demonstration of that technique.

CI: `.github/workflows/build.yml` builds debug + release APKs on every push and uploads them as artifacts.
