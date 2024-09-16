package com.example.project1.data

import java.util.Date

data class Orders_Items(
    val items_id: Int,
    val orders_id: Int,
    val quantity: Int,
    val description : String,
    val created_at : Date,
    val updated_at : Date
)
