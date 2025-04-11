package com.example.degreeofburn.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = prefs.edit()


    companion object {
        const val PREF_NAME = "DegreeOfBurnPrefs"
        const val USER_TOKEN = "user_token"
        const val USER_ID = "user_id"
        const val IS_LOGGED_IN = "is_logged_in"
        const val LOGIN_TIME = "login_time"
        private const val ONE_YEAR_MS = 365L * 24 * 60 * 60 * 1000 // 1 tahun

    }

    fun saveAuthToken(token: String) {
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun saveUserId(userId: String) {
        editor.putString(USER_ID, userId)
        editor.apply()
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn)
        if (isLoggedIn) {
            editor.putLong(LOGIN_TIME, System.currentTimeMillis())
        } else {
            editor.remove(LOGIN_TIME)
        }
        editor.apply()
    }

    fun isSessionValid(): Boolean {
        val loginTime = prefs.getLong(LOGIN_TIME, 0L)
        val currentTime = System.currentTimeMillis()
        return isLoggedIn() && (currentTime - loginTime) <= ONE_YEAR_MS
    }


    fun getAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun getUserId(): String? {
        return prefs.getString(USER_ID, null)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }

    fun clearSession() {
        editor.clear()
        editor.apply()
    }


}