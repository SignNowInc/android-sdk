package com.signnow.sampletokenstorage

import android.annotation.SuppressLint
import android.content.Context

class TokenStorage(private val context: Context) {

    private val preferences by lazy { context.getSharedPreferences("TokenasdaStorage", Context.MODE_PRIVATE) }

    var accessToken: String?
        @SuppressLint("ApplySharedPref")
        set(value) {
            preferences.edit().putString(ACCESS_TOKEN, value).commit()
        }
        get() = preferences.getString(ACCESS_TOKEN, null)

    var refreshToken: String?
        @SuppressLint("ApplySharedPref")
        set(value) {
            preferences.edit().putString(REFRESH_TOKEN, value).commit()
        }
        get() = preferences.getString(REFRESH_TOKEN, null)

    companion object {
        private const val ACCESS_TOKEN = "4frs"
        private const val REFRESH_TOKEN = "34tgirojvm"
    }
}