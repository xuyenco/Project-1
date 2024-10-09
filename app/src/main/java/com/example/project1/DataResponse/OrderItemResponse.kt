package com.example.project1.DataResponse

import java.util.Date


// ItemResponse cho bảng nối OrderItem
data class ItemsResponseForItemOrder(
    val items_id: Int,
    val item_name: String,
    val quantity: Int,
    val description: String
)
//JoinTable OrderItem
data class OrderItemResponse(
    val orderId : Int,
    val items: List<ItemsResponseForItemOrder>
)

data class OrderResponseForItemOrder(
    val order_id : Int,
    val status : String,
    val created_at : Date,
    val updated_at : Date
)

data class ItemOrderResponse (
    val itemId : Int,
    val orders : List<OrderResponseForItemOrder>
)