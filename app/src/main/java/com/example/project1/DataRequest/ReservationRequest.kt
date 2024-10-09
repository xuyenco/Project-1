package com.example.project1.DataRequest

import java.util.Date

data class ReservationRequest(
    val name: String,
    val quantity: Int,
    val phone: String,
    val email: String,
    val time: Date
)
