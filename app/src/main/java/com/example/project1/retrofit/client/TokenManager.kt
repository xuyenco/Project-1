package com.example.project1.retrofit.client

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    fun saveTokens(context: Context, accessToken: String, refreshToken: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("accessToken", accessToken)
            .putString("refreshToken", refreshToken)
            .apply()
    }

    fun getAccessToken(context: Context): String? {
        return context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)
            .getString("accessToken", null)
    }

    fun getRefreshToken(context: Context): String? {
        return context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)
            .getString("refreshToken", null)
    }
}