package com.example.project1.retrofit.service

import com.example.project1.DataRequest.LoginRequest
import com.example.project1.data.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthService {
    @POST("/api/auth/login")  // Đường dẫn endpoint của login
    suspend fun login(@Body request : LoginRequest): Response<LoginResponse>
    @PUT("/api/auth/refresh")
    suspend fun refreshToken(): Response<LoginResponse>
}