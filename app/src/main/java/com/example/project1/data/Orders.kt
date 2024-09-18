package com.example.project1.data

import java.util.Date

data class Orders(
    val orders_id: Int,
    var status : Boolean,
    val created_at : Date,
    val updated_at : Date
)
