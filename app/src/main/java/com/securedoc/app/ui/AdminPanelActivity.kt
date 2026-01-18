package com.securedoc.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.securedoc.app.databinding.ActivityAdminPanelBinding
import com.securedoc.app.util.Prefs
import com.securedoc.app.viewmodel.AdminViewModel

class AdminPanelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminPanelBinding
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var adapter: AdminStudentAdapter
    private val prefs by lazy { Prefs(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = AdminStudentAdapter { student ->
            viewModel.deleteStudent(student)
        }

        binding.studentsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.studentsRecyclerView.adapter = adapter

        binding.driveFolderInput.setText(prefs.driveFolderId)

        binding.saveFolderButton.setOnClickListener {
            val folderId = binding.driveFolderInput.text.toString().trim()
            if (folderId.isNotBlank()) {
                prefs.driveFolderId = folderId
                Toast.makeText(this, "Drive folder ID updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Folder ID cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.openDriveButton.setOnClickListener {
            val folderId = prefs.driveFolderId
            val url = "https://drive.google.com/drive/folders/$folderId"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        binding.refreshImagesButton.setOnClickListener {
            Toast.makeText(this, "Students will auto-refresh within 60 seconds", Toast.LENGTH_SHORT).show()
        }

        binding.createStudentButton.setOnClickListener {
            startActivity(Intent(this, CreateStudentActivity::class.java))
        }

        viewModel.students.observe(this) { students ->
            adapter.submitList(students)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadStudents()
    }
}
