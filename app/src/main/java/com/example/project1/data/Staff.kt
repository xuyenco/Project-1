package com.example.project1.data

import java.util.Date

data class Staff(
    val staff_id: Int,
    val name: String,
    val gender: String,
    val birthday: String,
    val image_url: String,
    val phone: String,
    val citizen_id: String,
    val role : String,
    val salary : Double,
    val wage: Double,
    val username : String,
    val password_hash : String,
    val email : String,
    val create_at: Date,
    val update_at: Date
)
