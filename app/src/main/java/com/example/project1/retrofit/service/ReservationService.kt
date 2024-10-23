package com.example.project1.retrofit.service

import com.example.project1.data.Reservation
import retrofit2.http.GET
import retrofit2.http.Path

interface ReservationService {
    @GET("/api/reservation/getall")
    suspend fun getAllReservation(): List<Reservation>
}