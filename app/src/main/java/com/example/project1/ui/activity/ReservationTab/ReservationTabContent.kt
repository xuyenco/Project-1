package com.example.project1.ui.activity.ReservationTab

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.project1.DataRequest.ReservationRequest
import com.example.project1.DataRequest.Tables_ReservationRequest
import com.example.project1.data.Reservation
import com.example.project1.data.Tables
import com.example.project1.retrofit.client.ApiClient
import com.example.project1.ui.activity.deleteTableReservation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ReservationTabContent(
    tablesLists: List<Tables>,
    tableReservationsList : SnapshotStateMap<Tables, SnapshotStateList<Reservation>>,
    inputTime: String,
    selectedTableIds : SnapshotStateList<Int?>,
    selectedReservation: Reservation?,
    allReservation: SnapshotStateList<Reservation>,
    onInputTimeChange :(String) -> Unit,
    onShowPopupChange: (Boolean) -> Unit,
    onShowDatePickerChange : (Boolean) -> Unit,
    onShowTimePickerChange : (Boolean) -> Unit,
    onSelectedReservationChange: (Reservation?) -> Unit,)
{
    var selectedTableId by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current


    Row(modifier = Modifier.fillMaxSize()) {
        //Phần 1: 2/3 bên tra
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            ContentLeft(
                onTableClick = {
                        tableId ->
                               if (tableId == selectedTableId) {
                                   selectedTableId = null
                               } else {
                                   selectedTableId = tableId
                               }
                },
                tablesLists,
                onShowPopup = { onShowPopupChange(true) },
                tableReservationsList,
                inputTime = inputTime,
                onInputTimeChange = onInputTimeChange,
                onShowDatePickerChange = { onShowDatePickerChange(true) },
                onShowTimePickerChange = { onShowTimePickerChange(true) },
                selectedTableIds)

        }

        // Phần 2: 1/3 bên phải
        Column(
            modifier = Modifier
                .weight(1f) // Tỷ lệ phần này chiếm 1/3 màn hình
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            ContentRight(
                selectedTableId,// Truyền selectedTableId vào ContentRight
                tableReservationsList,
                allReservation,
                onEditClick = {
                        reservation -> onSelectedReservationChange(reservation) // Update the selected reservation
                    onShowPopupChange(true) // Show the popup
                },
                onDeleteClick = {
                        reservation ->
                    val id = reservation.reservations_id
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val tablesByReservationId = ApiClient.tableReservationsService.getTablesByReservationId(id)
                            if (tablesByReservationId.tables.isNotEmpty()){
                                for (table in tablesByReservationId.tables){
                                    val tableReservation = Tables_ReservationRequest(
                                        reservations_id = id,   // Đảm bảo id là id của reservation
                                        tables_id = table.tables_id // Đảm bảo table.tables_id là id của bàn
                                    )
                                    tableReservationsList.keys.filter { it.tables_id == table.tables_id }.forEach {
                                        tableReservationsList[it]!!.remove(selectedReservation) }

                                    deleteTableReservation(tableReservation)
                                }
                            }
                            else{
                                Log.e("Error at get Table","Shit go wrong")
                            }
                            ApiClient.reservationService.deleteReservation(id)

                            withContext(Dispatchers.Main) {
                                onSelectedReservationChange(null)
                            }

                        } catch (e: Exception) {
                            Log.e("Reservation", "Error: I have no Ideal ${e.message}")
                        }
                    }
                },
                onStatusChange = { updatedReservation ->
                    // Cập nhật reservation mới trong danh sách tableReservationsList
                    val selectedTable = tableReservationsList.keys.find { it.tables_id == selectedTableId }
                    selectedTable?.let { table ->
                        val index = tableReservationsList[table]?.indexOfFirst { it.reservations_id == updatedReservation.reservations_id }
                        if (index != null && index >= 0) {
                            tableReservationsList[table]?.set(index, updatedReservation)
                        }
                        val updateReservationRequest = ReservationRequest(
                            quantity = updatedReservation.quantity.toInt(),
                            name = updatedReservation.name,
                            phone = updatedReservation.phone,
                            email = updatedReservation.email,
                            time = updatedReservation.time,
                            status = updatedReservation.status,
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                ApiClient.reservationService.editReservation(updateReservationRequest, updatedReservation.reservations_id)
                                Log.e("Reservation", "Status updated to: ${updatedReservation.status}")
                            } catch (e: Exception) {
                                Log.e("Reservation", "Error: ${e.message}")
                            }
                        }
                    }
                }

            )
        }
    }


}
