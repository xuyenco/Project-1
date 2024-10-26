package com.example.project1.data

import java.time.chrono.ChronoLocalDateTime
import java.util.Date

data class Reservation(
    val reservations_id : Int,
    val quantity : Int,
    val name : String,
    val phone: String,
    val email: String,
    val status: String,
    val time : Date,
    val created_at : Date,
    val updated_at : Date
)
