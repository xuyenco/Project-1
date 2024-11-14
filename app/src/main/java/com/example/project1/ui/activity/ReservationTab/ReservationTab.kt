package com.example.project1.ui.activity.ReservationTab

import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.project1.DataRequest.ReservationRequest
import com.example.project1.DataRequest.Tables_ReservationRequest
import com.example.project1.data.Reservation
import com.example.project1.data.Tables
import com.example.project1.retrofit.client.ApiClient
import com.example.project1.ui.activity.deleteTableReservation
import com.example.project1.ui.activity.getAllTables
import com.example.project1.ui.activity.getCurrentDateTime
import com.example.project1.ui.activity.getReservationByTableId
import com.example.project1.ui.activity.toDate
import com.example.project1.ui.activity.toString
import com.example.project1.ui.section.PopupBox
import com.example.project1.ui.section.TableItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

@Composable
fun ReservationTabScreen(){
    //Trạng thái load dữ liệu
    var isFirstLoading by remember { mutableStateOf(true) } // Chỉ true lần đầu load dữ liệu
    var isRefreshing by remember { mutableStateOf(false) }  // Trạng thái khi refresh
    var isError by remember { mutableStateOf(false) }

    //Dữ liệu lấy từ api
    var tablesLists by remember { mutableStateOf<List<Tables>>(emptyList()) }
    var tableReservationsList = remember { SnapshotStateMap<Tables, SnapshotStateList<Reservation>>()}

    //Input Time để tìm kiếm và đặt reservation
    var inputTime by remember { mutableStateOf("") }

    //Danh sách + trạng thái cho popup và các phần sau
    var selectedReservation by remember { mutableStateOf<Reservation?>(null)}
    var showPopup by remember { mutableStateOf(false) } // Trạng thái hiển thị popup
    var selectedTableIds = remember { mutableStateListOf<Int?>() }
    val context = LocalContext.current

    //Load dữ liệu
    LaunchedEffect(Unit) {
        while (true) {
            try {
                if (isFirstLoading) isFirstLoading = true
                isError = false
                isRefreshing = true

                // Fetch data from the API
                tablesLists = getAllTables()
                for (table in tablesLists) {
                    val reservationsResponse = getReservationByTableId(table.tables_id)
                    if (reservationsResponse != null && reservationsResponse.reservations.isNotEmpty()) {
                        // chỉ lấy những reservation chưa compeleted
                        reservationsResponse.reservations = reservationsResponse.reservations.filter {
                            //Chỉ lấy reservation tại thời điểm hiện tải đổ về trước 2 giờ
                            it.time.time > (getCurrentDateTime().time - 2 * 60 * 60 * 1000) && it.status != "Hoàn thành" }
                        tableReservationsList[table] = reservationsResponse.reservations.toMutableStateList()
                    }
                }
                if (isFirstLoading) isFirstLoading = false
                isRefreshing = false
            } catch (e: Exception) {
                isError = true
                isRefreshing = false
                if (isFirstLoading) isFirstLoading = false
            }
            delay(15000L)
        }
    }

    when {
        isFirstLoading -> {
            CircularProgressIndicator()
            inputTime = getCurrentDateTime().toString("yyyy/MM/dd HH:mm:ss")
        }
        isError -> {
            Text(text = "Something went wrong! Please try again later.")
        }
        tablesLists.isEmpty() || tableReservationsList.isEmpty()  -> {
            Text("No Data available")
        }
        else -> {
            Column {
                if (isRefreshing) {
                    Text("Refreshing data...")
                }
                // Hiển thị nội dung chính
                ReservationTabContent(
                    tablesLists,
                    tableReservationsList,
                    inputTime,
                    onInputTimeChange = { inputTime = it },
                    onShowPopupChange = { showPopup = it },
                    selectedTableIds,
                    selectedReservation,
                    onSelectedReservationChange = { selectedReservation = it })

            }
        }
    }
    //Hiện thị popup
    if (showPopup) {
        PopupBox(
            popupWidth = 500f,
            popupHeight = 600f,
            showPopup = showPopup,
            onClickOutside = {
                showPopup = false
                selectedReservation = null
            }
        ) {
            ReservationForm(
                reservation = selectedReservation,
                inputTime = inputTime,
                onSubmit = { reservationRequest ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            var isAvailable = true
                            val inputTimeDate = inputTime.toDate("yyyy/MM/dd HH:mm:ss")

                            // Kiểm tra từng reservation trong tableReservationsList chỉ với các bàn có trong selectedTableIds
                            for ((table, reservations) in tableReservationsList) {
                                if (table.tables_id in selectedTableIds && reservations != null && inputTimeDate != null) {
                                    val isTableAvailable = checkReservation(reservations, inputTimeDate)
                                    if (!isTableAvailable) {
                                        isAvailable = false
                                        break
                                    }
                                }
                            }

                            if (selectedReservation == null) {
                                if (selectedTableIds != null && isAvailable) {
                                    val reservation = ApiClient.reservationService.createReservation(reservationRequest)
                                    for (id in selectedTableIds.filterNotNull()) {
                                        val tableReservation = Tables_ReservationRequest(
                                            reservations_id = reservation.reservations_id,
                                            tables_id = id
                                        )
                                        ApiClient.tableReservationsService.addTableReservation(tableReservation)
                                    }
                                    withContext(Dispatchers.Main) {
                                        showPopup = false
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        if (selectedTableIds == null) {
                                            Toast.makeText(context, "Please select table", Toast.LENGTH_SHORT).show()
                                        }
                                        if (!isAvailable) {
                                            Toast.makeText(context, "Table is not available at the selected time", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                // Cập nhật reservation nếu đã tồn tại
                                ApiClient.reservationService.editReservation(
                                    reservationRequest, selectedReservation!!.reservations_id
                                )
                                withContext(Dispatchers.Main) {
                                    showPopup = false
                                    selectedReservation = null
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("Reservation", "Error: ${e.message}")
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                selectedTableIds
            )

        }
    }

}

@Composable
fun ReservationTabContent(
    tablesLists: List<Tables>,
    tableReservationsList : SnapshotStateMap<Tables, SnapshotStateList<Reservation>>,
    inputTime: String,
    onInputTimeChange :(String) -> Unit,
    onShowPopupChange: (Boolean) -> Unit,
    selectedTableIds : SnapshotStateList<Int?>,
    selectedReservation: Reservation?,
    onSelectedReservationChange: (Reservation?) -> Unit)
{
    var selectedTableId by remember { mutableStateOf<Int?>(null) }

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
                onShowPopup = { onShowPopupChange(true) },
                tableReservationsList,
                inputTime = inputTime,
                onInputTimeChange = onInputTimeChange,
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
                onEditClick = {
                    reservation -> onSelectedReservationChange(reservation) // Update the selected reservation
                    onShowPopupChange(true) // Show the popup
                },
                onDeleteClick = {
                    reservation ->
//                    onSelectedReservationChange(reservation)
//                    val id = selectedReservation!!.reservations_id
//                    Log.e("Deleted reservation", "Deleted reservation: $reservation")
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


@Composable
fun ContentLeft(
    onTableClick: (Int) -> Unit,
    tablesLists: List<Tables>,
    onShowPopup : () -> Unit,
    tableReservationsList : SnapshotStateMap<Tables, SnapshotStateList<Reservation>>,
    inputTime : String,
    onInputTimeChange :(String) -> Unit,
    selectedTableIds : SnapshotStateList<Int?>
)
{
    var reservationstate by remember { mutableStateOf(tablesLists.map { true }) }
    val context = LocalContext.current


    Column {
        Row {
            OutlinedTextField(
                value = inputTime,
                onValueChange = { onInputTimeChange(it) },
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
    selectedTableId: Int?,
    tableReservationsList : SnapshotStateMap<Tables, SnapshotStateList<Reservation>>,
    onEditClick: (Reservation) -> Unit,
    onDeleteClick: (Reservation) -> Unit,
    onStatusChange: (Reservation) -> Unit) {

    if (selectedTableId != null) {

        // Lấy tất cả các reservation liên quan đến bàn đã chọn
        val reservationsForTable = tableReservationsList.entries.find { it.key.tables_id == selectedTableId }?.value


        if (reservationsForTable != null) {
            Column {
                Text(text = "Danh sách đặt bàn cho bàn số ${selectedTableId + 1}:")
                reservationsForTable.forEach { reservation ->
                    ReservationItem(
                        reservation = reservation,
                        onEditClick =  { onEditClick(reservation) },
                        onDeleteClick = { onDeleteClick(reservation) },
                        onStatusChange = { onStatusChange(it) }
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


fun checkReservation (
    reservationResponseForTableReservation : MutableList<Reservation>?,
    inputTime : Date): Boolean
{
    if (reservationResponseForTableReservation != null) {
        val inputEndTime = inputTime.time + 2 * 60 * 60 * 1000

        for (reservation in reservationResponseForTableReservation) {
            val reservationStartTime = reservation.time.time
            val reservationEndTime = reservationStartTime + 2 * 60 * 60 * 1000

            if (!(inputEndTime <= reservationStartTime || inputTime.time >= reservationEndTime)) {
                return false
            }
        }
        // Nếu không trùng với bất kỳ reservation nào thì trả về true
        return true
    }
    // Nếu danh sách reservation rỗng thì luôn trả về true
    return true
}

