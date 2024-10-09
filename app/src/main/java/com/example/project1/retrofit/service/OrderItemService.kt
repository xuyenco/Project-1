package com.example.project1.retrofit.service

import com.example.project1.DataResponse.ItemOrderResponse
import com.example.project1.DataResponse.OrderItemResponse
import retrofit2.http.GET
import retrofit2.http.Path

//JoinTableOrderITem
interface OrderItemService {
    //get Item by orderId
    @GET("api/orderitem/order/{orderId}")
    suspend fun getItemByOrderId(@Path("orderId") orderId: Int): OrderItemResponse

    //get Order by itemId
    @GET("api/orderitem/item/{itemId}")
    suspend fun getOrderByItemId(@Path("itemId") itemId: Int): ItemOrderResponse


}