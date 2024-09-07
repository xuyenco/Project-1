package com.example.project1.ui.section

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.project1.data.Table

@Composable
fun TableItem(table: Table, onClick: () -> Unit, backgroundColor: Color = Color.Transparent) {
    Box(
        modifier = Modifier
            .size(250.dp, 200.dp)
            .padding(10.dp)
            .border(width = 1.dp, color = Color.Black)
            .background(backgroundColor) // Áp dụng màu nền ở đây
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Column {
            Icon(
                imageVector = Icons.Default.TableBar,
                contentDescription = "",
                modifier = Modifier
                    .padding(start = 5.dp, top = 5.dp, bottom = 5.dp)
                    .size(70.dp),
                tint = if (backgroundColor == Color.Black) Color.White else Color.Black
            )

            Text(
                text = "Bàn số " + table.tableNumber.toString(),
                color = if (backgroundColor == Color.Black) Color.White else Color.Black
            )
        }
    }
}
