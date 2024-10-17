package com.example.project1.retrofit.service

import com.example.project1.DataRequest.Tables_ReservationRequest
import com.example.project1.DataResponse.ReservationByTableIdResponse
import com.example.project1.DataResponse.TableByReservationIdResponse
import com.example.project1.DataResponse.TablesReservationAssignResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path

interface TableReservationService {
    @GET("api/reservationtable/table/{tableId}")
    suspend fun getReservationsByTableId(
        @Path("tableId") tableId: Int,
    ): ReservationByTableIdResponse

    @GET("api/reservationtable/reservation/{reservationId}")
    suspend fun getTablesByReservationId(
        @Path("reservationId") reservationId: Int,
    ): TableByReservationIdResponse

    @HTTP(method = "DELETE", path = "/api/reservationtable/remove", hasBody = true)
    suspend fun deleteTableReservation(
        @Body tableReservation: Tables_ReservationRequest
    ) : String
    @POST("api/reservationtable/assign")
    suspend fun addTableReservation(
        @Body tableReservationRequest: Tables_ReservationRequest
    )    : TablesReservationAssignResponse
}
