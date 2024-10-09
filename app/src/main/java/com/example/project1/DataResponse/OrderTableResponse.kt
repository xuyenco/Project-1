package com.example.project1.DataResponse

import java.util.Date

//Get Table by orderId
data class TableResponseForOrderTable(
    val table_name: String,
    val location : String,
    val status : String
)

data class OrderTableResponse(
    val order_status : String,
    val order_created_at : Date,
    val tables : List<TableResponseForOrderTable>
)

//Get Order by TableID
data class OrderResponseForTableOrder(
    val status : String,
    val created_at : Date,
    val updated_at : Date
)

data class TableOrderResponse(
    val table_name : String,
    val table_location : String,
    val table_status : String,
    val orders : List<OrderResponseForTableOrder>
)