package com.example.project1.data

import java.util.Date

data class Orders_Items(
    val orders_items_id: Int,
    val items_id: Int,
    val orders_id: Int,
    val quantity: Int,
    val created_at : Date,
    val updated_at : Date
)
data class AssignOrderItemRequest(
    val items_id: Int,
    val orders_id: Int,
    val quantity: Int,
)