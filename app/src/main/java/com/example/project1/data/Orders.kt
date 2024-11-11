package com.example.project1.data

import java.util.Date

data class Orders(
    val orders_id: Int,
    val reservations_id : Int,
    var status : String,
    var description : String,
    val created_at : Date,
    val updated_at : Date
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Orders) return false

        return orders_id == other.orders_id
    }

    override fun hashCode(): Int {
        return orders_id.hashCode()
    }
}
