package com.example.project1.retrofit.service

import com.example.project1.DataResponse.OrderbyTableIdResponse
import com.example.project1.DataResponse.TableByOrderIdResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface OrderTableService {
    //get Table by orderId
    @GET("/api/ordertable/order/{orderId}")
    suspend fun getTableByOrderId(@Path("orderId") orderId: Int): TableByOrderIdResponse

    //get Order by tableID
    @GET("/api/ordertable/table/{tableId}")
    suspend fun getOrderByTableId(@Path("tableId") tableId: Int): OrderbyTableIdResponse
}