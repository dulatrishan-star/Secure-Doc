package com.securedoc.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.securedoc.app.databinding.ActivityStudentGalleryBinding
import com.securedoc.app.drive.DriveServiceHelper
import com.securedoc.app.util.Prefs
import com.securedoc.app.viewmodel.StudentGalleryViewModel

class StudentGalleryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentGalleryBinding
    private val viewModel: StudentGalleryViewModel by viewModels()
    private val prefs by lazy { Prefs(this) }
    private lateinit var googleSignInClient: GoogleSignInClient
    private var driveHelper: DriveServiceHelper? = null

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            onGoogleSignedIn(account)
        } catch (error: ApiException) {
            Toast.makeText(this, "Google Sign-In required", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        binding = ActivityStudentGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(com.google.android.gms.common.api.Scope("https://www.googleapis.com/auth/drive.readonly"))
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, signInOptions)

        val adapter = GalleryAdapter(
            onItemClick = { image ->
                val intent = Intent(this, ImageViewerActivity::class.java)
                intent.putExtra(ImageViewerActivity.EXTRA_FILE_ID, image.id)
                intent.putExtra(ImageViewerActivity.EXTRA_FILE_NAME, image.name)
                startActivity(intent)
            },
            onLoadImage = { fileId ->
                driveHelper?.downloadImageBytes(fileId) ?: ByteArray(0)
            },
            watermarkProvider = { prefs.loggedInStudent }
        )

        binding.galleryRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.galleryRecyclerView.adapter = adapter

        viewModel.images.observe(this) { images ->
            adapter.submitList(images)
        }

        binding.signOutButton.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener {
                finish()
            }
        }

        maybeStartGoogleSignIn()
    }

    private fun maybeStartGoogleSignIn() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null) {
            signInLauncher.launch(googleSignInClient.signInIntent)
        } else {
            onGoogleSignedIn(account)
        }
    }

    private fun onGoogleSignedIn(account: GoogleSignInAccount) {
        driveHelper = DriveServiceHelper(this, account)
        val folderId = prefs.driveFolderId
        viewModel.startPolling(driveHelper!!, folderId)
    }
}
