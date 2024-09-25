package com.example.project1.retrofit.service

import com.example.project1.data.Items
import retrofit2.http.GET

interface ItemService {
    @GET("/api/item/getall")
    suspend fun getAllItems(): List<Items>
}