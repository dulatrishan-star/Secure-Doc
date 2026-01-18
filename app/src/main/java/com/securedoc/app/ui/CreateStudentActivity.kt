package com.securedoc.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.securedoc.app.data.AppDatabase
import com.securedoc.app.data.StudentEntity
import com.securedoc.app.databinding.ActivityCreateStudentBinding
import kotlinx.coroutines.launch

class CreateStudentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateStudentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveStudentButton.setOnClickListener {
            val username = binding.studentUsernameInput.text.toString().trim()
            val password = binding.studentPasswordInput.text.toString().trim()
            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Username and password required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                AppDatabase.getInstance().studentDao()
                    .insert(StudentEntity(username = username, password = password))
                Toast.makeText(this@CreateStudentActivity, "Student created", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
