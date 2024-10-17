package com.example.project1.DataRequest

data class Orders_ItemsRequestBody(
    val items_id : Int,
    val orders_id : Int,
    val quantity: Int
)
data class Orders_ItemsRequest(
    val data : List<Orders_ItemsRequestBody>
)
