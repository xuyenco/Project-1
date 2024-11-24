package com.example.project1.ui.activity.ReservationTab

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.project1.data.Reservation
import com.example.project1.data.Tables
import com.example.project1.ui.activity.toDate

@Composable
fun ContentLeft(
    onTableClick: (Int) -> Unit,
    tablesLists: List<Tables>,
    onShowPopup: () -> Unit,
    tableReservationsList: SnapshotStateMap<Tables, SnapshotStateList<Reservation>>,
    inputTime: String,
    onInputTimeChange: (String) -> Unit,
    onShowDatePickerChange : () -> Unit,
    onShowTimePickerChange : () -> Unit,
    selectedTableIds: SnapshotStateList<Int?>
) {
    var reservationState by remember { mutableStateOf(tablesLists.map { true }) }
    val context = LocalContext.current

    Column {
        Row {
            OutlinedTextField(
                value = inputTime,
                onValueChange = { onInputTimeChange(it) },
                label = { Text(text = "Time") },
                leadingIcon = { Icon(imageVector = Icons.Default.Timer, contentDescription = "Time Icon") },
                enabled = false, // Make it read-only
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(4f)
            )

            Button(
                onClick = {
                    selectedTableIds.clear()
                    val inputDate = inputTime.toDate("yyyy/MM/dd HH:mm:ss")
                    if (inputDate != null) {
                        reservationState = tablesLists.map { table ->
                            val reservationsForTable = tableReservationsList[table]
                            checkReservation(reservationsForTable, inputDate)
                        }
                    } else {
                        Toast.makeText(context, "Thời gian không hợp lệ", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Tìm kiếm")
            }
        }

        Row(modifier = Modifier.padding(8.dp)) {
            // Button for DatePicker
            Button(
                onClick = { onShowDatePickerChange() },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Chọn ngày")
            }

            // Button for TimePicker
            Button(
                onClick = { onShowTimePickerChange() },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Chọn giờ")
            }
        }

        Button(
            onClick = { onShowPopup() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Đặt bàn")
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(tablesLists.size) { index ->
                TableItem(
                    tables = tablesLists[index],
                    onClick = { onTableClick(tablesLists[index].tables_id) },
                    selectTable = {
                        if (reservationState[index]) {
                            if (selectedTableIds.contains(tablesLists[index].tables_id)) {
                                selectedTableIds.remove(tablesLists[index].tables_id)
                            } else {
                                selectedTableIds.add(tablesLists[index].tables_id)
                            }
                        }
                    },
                    reservationState = reservationState[index],
                    isSelected = selectedTableIds.contains(tablesLists[index].tables_id)
                )
            }
        }
    }


}


