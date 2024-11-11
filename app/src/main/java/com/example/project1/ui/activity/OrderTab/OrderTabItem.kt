package com.example.project1.ui.activity.OrderTab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.project1.DataResponse.ItemsResponseForItemByOrder
import com.example.project1.data.Tables

@Composable
fun OrderTabItem(
    itemsList: List<ItemsResponseForItemByOrder>,
    tablesList: List<Tables>,
    description : String?,
    checkedStates: List<Boolean>,
    onCheckedChange: (List<Boolean>) -> Unit,
    onOrderCompleted: () -> Unit
) {
    Column {
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
                .padding(8.dp)
        ) {
            Column {
                for (table in tablesList) {
                    Text(
                        text = table.name,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }

        // Hiển thị danh sách các dishes
        Box(modifier = Modifier.padding(8.dp)) {
            Column {
                //Hiện danh sách món ăn
                for ((index, dish) in itemsList.withIndex()) {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        val isChecked = checkedStates[index]

                        Text(
                            text = dish.name + ": " + dish.quantity_used,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                                .alpha(if (isChecked) 0.5f else 1f),
                            textDecoration = if (isChecked) TextDecoration.LineThrough else null
                        )
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                val updatedStates = checkedStates.toMutableList().apply {
                                    this[index] = checked
                                }
                                onCheckedChange(updatedStates)

                                // Kiểm tra nếu tất cả checkbox đều được checked
                                if (updatedStates.all { it }) {
                                    onOrderCompleted()
                                }
                            }
                        )
                    }
                }
                //Hiện description
                Text(
                    text = "Description: $description",
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
            }
        }
    }
}
