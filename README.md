# SecureDoc – Aviation Security Image Viewer

## Overview
SecureDoc is a Kotlin + XML Android application for securely viewing aviation security training images stored in a restricted Google Drive folder. Students can only view images inside the app with watermarking, screenshot blocking, and automatic updates.

## Features
- **Admin login** (local, hardcoded): `admin / Airbus@24`
- **Student accounts** stored locally with Room (created by admin)
- **Google Sign-In** with **Google Drive API** access (no Firebase)
- **Restricted Drive folder** (viewer access to student Gmail accounts)
- **Auto-refresh every 60 seconds** to pull new images
- **Full-screen viewer** with pinch zoom (PhotoView)
- **Watermark overlay** with username + timestamp, updated every minute
- **No downloads, no sharing, no screenshots**

## Configuration
### Drive folder ID
Update the folder ID in the Admin Panel or edit the default in:
- `AppConfig.DEFAULT_DRIVE_FOLDER_ID` in `AppConfig.kt`

The Drive query used is:
```
'<DRIVE_FOLDER_ID>' in parents and mimeType contains 'image/' and trashed=false
```

## Google Cloud Console Setup
1. Create or select a Google Cloud project.
2. Enable **Google Drive API**.
3. Configure the **OAuth consent screen**.
4. Create an **OAuth 2.0 Client ID** for Android.
5. Add your app package name: `com.securedoc.app`.
6. Add the SHA-1 of your debug/release keystore.
7. Download or copy the **Web Client ID** if needed.

## Testing with Restricted Drive Folder
1. Create a folder in Google Drive.
2. Set access to **Restricted**.
3. Share the folder to student Gmail accounts (Viewer).
4. Paste the folder ID into the Admin Panel or `AppConfig.DEFAULT_DRIVE_FOLDER_ID`.
5. Login as a student, then Google Sign-In with the shared Gmail account.

## Notes
- Images are loaded via authenticated Drive API calls.
- Disk caching is disabled for Glide to avoid persistent storage.
- Screen capture is blocked via `FLAG_SECURE` in student screens.
