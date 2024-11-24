package com.example.project1.ui.activity.ReservationTab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.project1.data.Reservation
import com.example.project1.data.Tables
@Composable
fun ContentRight(
    selectedTableId: Int?,
    tableReservationsList: SnapshotStateMap<Tables, SnapshotStateList<Reservation>>,
    allReservation: SnapshotStateList<Reservation>,
    onEditClick: (Reservation) -> Unit,
    onDeleteClick: (Reservation) -> Unit,
    onStatusChange: (Reservation) -> Unit
) {
    // Biến trạng thái để lưu giá trị tìm kiếm
    var searchQuery by remember { mutableStateOf("") }

    // Lọc danh sách dựa trên searchQuery
    val filteredReservations = if (selectedTableId != null) {
        tableReservationsList.entries.find { it.key.tables_id == selectedTableId }?.value
            ?.filter { it.name.contains(searchQuery, ignoreCase = true) } ?: emptyList()
    } else {
        allReservation.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Column {
        // Ô tìm kiếm
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Tìm kiếm theo tên khách đặt") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị danh sách đặt bàn
        if (filteredReservations.isEmpty()) {
            Text(text = "Không tìm thấy đặt bàn phù hợp.")
        } else {
            filteredReservations.forEach { reservation ->
                ReservationItem(
                    reservation = reservation,
                    onEditClick = { onEditClick(reservation) },
                    onDeleteClick = { onDeleteClick(reservation) },
                    onStatusChange = { onStatusChange(it) }
                )
                Divider()
            }
        }
    }
}
