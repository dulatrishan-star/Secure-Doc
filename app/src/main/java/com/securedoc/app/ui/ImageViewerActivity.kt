package com.securedoc.app.ui

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.securedoc.app.databinding.ActivityImageViewerBinding
import com.securedoc.app.drive.DriveServiceHelper
import com.securedoc.app.util.Prefs
import kotlinx.coroutines.launch

class ImageViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageViewerBinding
    private val prefs by lazy { Prefs(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fileId = intent.getStringExtra(EXTRA_FILE_ID)
        val fileName = intent.getStringExtra(EXTRA_FILE_NAME)
        binding.imageTitle.text = fileName ?: ""

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (fileId == null || account == null) {
            Toast.makeText(this, "Unable to load image", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.watermarkView.updateWatermark(prefs.loggedInStudent)

        lifecycleScope.launch {
            val helper = DriveServiceHelper(this@ImageViewerActivity, account)
            val bytes = helper.downloadImageBytes(fileId)
            if (bytes.isNotEmpty()) {
                Glide.with(this@ImageViewerActivity)
                    .load(bytes)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(false)
                    .into(binding.photoView)
            }
        }

        binding.closeButton.setOnClickListener { finish() }
    }

    companion object {
        const val EXTRA_FILE_ID = "extra_file_id"
        const val EXTRA_FILE_NAME = "extra_file_name"
    }
}
