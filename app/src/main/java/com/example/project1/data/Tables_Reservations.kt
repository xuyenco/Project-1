package com.example.project1.data

import java.util.Date

data class Tables_Reservations(
    val tables_id: Int,
    val reservations_id: Int,
    val created_at: Date,
    val updated_at: Date
)