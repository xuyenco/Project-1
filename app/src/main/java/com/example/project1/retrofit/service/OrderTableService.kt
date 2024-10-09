package com.example.project1.retrofit.service

import com.example.project1.DataResponse.OrderTableResponse
import com.example.project1.DataResponse.TableOrderResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface OrderTableService {
    //get Table by orderId
    @GET("/api/ordertable/order/{orderId}")
    suspend fun getTableByOrderId(@Path("orderId") orderId: Int): OrderTableResponse

    //get Order by tableID
    @GET("/api/ordertable/table/{tableId}")
    suspend fun getOrderByTableId(@Path("tableId") tableId: Int): TableOrderResponse
}