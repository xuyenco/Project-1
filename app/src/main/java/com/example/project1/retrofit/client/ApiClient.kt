package com.example.project1.retrofit.client

import com.example.project1.retrofit.service.ItemService
import com.example.project1.retrofit.service.OrdersService
import com.example.project1.retrofit.service.ReservationService
import com.example.project1.retrofit.service.ReservationTableService
import com.example.project1.retrofit.service.TableService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
//    private const val BASE_URL = "http://192.168.59.1:5001"
    private const val BASE_URL = "http://192.168.168.1:5001"
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object ApiClient {
    val tableService: TableService by lazy {
        RetrofitClient.retrofit.create(TableService::class.java)
    }
    val reservationService: ReservationService by lazy {
        RetrofitClient.retrofit.create(ReservationService::class.java)
    }
    val orderService : OrdersService by lazy {
        RetrofitClient.retrofit.create(OrdersService::class.java)
    }
    val itemService : ItemService by lazy {
        RetrofitClient.retrofit.create(ItemService::class.java)
    }
    val reservationTableService : ReservationTableService by lazy {
        RetrofitClient.retrofit.create(ReservationTableService::class.java)
    }
}
