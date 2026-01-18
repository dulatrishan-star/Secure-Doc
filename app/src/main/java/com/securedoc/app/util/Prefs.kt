package com.securedoc.app.util

import android.content.Context

class Prefs(context: Context) {
    private val prefs = context.getSharedPreferences(AppConfig.PREFS_NAME, Context.MODE_PRIVATE)

    var driveFolderId: String
        get() = prefs.getString(AppConfig.KEY_DRIVE_FOLDER_ID, AppConfig.DEFAULT_DRIVE_FOLDER_ID)
            ?: AppConfig.DEFAULT_DRIVE_FOLDER_ID
        set(value) = prefs.edit().putString(AppConfig.KEY_DRIVE_FOLDER_ID, value).apply()

    var loggedInStudent: String
        get() = prefs.getString(AppConfig.KEY_LOGGED_IN_STUDENT, "") ?: ""
        set(value) = prefs.edit().putString(AppConfig.KEY_LOGGED_IN_STUDENT, value).apply()
}
