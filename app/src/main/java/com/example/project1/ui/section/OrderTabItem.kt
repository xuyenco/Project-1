package com.example.project1.ui.section

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.project1.data.Dishes
import com.example.project1.data.Table

@Composable
fun OrderTabItem(dishesList: List<Dishes>, tableList: List<Table>) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .border(1.dp, Color.LightGray)) {
        // Hiển thị danh sách các table
        Box(
            modifier = Modifier
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    val y = size.height - strokeWidth / 2

                    drawLine(
                        Color.LightGray,
                        Offset(0f, y),
                        Offset(size.width, y),
                        strokeWidth
                    )
                }
                .padding(8.dp) // Thêm padding cho Box
        ) {
            Column { // Thay vì sử dụng for, dùng Column để xếp chồng các Text
                for (table in tableList) {
                    Text(
                        text = "Table ${table.tableNumber}",
                        modifier = Modifier.padding(bottom = 4.dp) // Padding giữa các Text
                    )
                }
            }
        }

        // Hiển thị danh sách các dishes
        Box(
            modifier = Modifier.padding(8.dp)
        ) {
            Column {
                // Sử dụng remember để giữ trạng thái cho các checkbox
                val checkedStates = remember { mutableStateOf(List(dishesList.size) { false }) }

                for ((index, dish) in dishesList.withIndex()) {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp) // Thêm padding giữa các dòng
                    ) {
                        Text(
                            text = dish.name,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp) // Padding bên phải của Text
                        )
                        Checkbox(
                            checked = checkedStates.value[index],
                            onCheckedChange = { checked ->
                                // Cập nhật trạng thái checkbox tại vị trí index
                                checkedStates.value = checkedStates.value.toMutableList().apply {
                                    this[index] = checked
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

