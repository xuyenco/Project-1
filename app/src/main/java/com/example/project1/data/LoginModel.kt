package com.example.project1.data

data class LoginResponse(
    val id: Int,
    val email: String,
    val username: String,
    val role: String,
    val accessToken: String,
    val refreshToken: String
)