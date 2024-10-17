package com.example.project1.DataResponse

import com.example.project1.data.Orders
import com.example.project1.data.Tables
import java.util.Date

//Get Table by orderId
data class TableByOrderIdResponse(
    val orders_id: Int,
    var status : String,
    var description : String,
    val created_at : Date,
    val updated_at : Date,
    val tables : List<Tables>
)

data class OrderbyTableIdResponse(
    val tables_id : Int,
    val name: String,
    val quantity : Int,
    val location : String,
    val status : String,
    val created_at : Date,
    val updated_at : Date,
    val orders : List<Orders>
)