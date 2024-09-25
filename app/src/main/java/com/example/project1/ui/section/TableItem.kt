package com.example.project1.ui.section

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.project1.data.Tables

@Composable
fun TableItem(
    tables: Tables,
    onClick: () -> Unit,
    reservationState: Boolean,
    selectTable: () -> Unit,
    isSelected: Boolean
) {
    val backgroundColor = if (reservationState) Color.Transparent else Color.Black

    Box(
        modifier = Modifier
            .size(250.dp, 200.dp)
            .padding(10.dp)
            .border(width = 1.dp, color = Color.Black)
            .background(backgroundColor)
            .clickable {
                onClick()
                selectTable()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.TableBar,
                contentDescription = "",
                modifier = Modifier
                    .padding(start = 5.dp, top = 5.dp, bottom = 5.dp)
                    .size(70.dp),
                tint = if (reservationState) Color.Black else Color.White
            )

            Text(
                text = tables.name,
                color = if (reservationState) Color.Black else Color.White
            )
        }

        // Thêm Checkbox vào góc trên bên phải
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd) // Căn chỉnh Checkbox ở góc trên bên phải
                .padding(8.dp)
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { selectTable() }, // Gọi `selectTable` khi Checkbox thay đổi
                colors = CheckboxDefaults.colors(
                    checkmarkColor = Color.White,
                    uncheckedColor = if (reservationState) Color.Black else Color.White
                )
            )
        }
    }
}