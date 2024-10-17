package com.example.project1.data

import java.util.Date

data class Tables(
    val tables_id : Int,
    val name: String,
    val quantity : Int,
    val location : String,
    val status : String,
    val created_at : Date,
    val updated_at : Date
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Tables) return false

        return tables_id == other.tables_id
    }

    override fun hashCode(): Int {
        return tables_id.hashCode()
    }
}
