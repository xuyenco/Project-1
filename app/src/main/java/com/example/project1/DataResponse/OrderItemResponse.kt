package com.example.project1.DataResponse

import java.util.Date


// ItemResponse cho bảng nối OrderItem
data class ItemsResponseForItemByOrder(
    val items_id: Int,
    val name: String,
    val image_url: String,
    val unit: String,
    val category : String,
    val price : Double,
    var quantity_used : Int,
    val created_at : Date,
    val updated_at : Date,
)
//JoinTable OrderItem
data class ItemByOrderIdResponse(
    val orders_id : Int,
    val reservations_id : Int,
    val status : String,
    val description : String,
    val created_at : Date,
    val updated_at : Date,
    val items: List<ItemsResponseForItemByOrder>
)

data class OrderResponseForOrderByItem(
    val orders_id : Int,
    val status : String,
    val description : String,
    val quantity_used : Int,
    val created_at : Date,
    val updated_at : Date,
)

data class OrderByItemIdResponse (
    val items_id : Int,
    val name : String,
    val image_url : String,
    val unit : String,
    val category : String,
    val price : Double,
    val created_at: Date,
    val updated_at: Date,
    val orders : List<OrderResponseForOrderByItem>
)