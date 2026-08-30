# تطبيق مراسلة العائلة (FamilyChat)

تطبيق أندرويد كامل (Kotlin) للتراسل الفوري بين أفراد العائلة، مع **تسجيل عبر معرّف خاص (Unique ID)** بدلاً من رقم الهاتف.

## كيف يعمل نظام المعرّف الفريد؟
المستخدم يختار معرّفاً مثل `ahmad123` بدل رقم الهاتف. داخلياً، يُحوَّل هذا المعرّف تلقائياً إلى بريد وهمي (`ahmad123@familychat.local`) يُستخدم مع Firebase Authentication — لكن المستخدم لا يرى ولا يتعامل إلا مع معرّفه الخاص.

---

## خطوات التشغيل (مطلوبة قبل أن يعمل التطبيق)

### 1. تثبيت الأدوات
- ثبّت **Android Studio** (أحدث إصدار) من الموقع الرسمي.

### 2. إنشاء مشروع Firebase (مجاني)
1. اذهب إلى https://console.firebase.google.com
2. أنشئ مشروعاً جديداً (Add project) وسمّه مثلاً `FamilyChat`.
3. من القائمة الجانبية: **Build > Authentication > Get started** → فعّل طريقة **Email/Password**.
4. من القائمة الجانبية: **Build > Firestore Database > Create database** → اختر وضع **Production mode** ثم أي منطقة قريبة منك.
5. اذهب إلى إعدادات المشروع (⚙️ Project settings) → بند **Your apps** → اضغط أيقونة أندرويد لإضافة تطبيق.
   - Package name: `com.family.chat` (يجب أن يطابق تماماً)
6. حمّل ملف **`google-services.json`** الذي يظهر لك.
7. ضع هذا الملف داخل مجلد `app/` في هذا المشروع (بجانب `build.gradle` الخاص بالتطبيق).

### 3. قواعد أمان Firestore
في تبويب **Firestore Database > Rules** استبدل القواعد بما يلي (تسمح فقط للمستخدمين المسجّلين بالقراءة/الكتابة):

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    match /chats/{chatId}/messages/{messageId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### 4. فتح المشروع وبناء الـ APK
1. افتح Android Studio → **Open** → اختر مجلد `FamilyChat` (هذا المشروع).
2. انتظر حتى ينتهي Gradle Sync تلقائياً (يحتاج إنترنت لتحميل المكتبات أول مرة).
3. من القائمة: **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
4. بعد الانتهاء، اضغط الرابط الذي يظهر (locate) لإيجاد ملف الـ APK داخل:
   `app/build/outputs/apk/debug/app-debug.apk`
5. انسخ هذا الملف لهاتفك وثبّته (فعّل "السماح بتثبيت من مصادر غير معروفة" إن طُلب).

---

## هيكل المشروع
```
FamilyChat/
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/family/chat/
│       │   ├── LoginActivity.kt        # تسجيل الدخول بالمعرّف
│       │   ├── RegisterActivity.kt     # إنشاء حساب بمعرّف جديد
│       │   ├── FamilyListActivity.kt   # قائمة أفراد العائلة
│       │   ├── ChatActivity.kt         # شاشة الدردشة الفورية
│       │   ├── models/                 # User, Message
│       │   ├── adapters/               # RecyclerView Adapters
│       │   └── utils/AuthHelper.kt     # منطق المعرّف الفريد + Firebase
│       └── res/                        # الواجهات والألوان
├── build.gradle
└── settings.gradle
```

## أفكار لتطوير التطبيق لاحقاً
- إضافة صور وملفات في الدردشة (Firebase Storage).
- مجموعات عائلية (Group Chat) بدل الدردشة الفردية فقط.
- إشعارات فورية (Firebase Cloud Messaging).
- صورة شخصية (Avatar) حقيقية بدل الحرف الأول من الاسم.
- تشفير الرسائل من طرف لطرف.

## البناء من الهاتف عبر GitHub Actions (بدون Android Studio)

يحتوي المشروع على ملف جاهز: `.github/workflows/build.yml` يجعل GitHub يبني الـ APK تلقائياً على خوادمه في كل مرة ترفع فيها الكود. كل ما تحتاجه من الهاتف هو رفع الملفات فقط.

### الخطوات:

1. **أنشئ حساب GitHub** (إن لم يكن لديك) من https://github.com — يمكن من متصفح الهاتف مباشرة.

2. **أنشئ مستودع (Repository) جديد:**
   - اضغط **+** أعلى الصفحة → **New repository**
   - اسمه مثلاً `FamilyChat`
   - اجعله **Private** (مهم، لأن الكود سيحتوي لاحقاً بيانات حساسة)
   - اضغط **Create repository**

3. **ضع ملف `google-services.json` في مكانه أولاً**
   قبل الرفع، تأكد من نسخ الملف الذي حمّلته من Firebase إلى داخل مجلد `FamilyChat/app/` على هاتفك (باستخدام أي تطبيق لإدارة الملفات).

4. **ارفع المشروع كاملاً:**
   - **من متصفح الهاتف:** في صفحة المستودع اضغط **Add file → Upload files**، ثم اختر جميع ملفات ومجلدات `FamilyChat` وارفعها (بعض المتصفحات تسمح برفع مجلد كامل بالسحب).
   - **أو باستخدام تطبيق GitHub الرسمي / تطبيق مثل Working Copy أو Termux:** أسهل لرفع مجلدات متداخلة كاملة دفعة واحدة.
   - تأكد أن هيكل المجلدات يبقى كما هو (لا ترفع الملفات مبعثرة خارج مجلداتها).

5. **البناء يبدأ تلقائياً:**
   بمجرد اكتمال الرفع، اذهب لتبويب **Actions** أعلى صفحة المستودع — ستجد عملية بناء تعمل تلقائياً (باسم "Build APK"). انتظر حتى تظهر علامة ✅ خضراء (يأخذ عادة 2-4 دقائق).

6. **تحميل الـ APK:**
   بعد اكتمال البناء بنجاح، افتح تلك العملية (الصف الذي ظهر) → انزل لأسفل الصفحة إلى قسم **Artifacts** → اضغط على `FamilyChat-debug-apk` لتحميله كملف zip يحتوي على `app-debug.apk`.

7. **تثبيته على هاتفك:**
   فك ضغط الملف واضغط على `app-debug.apk` لتثبيته (فعّل "السماح بالتثبيت من مصادر غير معروفة" إذا طُلب منك ذلك).

> بهذه الطريقة، أنت فقط ترفع الملفات من هاتفك — والبناء الفعلي يحدث بالكامل على خوادم GitHub المجانية، دون حاجة لتثبيت Android Studio أو Gradle على جهازك.

---

## ملاحظة مهمة
بدون وضع `google-services.json` الصحيح في مجلد `app/`، لن يعمل التطبيق (سيتوقف عند بدء التشغيل). هذه الخطوة إلزامية وتأخذ حوالي 5 دقائق فقط.
