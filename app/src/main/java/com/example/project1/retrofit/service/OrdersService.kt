package com.example.project1.retrofit.service

import com.example.project1.data.AssignOrderItemRequest
import com.example.project1.data.BillRequest
import com.example.project1.data.CreateOrderRequest
import com.example.project1.data.OrderItemsResponse
import com.example.project1.data.Orders
import com.example.project1.data.Orders_Items
import com.example.project1.data.Orders_Tables
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OrdersService {
    @GET("/api/order/getall")
    suspend fun getAllOrders(): List<Orders>
    @GET("/api/order/reservation/{reservationId}")
    suspend fun getOrderIdByReservationId(
        @Path("reservationId") reservationId: Int
    ): Orders

    @GET("api/orderitem/order/{id}")
    suspend fun getOrderItems(
        @Path("id") orderId: Int
    ): OrderItemsResponse
    @POST("api/order")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<Orders>
    @POST("api/ordertable/assign")
    suspend fun assignOrder(@Body assignOrderRequest: Orders_Tables): Response<Unit>
    @POST("api/orderitem/assign")
    suspend fun assignItemsToOrder(@Body assignOrderRequest: List<AssignOrderItemRequest>): Response<List<Orders_Items>>
    @POST("/api/bill")
    suspend fun createBill(@Body billRequest: BillRequest): Response<Unit>
}