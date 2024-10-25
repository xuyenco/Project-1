package com.example.project1.ui.activity.ReservationTab

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.project1.data.Reservation
import com.example.project1.ui.activity.toString


@Composable
fun ReservationItem(
    reservation : Reservation,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onStatusChange: (Reservation) -> Unit)
{
    var expanded by remember { mutableStateOf(false) }  // State để theo dõi menu có đang mở hay không
    val toggleStatus = reservation.status == "Arrived"

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Trạng thái: ")
                    Switch(
                        checked = toggleStatus,
                        onCheckedChange = {isChecked ->
                            val updatedReservation = reservation.copy(status = if (isChecked) "Arrived" else "Pending")
                            onStatusChange(updatedReservation) // Gọi callback để xử lý cập nhật
                        }
                    )
                    Text(if (toggleStatus) "Arrived" else "Pending")
                }
//                Text(text = "Trạng thái: ${reservation.status}")
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
