package com.example.project1.retrofit.client

import android.content.Context
import android.content.SharedPreferences
object TokenManager {
    private lateinit var sharedPreferences: SharedPreferences

    // Hàm khởi tạo để nhận context và tạo sharedPreferences
    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit()
            .putString("accessToken", accessToken)
            .putString("refreshToken", refreshToken)
            .apply()
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("accessToken", null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refreshToken", null)
    }
}
