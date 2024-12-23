package com.example.project1.ui.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.project1.data.Items
import com.example.project1.data.Reservation
import java.util.Date

@Composable
fun ReservationItem(
    reservation: Reservation,
    tablesList: String,
    onClick: () -> Unit,
    isSelected: Boolean,
    orderStatus: String
) {
    val backgroundColor = if (isSelected) Color.Red else Color.White // Màu nền khi được chọn
    val surfaceColor = when {
        reservation.status == "Đã đặt món" -> Color.Green
        reservation.status == "Hoàn thành" -> Color.Red
        else -> MaterialTheme.colorScheme.surface
    }
    val orderStatusText = when{
        orderStatus == "Hoàn thành" -> "Đơn hoàn thành"
        orderStatus == "Đang chờ" -> "Đơn đang xử lý"
        else -> "Error"
    }
    Box(
        modifier = Modifier
            .padding(8.dp)
            .background(backgroundColor) // Đặt màu nền dựa trên trạng thái được chọn
            .clickable { onClick() }
            .fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            color = surfaceColor,
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 4.dp
        ) {
            Box( // Sử dụng Box để căn chỉnh `orderStatus`
                modifier = Modifier.fillMaxWidth()
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
                // Hiển thị `orderStatus` ở góc trên bên phải
                if(orderStatusText != "Error")
                    Text(
                    text = orderStatusText,
                    style = MaterialTheme.typography.labelMedium.copy(color = Color.White),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Blue, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .size(100.dp) // Tạo khung cho ảnh
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = item.image_url),
                    contentDescription = "Hình ảnh món ăn",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // Cắt và căn chỉnh để khớp khung
                )
            }

            Text(
                text = item.name,
                maxLines = 1, // Giới hạn số dòng để tránh tràn
                overflow = TextOverflow.Ellipsis, // Hiển thị "..." nếu text quá dài
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .fillMaxWidth()
            )

            Text(
                text = "${item.price} VND",
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewReservationItem() {
//    val reservation = Reservation(1, 10, "Alice Smith", "0123456789", "alice@example.com","pending", Date(), Date(), Date())
//
//    ReservationItem(
//        reservation = reservation,
//        tablesList = "Table 1, Table 2",
//        onClick = {},
//        isSelected = true,
//        orderStatus = "Đang chờ"
//    )
//}
@Preview(showBackground = true)
@Composable
fun PreviewMenuItem() {
    MenuItem(
        item = Items(
            items_id = 1,
            name = "Very Long Dish Name That Might Overflow Very Long Dish Name That Might Overflow",
            image_url = "https://product.hstatic.net/1000389344/product/tex_-_mex__4_17a6169ff10041c3b4c6000d4f3173b8_master.png",
            unit = "plate",
            category = "Main Course",
            price = 120000,
            created_at = Date(),
            updated_at = Date()
        ),
        onClick = {}
    )
}
