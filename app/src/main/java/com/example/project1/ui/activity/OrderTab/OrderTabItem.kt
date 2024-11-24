package com.example.project1.ui.activity.OrderTab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.DataResponse.ItemsResponseForItemByOrder
import com.example.project1.data.Tables
@Composable
fun OrderTabItem(
    itemsList: List<ItemsResponseForItemByOrder>,
    tablesList: List<Tables>,
    description: String?,
    checkedStates: List<Boolean>,
    onCheckedChange: (List<Boolean>) -> Unit,
    onOrderCompleted: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(
            modifier = Modifier
                .background(Brush.verticalGradient(colors = listOf(Color(0xFFF5F5F5), Color.White)))
                .padding(16.dp)
        ) {
            // Danh sách bàn
            Text(
                text = "Danh sách bàn:",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Column(
                modifier = Modifier
                    .padding(bottom = 16.dp)
            ) {
                tablesList.forEach { table ->
                    Text(
                        text = table.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            Divider(color = Color.Gray, thickness = 1.dp)

            // Danh sách món ăn
            Text(
                text = "Danh sách món ăn:",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Column {
                itemsList.forEachIndexed { index, dish ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        val isChecked = checkedStates[index]
                        Text(
                            text = "${dish.name}: ${dish.quantity_used}",
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                                .alpha(if (isChecked) 0.5f else 1f),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textDecoration = if (isChecked) TextDecoration.LineThrough else null,
                                color = if (isChecked) Color.Gray else Color.Black
                            )
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
            }

            Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            // Mô tả
            Text(
                text = "Ghi chú: ${description ?: "Không có ghi chú"}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = FontStyle.Italic,
                    color = Color.DarkGray
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
