package com.example.project1.retrofit.service

import com.example.project1.data.Tables_Reservations
import retrofit2.http.GET
import retrofit2.http.Path

interface ReservationTableService {
    @GET("/api/reservationtable/getall")
    suspend fun getReservationTable(): List<Tables_Reservations>
}