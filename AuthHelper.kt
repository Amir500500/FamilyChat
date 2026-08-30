package com.family.chat.utils

import com.family.chat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * يسمح هذا التطبيق بتسجيل الدخول عبر "معرّف فريد" (Unique ID) يختاره المستخدم
 * بدلاً من رقم الهاتف. نظراً لأن Firebase Authentication يعتمد داخلياً على
 * البريد الإلكتروني وكلمة المرور، نقوم بتحويل المعرّف الفريد إلى بريد إلكتروني
 * وهمي داخلي (مثال: ahmad123 -> ahmad123@familychat.local) بشكل شفاف تماماً
 * عن المستخدم، الذي لا يرى أو يستخدم إلا معرّفه الخاص وكلمة المرور.
 */
object AuthHelper {

    private const val EMAIL_DOMAIN = "@familychat.local"

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun idToEmail(uniqueId: String): String {
        return uniqueId.trim().lowercase() + EMAIL_DOMAIN
    }

    fun isValidUniqueId(id: String): Boolean {
        // أحرف إنجليزية وأرقام و "_" فقط، من 3 إلى 20 حرفاً
        val regex = Regex("^[a-zA-Z0-9_]{3,20}$")
        return regex.matches(id)
    }

    fun register(
        uniqueId: String,
        password: String,
        displayName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isValidUniqueId(uniqueId)) {
            onError("المعرّف يجب أن يكون من 3-20 حرفاً إنجليزياً أو رقماً فقط (بدون مسافات)")
            return
        }

        val email = idToEmail(uniqueId)

        // تحقق أولاً إن كان المعرّف مستخدماً من قبل
        firestore.collection("users")
            .whereEqualTo("uniqueId", uniqueId.lowercase())
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    onError("هذا المعرّف مستخدم بالفعل، اختر معرّفاً آخر")
                    return@addOnSuccessListener
                }

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        val uid = result.user?.uid ?: return@addOnSuccessListener
                        val user = User(
                            uid = uid,
                            uniqueId = uniqueId.lowercase(),
                            displayName = displayName,
                            createdAt = System.currentTimeMillis()
                        )
                        firestore.collection("users").document(uid)
                            .set(user)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e -> onError(e.localizedMessage ?: "خطأ في إنشاء الحساب") }
                    }
                    .addOnFailureListener { e -> onError(e.localizedMessage ?: "تعذر إنشاء الحساب") }
            }
            .addOnFailureListener { e -> onError(e.localizedMessage ?: "خطأ في الاتصال") }
    }

    fun login(
        uniqueId: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val email = idToEmail(uniqueId)
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.localizedMessage ?: "معرّف أو كلمة مرور غير صحيحة") }
    }

    fun currentUserId(): String? = auth.currentUser?.uid

    fun logout() {
        auth.signOut()
    }

    // ينشئ معرّف محادثة ثابت وفريد بين شخصين بغض النظر عن ترتيبهما
    fun buildChatId(uidA: String, uidB: String): String {
        return if (uidA < uidB) "${uidA}_${uidB}" else "${uidB}_${uidA}"
    }
}
