package com.securedoc.app.drive

import android.content.Context
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class DriveServiceHelper(
    context: Context,
    account: GoogleSignInAccount
) {
    private val drive: Drive

    init {
        // Use Google Sign-In credentials for Drive API access.
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            listOf(DriveScopes.DRIVE_READONLY)
        ).apply {
            selectedAccount = account.account
        }

        drive = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("SecureDoc").build()
    }

    suspend fun listImages(folderId: String): List<DriveImage> = withContext(Dispatchers.IO) {
        val query = "'$folderId' in parents and mimeType contains 'image/' and trashed=false"
        val result = drive.files().list()
            .setQ(query)
            .setFields("files(id,name,mimeType)")
            .execute()

        result.files?.map { file ->
            DriveImage(
                id = file.id,
                name = file.name,
                mimeType = file.mimeType
            )
        } ?: emptyList()
    }

    suspend fun downloadImageBytes(fileId: String): ByteArray = withContext(Dispatchers.IO) {
        // Download to memory only; no disk persistence.
        try {
            val outputStream = ByteArrayOutputStream()
            drive.files().get(fileId).executeMediaAndDownloadTo(outputStream)
            outputStream.toByteArray()
        } catch (error: GoogleJsonResponseException) {
            ByteArray(0)
        }
    }
}
