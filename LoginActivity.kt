package com.family.chat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.family.chat.utils.AuthHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var etUniqueId: EditText
    private lateinit var etPassword: EditText
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUniqueId = findViewById(R.id.etUniqueId)
        etPassword = findViewById(R.id.etPassword)
        progressBar = findViewById(R.id.progressBar)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val tvGoRegister: TextView = findViewById(R.id.tvGoRegister)

        btnLogin.setOnClickListener { attemptLogin() }
        tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        // إن كان المستخدم مسجلاً دخوله بالفعل، ننتقل مباشرة لقائمة العائلة
        if (AuthHelper.currentUserId() != null) {
            goToFamilyList()
        }
    }

    private fun attemptLogin() {
        val id = etUniqueId.text.toString().trim()
        val password = etPassword.text.toString()

        if (id.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "يرجى تعبئة جميع الحقول", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        AuthHelper.login(
            uniqueId = id,
            password = password,
            onSuccess = {
                setLoading(false)
                goToFamilyList()
            },
            onError = { message ->
                setLoading(false)
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun goToFamilyList() {
        startActivity(Intent(this, FamilyListActivity::class.java))
        finish()
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}
