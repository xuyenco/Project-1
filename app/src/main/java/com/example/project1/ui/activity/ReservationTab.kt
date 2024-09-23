package com.example.project1.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project1.data.Reservation
import com.example.project1.data.SideNavbar
import com.example.project1.data.Tables
import com.example.project1.data.Tables_Reservations
import com.example.project1.retrofit.client.ApiClient
import com.example.project1.ui.section.NavigationDrawerItem
import com.example.project1.ui.section.PopupBox
import com.example.project1.ui.section.TableItem
import com.example.project1.ui.theme.Project1Theme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.jar.Attributes.Name

private val TablesReservationsLists = listOf(
    Tables_Reservations(
        tables_id = 0,
        reservations_id = 0,
        created_at = Date(),
        updated_at = Date()
    ), Tables_Reservations(
        tables_id = 1,
        reservations_id = 1,
        created_at = Date(),
        updated_at = Date()
    ), Tables_Reservations(
        tables_id = 2,
        reservations_id = 2,
        created_at = Date(),
        updated_at = Date()
    ), Tables_Reservations(
        tables_id = 3,
        reservations_id = 3,
        created_at = Date(),
        updated_at = Date()
    ), Tables_Reservations(
        tables_id = 4,
        reservations_id = 4,
        created_at = Date(),
        updated_at = Date()
    )
)

@Composable
fun ReservationTabScreen(tablesLists: List<Tables>,reservationList: List<Reservation>) {
    var selectedTableId by remember { mutableStateOf<Int?>(null) }
    var showPopup by remember { mutableStateOf(false) } // Trạng thái hiển thị popup
    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(2f) // Tỷ lệ phần này chiếm 2/3 màn hình
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            ContentLeft(
                onTableClick = {
                    tableId -> selectedTableId = tableId },
                tablesLists,
                onShowPopup = { showPopup = true },
                reservationList) // Nội dung phần 1 và 2
        }

        // Phần 2: 1/3 bên phải
        Column(
            modifier = Modifier
                .weight(1f) // Tỷ lệ phần này chiếm 1/3 màn hình
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            ContentRight(selectedTableId,reservationList) // Truyền selectedTableId vào ContentRight
        }
    }
    PopupBox(
        popupWidth = 500f, // Kích thước popup
        popupHeight = 500f,
        showPopup = showPopup,
        onClickOutside = { showPopup = false } // Tắt popup khi click ra ngoài
    ) {
        // Nội dung bên trong popup
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var quantity by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .padding(16.dp) // Padding cho toàn bộ cột bên trong popup
        ) {
            Text(
                text = "Điền thông tin khách hàng",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Họ tên khách
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(text = "Họ tên khách đặt bàn:")
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                )
            }

            // Số điện thoại
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(text = "Số điện thoại:")
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                )
            }

            // Email
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(text = "Email:")
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                )
            }

            // Số lượng khách
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(text = "Số lượng khách:")
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                )
            }
            Row {
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Cancle")
                }
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Submit")
                }
            }
        }
    }
}

@Composable
fun ContentLeft(onTableClick: (Int) -> Unit, tablesLists: List<Tables>, onShowPopup : () -> Unit, reservationList: List<Reservation>) {
    var inputtime by remember { mutableStateOf("") }
    var reservationstate by remember { mutableStateOf(tablesLists.map { true }) }
    var selectedTableIds = remember { mutableStateListOf<Int?>() }
    val context = LocalContext.current

    Column {
        Row {
            OutlinedTextField(
                value = inputtime,
                onValueChange = { inputtime = it },
                label = { Text(text = "Time") },
                leadingIcon = { Icon(imageVector = Icons.Default.Timer, contentDescription = "Time Icon") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(4f)
            )

            Button(
                onClick = {
                    selectedTableIds.clear()
                    val inputDate = inputtime.toDate("yyyy/MM/dd HH:mm:ss")
                    if (inputDate != null) {
                        reservationstate = tablesLists.map { table ->
                            val reservationsForTable = TablesReservationsLists
                                .filter { it.tables_id == table.tables_id }
                                .mapNotNull { tableReservation ->
                                    reservationList.find { it.reservations_id == tableReservation.reservations_id }
                                }
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
                Text(text = "Submit")
            }
        }

        // Button to trigger popup
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
                        if (reservationstate[index]) {
                            if (selectedTableIds.contains(tablesLists[index].tables_id)) {
                                selectedTableIds.remove(tablesLists[index].tables_id)
                            } else {
                                selectedTableIds.add(tablesLists[index].tables_id)
                            }
                        }
                    },
                    reservationState = reservationstate[index],
                    isSelected = selectedTableIds.contains(tablesLists[index].tables_id)
                )
            }
        }
    }
}

@Composable
fun ContentRight(selectedTableId: Int?, reservationList: List<Reservation>) {
    if (selectedTableId != null) {
        // Lấy tất cả các reservation liên quan đến bàn đã chọn
        val reservationsForTable = TablesReservationsLists
            .filter { it.tables_id == selectedTableId }
            .mapNotNull { tableReservation ->
                reservationList.find { it.reservations_id == tableReservation.reservations_id }
            }

        if (reservationsForTable.isNotEmpty()) {
            Column {
                Text(text = "Danh sách đặt bàn cho bàn số ${selectedTableId + 1}:")
                reservationsForTable.forEach { reservation ->
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "Tên: ${reservation.name}")
                        Text(text = "Số điện thoại: ${reservation.phone}")
                        Text(text = "Thời gian: ${reservation.time.toString("yyyy/MM/dd HH:mm:ss")}")
                    }
                    Divider()
                }
            }
        } else {
            Text(text = "Không có đặt bàn cho bàn này.")
        }
    } else {
        Text(text = "Chọn bàn để xem thông tin đặt bàn.")
    }
}
fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}
fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}
fun String.toDate(format: String, locale: Locale = Locale.getDefault()): Date? {
    return try {
        val formatter = SimpleDateFormat(format, locale)
        formatter.parse(this)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
fun checkReservation (reservationList : List<Reservation>, inputTime : Date): Boolean {
    val inputTimeEnd = inputTime.time + 2 * 60 * 60 * 100
    for (reservation in reservationList) {
        val reservation1Date = reservation.time
        if (reservation1Date.time < inputTime.time && inputTime.time < reservation1Date.time + 2 * 60 * 60 * 1000) {
            return false
        }
    }
    for (reservation in reservationList) {
        val reservation1Date = reservation.time
        if (reservation1Date.time < inputTimeEnd && inputTimeEnd <reservation1Date.time + 2 * 60 * 60 * 1000) {
            return false
        }
    }
    return true
}

// retrofit API
// Get all tables
suspend fun getAllTables(): List<Tables> {
    return try {
        withContext(Dispatchers.IO) {
            ApiClient.tableService.getAllTable()
        }
    } catch (e: Exception) {
        Log.e("Api error", e.toString())
        emptyList()
    }
}
// Get all reservations
suspend fun getAllReservations(): List<Reservation> {
    return try {
        withContext(Dispatchers.IO) {
            ApiClient.reservationService.getAllReservation()
        }
    } catch (e: Exception) {
        Log.e("Api error", e.toString())
        emptyList()
    }
}
