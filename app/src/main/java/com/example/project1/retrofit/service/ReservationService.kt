package com.example.project1.retrofit.service

import com.example.project1.DataRequest.ReservationRequest
import com.example.project1.data.Reservation
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReservationService {
    // Lấy tất cả các Reservation
    @GET("/api/reservation/getall")
    suspend fun getAllReservation(): List<Reservation>

    // Lấy một Reservation theo id
    @GET("/api/reservation/get/{id}")
    suspend fun getReservationById(@Path("id") id: Int): Reservation

    // Tạo mới một Reservation
    @POST("/api/reservation")
    suspend fun createReservation(@Body reservationRequest: ReservationRequest): Reservation

    // Chỉnh sửa một Reservation theo id
    @PATCH("/api/reservation/{id}")
    suspend fun editReservation(@Body reservation: ReservationRequest, @Path("id") id: Int): Reservation

    // Xoá một Reservation theo id
    @DELETE("/api/reservation/{id}")
    suspend fun deleteReservation(@Path("id") id: Int) : String
}
