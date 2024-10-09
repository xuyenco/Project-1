package com.example.project1.ui.activity

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.project1.DataRequest.ReservationRequest
import com.example.project1.data.Reservation
import com.example.project1.data.Tables
import com.example.project1.data.Tables_Reservations
import com.example.project1.retrofit.client.ApiClient
import com.example.project1.ui.section.PopupBox
import com.example.project1.ui.section.TableItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val TablesReservationsLists = listOf(
    Tables_Reservations(
        tables_id = 1,
        reservations_id = 0,
        created_at = Date(),
        updated_at = Date()
    ), Tables_Reservations(
        tables_id = 1,
        reservations_id = 1,
        created_at = Date(),
        updated_at = Date()
    ), Tables_Reservations(
        tables_id = 1,
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
fun ReservationTabScreen(){
    var tablesLists by remember { mutableStateOf<List<Tables>>(emptyList()) }
    var reservationList by remember { mutableStateOf<List<Reservation>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    //new dat

    LaunchedEffect(Unit) {
        while (true) {
            try {
                // Fetch data from the API
                tablesLists = getAllTables()
                reservationList = getAllReservations()
                isLoading = false
                isError = false // Reset error if successful
            } catch (e: Exception) {
                isError = true
                isLoading = false
            }
            delay(20000L) // Wait for 1 second before fetching again
        }
    }

    if (isLoading) {
        Text(text = "Đang tải dữ liệu...")
    } else if (isError) {
        Text(text = "Đang tải dữ liệu...")
    } else {
        ReservationTabContent(tablesLists,reservationList)
    }

}

@Composable
fun ReservationTabContent(tablesLists: List<Tables>,reservationList: List<Reservation>) {
    var selectedTableId by remember { mutableStateOf<Int?>(null) }
    var inputTime by remember { mutableStateOf("") }
    var showPopup by remember { mutableStateOf(false) } // Trạng thái hiển thị popup
    var selectedReservation by remember { mutableStateOf<Reservation?>(null)} // Lưu thông tin reservation đợc chọn

    inputTime = getCurrentDateTime().toString("yyyy/MM/dd HH:mm:ss")

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
                reservationList,
                inputTime = inputTime,
                onInputTimeChange = { it -> inputTime = it })
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
                reservationList,
                onEditClick = {
                    reservation -> selectedReservation = reservation // Update the selected reservation
                    showPopup = true // Show the popup
                },
                onDeleteClick = {
                    reservation -> selectedReservation = reservation
                    val id = selectedReservation!!.reservations_id
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            var result = ApiClient.reservationService.deleteReservation(id)
                            reservationList.filter{it != selectedReservation}

                        } catch (e: Exception) {
                            Log.e("Reservation", "Error: ${e.message}")
                        }
                    }

                }
            )
        }
    }
    if (showPopup) {
        PopupBox(
            popupWidth = 500f,
            popupHeight = 500f,
            showPopup = showPopup,
            onClickOutside = { showPopup = false }
        ) {
            ReservationForm(
                reservation = selectedReservation,
                inputTime = inputTime,
                onSubmit = { reservationRequest ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            if (selectedReservation == null) {
                                // Tạo mới
                                ApiClient.reservationService.createReservation(reservationRequest)
                            } else {
//                             Cập nhật
                                ApiClient.reservationService.editReservation(
                                    reservationRequest,selectedReservation!!.reservations_id
                                )
                            }
                            withContext(Dispatchers.Main) { showPopup = false }
                        } catch (e: Exception) {
                            Log.e("Reservation", "Error: ${e.message}")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ReservationForm(
    reservation: Reservation?,
    inputTime: String,
    onSubmit: (ReservationRequest) -> Unit
) {
    var name by remember { mutableStateOf(reservation?.name ?: "") }
    var phone by remember { mutableStateOf(reservation?.phone ?: "") }
    var email by remember { mutableStateOf(reservation?.email ?: "") }
    var quantity by remember { mutableStateOf(reservation?.quantity?.toString() ?: "") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = if (reservation != null) "Edit Reservation" else "New Reservation", modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantity") })

        Button(onClick = {
            val parsedTime = inputTime.toDate("yyyy/MM/dd HH:mm:ss")
            if (parsedTime != null) {
                val reservationRequest = ReservationRequest(
                    quantity = quantity.toInt(),
                    name = name,
                    phone = phone,
                    email = email,
                    time = parsedTime
                )
                onSubmit(reservationRequest)
            } else {
                Log.e("Reservation", "Invalid date format")
            }
        }) {
            Text(if (reservation != null) "Save Changes" else "Submit")
        }
    }
}

@Composable
fun ContentLeft(
    onTableClick: (Int) -> Unit,
    tablesLists: List<Tables>,
    onShowPopup : () -> Unit,
    reservationList: List<Reservation>,
    inputTime : String,
    onInputTimeChange :(String) -> Unit)
{
    var reservationstate by remember { mutableStateOf(tablesLists.map { true }) }
    var selectedTableIds = remember { mutableStateListOf<Int?>() }
    val context = LocalContext.current


    Column {
        Row {
            OutlinedTextField(
                value = inputTime,
                onValueChange = { it -> onInputTimeChange(it) },
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
                    val inputDate = inputTime.toDate("yyyy/MM/dd HH:mm:ss")
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
fun ContentRight(
    selectedTableId: Int?, reservationList: List<Reservation>,onEditClick: (Reservation) -> Unit,onDeleteClick: (Reservation) -> Unit) {

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
                    ReservationItem(
                        reservation = reservation,
                        onEditClick =  { onEditClick(reservation) },
                        onDeleteClick = { onDeleteClick(reservation) }
                    )
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

@Composable
fun ReservationItem(reservation: Reservation, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }  // State để theo dõi menu có đang mở hay không

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }  // Mở menu khi nhấn vào Box
                .padding(8.dp)
        ) {
            Column {
                Text(text = "Tên: ${reservation.name}")
                Text(text = "Số điện thoại: ${reservation.phone}")
                Text(text = "Thời gian: ${reservation.time.toString("yyyy/MM/dd HH:mm:ss")}")
            }
        }

        // DropdownMenu xuất hiện khi expanded = true
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }  // Đóng menu khi nhấn ra ngoài
        ) {
            DropdownMenuItem(
                text = { Text(text = "Sửa") },
                onClick = {
                    expanded = false
                    onEditClick() // Sẽ hiện lên popupbox để chỉnh sửa thông tin khách hàng
                })

            DropdownMenuItem(
                text = { Text(text = "Xóa") },
                onClick = {
                    expanded = false
                    onDeleteClick()
                })
        }
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

