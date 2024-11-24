package com.example.project1.retrofit.service

import com.example.project1.data.Tables
import retrofit2.http.GET


interface TableService {
    @GET("/api/table/getall")
    suspend fun getAllTable(): List<Tables>
}