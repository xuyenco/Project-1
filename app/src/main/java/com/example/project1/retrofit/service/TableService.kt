package com.example.project1.retrofit.service

import com.example.project1.data.Tables
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface TableService {
    @GET("/api/table/getall")
    suspend fun getAllTable(): List<Tables>
}