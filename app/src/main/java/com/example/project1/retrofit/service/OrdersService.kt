package com.example.project1.retrofit.service

import com.example.project1.DataRequest.OrderRequest
import com.example.project1.DataRequest.Orders_ItemsRequest
import com.example.project1.DataRequest.ReservationRequest
import com.example.project1.data.Items
import com.example.project1.data.Orders
import com.example.project1.data.Orders_Items
import com.example.project1.data.Reservation
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OrdersService {
    @GET("/api/order/getall")
    suspend fun getAllOrders(): List<Orders>
    @GET("/api/order/get/{id}")
    suspend fun getOrderById(@Path("id") id: Int): Orders

    @POST("/api/order/create")
    suspend fun createOrder(@Body orderRequest: OrderRequest): Orders

    @PATCH("/api/order/{id}")
    suspend fun editOrder(@Body orders: OrderRequest, @Path("id") id: Int): Orders

    @DELETE("/api/order/{id}")
    suspend fun deleteOrder(@Path("id") id: Int) : String

    //join ItemTable
    @POST("api/order/joinitem/{OrderId}")
    suspend fun createJoinItemTable(
        @Path("OrderId") orderId: Int,
        @Body orders_ItemsRequest: Orders_ItemsRequest)
    : List<Orders_Items>
    @GET("/api/order/joinitem/{OrderId}")
    suspend fun getItemByOrderId(
        @Path("OrderId") orderId: Int
    ) : List<Items>

}