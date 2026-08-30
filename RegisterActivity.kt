package com.family.chat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.family.chat.utils.AuthHelper

class RegisterActivity : AppCompatActivity() {

    private lateinit var etDisplayName: EditText
    private lateinit var etUniqueId: EditText
    private lateinit var etPassword: EditText
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etDisplayName = findViewById(R.id.etDisplayName)
        etUniqueId = findViewById(R.id.etUniqueId)
        etPassword = findViewById(R.id.etPassword)
        progressBar = findViewById(R.id.progressBar)
        val btnRegister: Button = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener { attemptRegister() }
    }

    private fun attemptRegister() {
        val name = etDisplayName.text.toString().trim()
        val id = etUniqueId.text.toString().trim()
        val password = etPassword.text.toString()

        if (name.isEmpty() || id.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "يرجى تعبئة جميع الحقول", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 6) {
            Toast.makeText(this, "كلمة المرور يجب أن تكون 6 أحرف على الأقل", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        AuthHelper.register(
            uniqueId = id,
            password = password,
            displayName = name,
            onSuccess = {
                setLoading(false)
                Toast.makeText(this, "تم إنشاء الحساب بنجاح", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, FamilyListActivity::class.java))
                finish()
            },
            onError = { message ->
                setLoading(false)
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}
