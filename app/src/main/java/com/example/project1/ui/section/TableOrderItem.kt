package com.example.project1.ui.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.project1.data.Items
import com.example.project1.data.Menu
import com.example.project1.data.Reservation
import com.example.project1.data.Tables
import java.util.Date

@Composable
fun TableOrderItem(
    reservation: Reservation,
    tablesList: String,
    onClick: () -> Unit,
    isSelected: Boolean
) {
    val backgroundColor = if (isSelected) Color.Red else Color.White // Thay đổi màu nền khi được chọn
    val surfaceColor = when {
        reservation.status == "Ordered" -> Color.Green
        else -> MaterialTheme.colorScheme.surface
    }
    Box(
        modifier = Modifier
            .padding(8.dp)
            .background(backgroundColor)// Đặt màu nền dựa trên trạng thái được chọn
            .clickable { onClick() }
            .fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier.padding(4.dp).fillMaxWidth(),
            color = surfaceColor,
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Reservation ID: ${reservation.reservations_id}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Name: ${reservation.name}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Phone: ${reservation.phone}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Email: ${reservation.email}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Quantity: ${reservation.quantity}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Tables Reserved: $tablesList",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}


@Composable
fun MenuItem(item: Items, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(250.dp, 200.dp)
            .padding(10.dp)
            .border(width = 1.dp, color = Color.Black)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = item.image_url),
                contentDescription = "Hình ảnh món ăn",
                modifier = Modifier
                    .padding(5.dp)
                    .size(70.dp)
            )
            Text(text = "${item.name} \n ${item.price} vnd")
        }
    }
}