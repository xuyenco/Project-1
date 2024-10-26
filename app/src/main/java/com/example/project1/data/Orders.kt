package com.example.project1.data

import java.util.Date

data class Orders(
    val orders_id: Int,
    var status : String,
    val decription: String,
    val created_at : Date,
    val updated_at : Date
)
data class CreateOrderRequest(
    val description: String,
    val status: String
)
data class OrderItemsResponse(
    val orders_id: Int,
    val status: String,
    val description: String?,
    val created_at: Date,
    val updated_at: Date,
    val items: List<ItemsResponse>
)
data class OrderTableResponse(
    val tables_id: Int,
    val name: String,
    val quantity: Int,
    val location: String,
    val status: String,
    val created_at: String,
    val updated_at: String,
    val orders: List<Orders>
)