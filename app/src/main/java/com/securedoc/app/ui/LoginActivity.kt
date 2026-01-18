package com.securedoc.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.securedoc.app.data.AppDatabase
import com.securedoc.app.databinding.ActivityLoginBinding
import com.securedoc.app.util.AppConfig
import com.securedoc.app.util.Prefs
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val prefs by lazy { Prefs(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginAdminButton.setOnClickListener {
            val username = binding.usernameInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            if (username == AppConfig.ADMIN_USERNAME && password == AppConfig.ADMIN_PASSWORD) {
                startActivity(Intent(this, AdminPanelActivity::class.java))
            } else {
                Toast.makeText(this, "Invalid admin credentials", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginStudentButton.setOnClickListener {
            val username = binding.usernameInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Enter student credentials", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val student = AppDatabase.getInstance().studentDao()
                    .getByCredentials(username, password)
                if (student != null) {
                    prefs.loggedInStudent = student.username
                    startActivity(Intent(this@LoginActivity, StudentGalleryActivity::class.java))
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid student credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
