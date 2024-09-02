package com.example.project1.data

data class Bill(
    val idBill : Int,
    val totalMoney : Double,
    val paymentMethod : String,
    val dateTime : String,
    val idOrder: Int,
    val idStaff: Int
)
