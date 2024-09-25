package com.example.project1.data

import java.util.Date

data class Bill(
    val bill_id : Int,
    val total : Double,
    val orders_id : Int ,
    val staff_id : Int,
    val created_at : Date,
    val updated_at : Date
)