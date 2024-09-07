package com.example.project1.data

import java.time.chrono.ChronoLocalDateTime

data class Reservation(
    val id: Int,
    val dateTime: String,
    val name: String,
    val number : String
)
