package com.example.project1.retrofit.service

import com.example.project1.data.Orders
import retrofit2.http.GET

interface OrdersService {
    @GET("/api/order/getall")
    suspend fun getAllOrders(): List<Orders>
}