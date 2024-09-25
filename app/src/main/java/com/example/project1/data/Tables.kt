package com.example.project1.data

import java.util.Date

data class Tables(
    val tables_id : Int,
    val name: String,
    val quantity : Int,
    val location : String,
    val status : String,
    val create_at : Date,
    val update_at : Date
)
