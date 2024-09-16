package com.example.project1.data

import org.w3c.dom.Text
import java.util.Date

data class Staff(
    val staff_id: Int,
    val name: String,
    val birthday: String,
    val image_url: String,
    val phone: String,
    val citizen_id: String,
    val role : String,
    val salary : Double,
    val wage: Double,
    val create_at: Date,
    val update_at: Date
)
