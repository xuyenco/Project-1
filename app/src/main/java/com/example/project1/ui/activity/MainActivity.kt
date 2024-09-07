package com.example.project1.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project1.data.Reservation
import com.example.project1.data.SideNavbar
import com.example.project1.data.Table
import com.example.project1.data.Table_Reservation
import com.example.project1.ui.theme.Project1Theme
import com.example.project1.ui.section.NavigationDrawerItem
import com.example.project1.ui.section.TableItem
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val navBarItemList = listOf(
    SideNavbar(
        title = "Home",
        icon = Icons.Default.Home
    ),
    SideNavbar(
        title = "Đặt bàn",
        icon = Icons.Default.TableRestaurant
    ),
    SideNavbar(
        title = "Đặt món",
        icon = Icons.Default.Restaurant
    )
)
private val tableItemList = listOf(
    Table(
        id = 0,
        tableNumber = 1,
        tableSeatNumber = 2,
        tablePosition = 2,
        tableStatus = true
    ),Table(
        id = 1,
        tableNumber = 2,
        tableSeatNumber = 2,
        tablePosition = 2,
        tableStatus = true
    ),Table(
        id = 2,
        tableNumber = 3,
        tableSeatNumber = 2,
        tablePosition = 2,
        tableStatus = true
    ),Table(
        id = 3,
        tableNumber = 3,
        tableSeatNumber = 3,
        tablePosition = 3,
        tableStatus = true
    ),Table(
        id = 4,
        tableNumber = 4,
        tableSeatNumber = 4,
        tablePosition = 4,
        tableStatus = true
    ),Table(
        id = 5,
        tableNumber = 5,
        tableSeatNumber = 5,
        tablePosition = 5,
        tableStatus = true
    ),Table(
        id = 6,
        tableNumber = 6,
        tableSeatNumber = 6,
        tablePosition = 6,
        tableStatus = true
    )

)
private val reservationItemList = listOf(
    Reservation(
        id = 0,
        dateTime = "2024/09/03 3:15:00",
        name = "Duong",
        number = "0986916220"
    ),Reservation(
        id = 1,
        dateTime = "2024/09/03 4:15:00",
        name = "Duong",
        number = "0986916220"
    ),Reservation(
        id = 2,
        dateTime = "2024/09/03 5:15:00",
        name = "Duong",
        number = "0986916220"
    ),Reservation(
        id = 3,
        dateTime = "2024/09/03 6:15:00",
        name = "Duong",
        number = "0986916220"
    ),Reservation(
        id = 4,
        dateTime = "2024/09/03 6:15:00",
        name = "Duong",
        number = "0986916220"
    ),Reservation(
        id = 5,
        dateTime = "2024/09/03 9:15:00",
        name = "Duong",
        number = "0986916220"
    ),Reservation(
        id = 6,
        dateTime = "2024/09/03 8:15:00",
        name = "Duong",
        number = "0986916220"
    )
)
private val Table_ReservationItemList = listOf(
    Table_Reservation(
        tableId = 0,
        reservationId = 0
    ),Table_Reservation(
        tableId = 1,
        reservationId = 1
    ),Table_Reservation(
        tableId = 2,
        reservationId = 2
    ),Table_Reservation(
        tableId = 3,
        reservationId = 3
    ),Table_Reservation(
        tableId = 0,
        reservationId = 4
    ),Table_Reservation(
        tableId = 1,
        reservationId = 5
    ),Table_Reservation(
        tableId = 2,
        reservationId = 6
    )
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Project1Theme {
                SetNavbarColor(color = MaterialTheme.colorScheme.background)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen()
                }

            }
        }
    }
}
@Composable
private fun SetNavbarColor(color : Color){
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(color = color)
    }
}
@Composable
fun HomeScreen() {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Text("Drawer title", modifier = Modifier.padding(16.dp))
                Divider()
                for(item in navBarItemList) {
                    NavigationDrawerItem(sideNavbar = item)
                }
                // ...other drawer items
            }
        }
    ) {
        // Screen content
        ThreeColumnLayout()
    }

}
@Composable
fun ThreeColumnLayout() {
    var selectedTableId by remember { mutableStateOf<Int?>(null) }
    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(2f) // Tỷ lệ phần này chiếm 2/3 màn hình
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            ContentLeft(onTableClick = { tableId ->
                // Tìm kiếm reservationId dựa trên tableId từ bảng trung gian
                selectedTableId = tableId
            }) // Nội dung phần 1 và 2
        }

        // Phần 2: 1/3 bên phải
        Column(
            modifier = Modifier
                .weight(1f) // Tỷ lệ phần này chiếm 1/3 màn hình
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            ContentRight(selectedTableId) // Truyền selectedTableId vào ContentRight
        }

    }
}
@Composable
fun ContentLeft(onTableClick: (Int) -> Unit) {
    var inputtime by remember { mutableStateOf("") }
    var tableColors by remember { mutableStateOf(tableItemList.map { Color.Transparent }) }
    val context = LocalContext.current

    val date = getCurrentDateTime()
    val dateInString = date.toString("yyyy/MM/dd HH:mm:ss")

    Column {
        Row {
            OutlinedTextField(
                value = inputtime,
                onValueChange = { inputtime = it },
                label = { Text(text = "Time") },
                leadingIcon = { Icon(imageVector = Icons.Default.Timer, contentDescription = "Home") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(4f))
            Button(
                onClick = {
                    val inputDate = inputtime.toDate("yyyy/MM/dd HH:mm:ss")
                    if (inputDate != null) {
                        tableColors = tableItemList.map { table ->
                            val reservationsForTable = Table_ReservationItemList
                                .filter { it.tableId == table.id }
                                .mapNotNull { tableReservation ->
                                    reservationItemList.find { it.id == tableReservation.reservationId }
                                }

                            if (!checkReservation(reservationsForTable, inputDate)) {
                                Color.Black
                            } else {
                                Color.Transparent
                            }
                        }
                    } else {
                        Toast.makeText(context, "Thời gian không hợp lệ", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Summit")
            }

        }

        Button(onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()) {
            Text(text = "Đặt bàn")
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(tableItemList.size) { index ->
                TableItem(
                    table = tableItemList[index],
                    onClick = { onTableClick(tableItemList[index].id) },
                    backgroundColor = tableColors[index] // Thay đổi màu nền của bàn
                )
            }
        }

    }
}
@Composable
fun ContentRight(selectedTableId: Int?) {
    if (selectedTableId != null) {
        // Lấy tất cả các reservation liên quan đến bàn đã chọn
        val reservationsForTable = Table_ReservationItemList
            .filter { it.tableId == selectedTableId }
            .mapNotNull { tableReservation ->
                reservationItemList.find { it.id == tableReservation.reservationId }
            }

        if (reservationsForTable.isNotEmpty()) {
            Column {
                Text(text = "Danh sách đặt bàn cho bàn số ${selectedTableId + 1}:")
                reservationsForTable.forEach { reservation ->
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "Tên: ${reservation.name}")
                        Text(text = "Số điện thoại: ${reservation.number}")
                        Text(text = "Thời gian: ${reservation.dateTime}")
                    }
                    Divider()
                }
            }
        } else {
            Text(text = "Không có đặt bàn cho bàn này.")
        }
    } else {
        Text(text = "Chọn bàn để xem thông tin đặt bàn.")
    }
}
fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}
fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}
fun String.toDate(format: String, locale: Locale = Locale.getDefault()): Date? {
    return try {
        val formatter = SimpleDateFormat(format, locale)
        formatter.parse(this)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
fun checkReservation (reservationList : List<Reservation> , inputTime : Date): Boolean {
    val inputTimeEnd = inputTime.time + 2 * 60 * 60 * 100
    for (reservation in reservationList) {
       val reservation1Date = reservation.dateTime.toDate("yyyy/MM/dd HH:mm:ss")
        if (reservation1Date != null && reservation1Date.time < inputTime.time &&  inputTime.time < reservation1Date.time + 2 * 60 * 60 * 1000 ) {
            return false
        }
    }
    for (reservation in reservationList) {
        val reservation1Date = reservation.dateTime.toDate("yyyy/MM/dd HH:mm:ss")
        if (reservation1Date != null && reservation1Date.time < inputTimeEnd &&  inputTimeEnd <reservation1Date.time + 2 * 60 * 60 * 1000 ) {
            return false
        }
    }
    return true
}
