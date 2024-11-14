package com.example.project1.ui.activity.ReservationTab

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.project1.DataRequest.ReservationRequest
import com.example.project1.data.Reservation
import com.example.project1.ui.activity.toDate

@Composable
fun ReservationForm(
    reservation: Reservation?,
    inputTime: String,
    onSubmit: (ReservationRequest) -> Unit,
    selectedTableIds: SnapshotStateList<Int?> // Bàn đã chọn
) {
    var name by remember { mutableStateOf(reservation?.name ?: "") }
    var phone by remember { mutableStateOf(reservation?.phone ?: "") }
    var email by remember { mutableStateOf(reservation?.email ?: "") }
    var quantity by remember { mutableStateOf(reservation?.quantity?.toString() ?: "") }
    var status by remember { mutableStateOf("Đang chờ") } // Trạng thái mặc định cho đặt bàn mới
    var toggleStatus by remember { mutableStateOf(false) } // Trạng thái của nút bật/tắt

    // Chuyển các tableId thành chuỗi để hiển thị
    val selectedTablesText = selectedTableIds.filterNotNull().joinToString(", ") { "Table $it" }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = if (reservation != null) "Sửa Đặt Bàn" else "Đặt Bàn Mới",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Hiển thị trường name và quantity luôn
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tên") })
        OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Số lượng") })

        if (status != "Đã đến") {
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Số điện thoại") })
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })

            OutlinedTextField(
                value = selectedTablesText,
                onValueChange = {},
                label = { Text("Bàn đã chọn") },
                enabled = false,
                readOnly = true
            )
        }

        if (reservation == null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Trạng thái: ")
                Switch(
                    checked = toggleStatus,
                    onCheckedChange = { isChecked ->
                        toggleStatus = isChecked
                        status = if (isChecked) "Đã đến" else "Đang chờ"
                    }
                )
                Text(if (toggleStatus) "Đã đến" else "Đang chờ")
            }
        }

        Button(onClick = {
            val parsedTime = inputTime.toDate("yyyy/MM/dd HH:mm:ss")
            if (parsedTime != null) {
                val reservationRequest = ReservationRequest(
                    quantity = quantity.toInt(),
                    name = name,
                    phone = phone,
                    email = email,
                    time = parsedTime,
                    status = status, // Sử dụng trạng thái đã chọn
                )
                onSubmit(reservationRequest)
            } else {
                Log.e("Reservation", "Định dạng thời gian không hợp lệ")
            }
        }) {
            Text(if (reservation != null) "Lưu thay đổi" else "Gửi")
        }
    }
}