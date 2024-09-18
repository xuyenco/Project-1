package com.example.project1.data

import java.util.Date

data class Items(
    val items_id : Int,
    val name : String,
    val image_url : String,
    val quantity : Int,
    val unit : String,
    val category : String,
    val price : Number,
    val created_at : Date,
    val updated_at : Date
)
