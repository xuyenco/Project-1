package com.example.project1.retrofit.service

import com.example.project1.data.Reservation
import com.example.project1.data.Tables
import retrofit2.http.GET
import retrofit2.http.Path


interface TableService {
    @GET("/api/table/getall")
    suspend fun getAllTable(): List<Tables>
    @GET("/api/reservation/jointable/{reservationId}")
    suspend fun getTableByReservationId (
        @Path("reservationId") reservationId: Int
    ) : List<Tables>
}