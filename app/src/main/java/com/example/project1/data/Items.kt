package com.example.project1.data

import java.util.Date

data class Items(
    val items_id : Int,
    val name : String,
    val image_url : String,
    val unit : String,
    val category : String,
    val price : Int,
    val created_at : Date,
    val updated_at : Date
)
data class ItemsResponse(
    val items_id: Int,
    val name: String,
    val image_url: String,
    val unit: String,
    val category: String,
    val price: Int,
    val created_at: Date,
    val updated_at: Date,
    val quantity_used: Int
)