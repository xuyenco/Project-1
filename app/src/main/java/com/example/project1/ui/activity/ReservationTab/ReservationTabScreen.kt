package com.example.project1.ui.activity.ReservationTab

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.project1.DataRequest.Tables_ReservationRequest
import com.example.project1.data.Reservation
import com.example.project1.data.Tables
import com.example.project1.retrofit.client.ApiClient
import com.example.project1.ui.activity.getAllTables
import com.example.project1.ui.activity.getCurrentDateTime
import com.example.project1.ui.activity.getReservationByTableId
import com.example.project1.ui.activity.toDate
import com.example.project1.ui.activity.toString
import com.example.project1.ui.section.PopupBox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationTabScreen(){
    //Trạng thái load dữ liệu
    var isFirstLoading by remember { mutableStateOf(true) } // Chỉ true lần đầu load dữ liệu
    var isRefreshing by remember { mutableStateOf(false) }  // Trạng thái khi refresh
    var isError by remember { mutableStateOf(false) }

    //Dữ liệu lấy từ api
    var tablesLists by remember { mutableStateOf<List<Tables>>(emptyList()) }
    var tableReservationsList = remember { SnapshotStateMap<Tables, SnapshotStateList<Reservation>>()}
    var allReservation = remember { SnapshotStateList<Reservation>() }

    //Input Time để tìm kiếm và đặt reservation
    var inputTime by remember { mutableStateOf(SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(Date())) }

    //Danh sách + trạng thái cho popup và các phần sau
    var selectedReservation by remember { mutableStateOf<Reservation?>(null)}
    var showPopup by remember { mutableStateOf(false) } // Trạng thái hiển thị popup
    var selectedTableIds = remember { mutableStateListOf<Int?>() }

    // Khởi tạo Calendar để quản lý trạng thái ngày giờ
    val inputTimeCalendar = remember { Calendar.getInstance() }
    inputTimeCalendar.time = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).parse(inputTime) ?: Date()


    // Flags for showing dialogs
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
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
                val reservations = ApiClient.reservationService.getAllReservation()
                    .filter { it.time.time > (getCurrentDateTime().time - 2 * 60 * 60 * 1000) && it.status != "Hoàn thành" }

                // Cập nhật trực tiếp danh sách
                allReservation.clear()
                allReservation.addAll(reservations)

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
            delay(5000L)
        }
    }
    Log.e("ALl reservation 2", allReservation.toString())


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
                    selectedTableIds,
                    selectedReservation,
                    allReservation,
                    onInputTimeChange = { inputTime = it },
                    onShowPopupChange = { showPopup = it },
                    onShowDatePickerChange = { showDatePicker = it },
                    onShowTimePickerChange = { showTimePicker = it },
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
                                if (selectedTableIds.size != 0 && isAvailable) {
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
                                        if (selectedTableIds.size == 0) {
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

    // DatePicker Popup
    if (showDatePicker) {
        PopupBox(
            popupWidth = 500f,
            popupHeight = 700f,
            showPopup = showDatePicker,
            onClickOutside = { showDatePicker = false }
        ) {
            val dateState = rememberDatePickerState()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                // Hiển thị DatePicker
                DatePicker(state = dateState)

                Spacer(modifier = Modifier.height(16.dp))

                // Nút xác nhận
                Button(onClick = {
                    val selectedDateMillis = dateState.selectedDateMillis
                    if (selectedDateMillis != null) {
                        // Chỉ sửa ngày trong inputTimeCalendar
                        val selectedDate = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
                        inputTimeCalendar.set(Calendar.YEAR, selectedDate.get(Calendar.YEAR))
                        inputTimeCalendar.set(Calendar.MONTH, selectedDate.get(Calendar.MONTH))
                        inputTimeCalendar.set(Calendar.DAY_OF_MONTH, selectedDate.get(Calendar.DAY_OF_MONTH))

                        // Cập nhật inputTime
                        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                        inputTime = dateFormat.format(inputTimeCalendar.time)

                        // Ẩn DatePicker
                        showDatePicker = false
                    } else {
                        Toast.makeText(context, "Please select a date", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Confirm")
                }
            }
        }
    }

// TimePicker Popup
    if (showTimePicker) {
        PopupBox(
            popupWidth = 700f,
            popupHeight = 500f,
            showPopup = showTimePicker,
            onClickOutside = { showTimePicker = false }
        ) {
            val timeState = rememberTimePickerState(is24Hour = true)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                // Hiển thị TimePicker
                TimePicker(state = timeState)

                Spacer(modifier = Modifier.height(16.dp))

                // Nút xác nhận
                Button(onClick = {
                    // Lấy giờ và phút từ TimePickerState
                    val hour = timeState.hour
                    val minute = timeState.minute

                    // Chỉ sửa giờ và phút trong inputTimeCalendar
                    inputTimeCalendar.set(Calendar.HOUR_OF_DAY, hour)
                    inputTimeCalendar.set(Calendar.MINUTE, minute)
                    inputTimeCalendar.set(Calendar.SECOND, 0)

                    // Cập nhật inputTime
                    val timeFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                    inputTime = timeFormat.format(inputTimeCalendar.time)

                    // Ẩn TimePicker
                    showTimePicker = false
                }) {
                    Text("Confirm")
                }
            }
        }
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



