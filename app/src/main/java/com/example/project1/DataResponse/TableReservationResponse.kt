package com.example.project1.DataResponse

import com.example.project1.data.Reservation
import com.example.project1.data.Tables
import com.example.project1.data.Tables_Reservations
import java.util.Date


//get Reservation by tableID

data class ReservationByTableIdResponse(
    val tables_id : Int,
    val name: String,
    val quantity : Int,
    val location : String,
    val status : String,
    val created_at : Date,
    val updated_at : Date,
    val reservations : List<Reservation>
)

//Get Table by orderID

data class TableByReservationIdResponse(
    val reservations_id : Int,
    val quantity : Int,
    val name : String,
    val phone: String,
    val email: String,
    val status : String,
    val time : Date,
    val created_at : Date,
    val updated_at : Date,
    val tables : List<Tables>
)

data class TablesReservationAssignResponse(
    val message : String,
    val table : Tables,
    val reservation : Reservation,
    val relation : Tables_Reservations
)




