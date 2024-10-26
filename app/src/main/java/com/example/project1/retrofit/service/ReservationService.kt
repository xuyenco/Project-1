package com.example.project1.retrofit.service

import com.example.project1.data.Reservation
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ReservationService {
    @GET("/api/reservation/getall")
    suspend fun getAllReservation(): List<Reservation>
    @PATCH("api/reservation/{reservationId}")
    suspend fun updateReservationStatus(
        @Path("reservationId") reservationId: Int,
        @Body statusUpdate: Map<String, String> // Body với status mới
    ): Response<Void>
}