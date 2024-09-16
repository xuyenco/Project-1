package com.example.project1.ui.activity

import android.os.Bundle
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.project1.data.Reservation
import com.example.project1.data.SideNavbar
import com.example.project1.data.Tables
import com.example.project1.data.Tables_Reservations
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
private val tablesItemLists = listOf(
    Tables(
        tables_id = 0,
        name = "Table 1",
        quantity = 4,
        location = "Location 1",
        status = "Empty",
        create_at = Date(),
        update_at = Date()
    ),Tables(
        tables_id = 1,
        name = "Table 2",
        quantity = 4,
        location = "Location 1",
        status = "Empty",
        create_at = Date(),
        update_at = Date()
    ),Tables(
        tables_id = 2,
        name = "Table 3",
        quantity = 4,
        location = "Location 1",
        status = "Empty",
        create_at = Date(),
        update_at = Date()
    ),Tables(
        tables_id = 3,
        name = "Table 4",
        quantity = 4,
        location = "Location 1",
        status = "Empty",
        create_at = Date(),
        update_at = Date()
    ),Tables(
        tables_id = 4,
        name = "Table 5",
        quantity = 4,
        location = "Location 1",
        status = "Empty",
        create_at = Date(),
        update_at = Date()
    ),Tables(
        tables_id = 5,
        name = "Table 6",
        quantity = 4,
        location = "Location 1",
        status = "Empty",
        create_at = Date(),
        update_at = Date()
    ),

)
private val reservationItemList = listOf(
    Reservation(
        reservation_id = 0,
        name = "Nguyễn Văn A",
        phone = "0909999999",
        email = "a@a.com",
        quantity = 4,
        time = Date(),
        created_at = Date(),
        updated_at = Date()
    ),Reservation(
        reservation_id = 1,
        name = "Nguyễn Văn B",
        phone = "0909999999",
        email = "a@a.com",
        quantity = 4,
        time = Date(),
        created_at = Date(),
        updated_at = Date()
    ),Reservation(
        reservation_id = 2,
        name = "Nguyễn Văn C",
        phone = "0909999999",
        email = "a@a.com",
        quantity = 4,
        time = Date(),
        created_at = Date(),
        updated_at = Date()
    ),Reservation(
        reservation_id = 3,
        name = "Nguyễn Văn D",
        phone = "0909999999",
        email = "a@a.com",
        quantity = 4,
        time = Date(),
        created_at = Date(),
        updated_at = Date()
    ),
)
private val TablesReservationsItemLists = listOf(
    Tables_Reservations(
        tables_id = 0,
        reservations_id = 0,
        created_at = Date(),
        updated_at = Date()
    ),Tables_Reservations(
        tables_id = 1,
        reservations_id = 1,
        created_at = Date(),
        updated_at = Date()
    ),Tables_Reservations(
        tables_id = 2,
        reservations_id = 2,
        created_at = Date(),
        updated_at = Date()
    ),Tables_Reservations(
        tables_id = 3,
        reservations_id = 3,
        created_at = Date(),
        updated_at = Date()
    ),Tables_Reservations(
        tables_id = 4,
        reservations_id = 4,
        created_at = Date(),
        updated_at = Date()
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
    var reservationstate by remember { mutableStateOf(tablesItemLists.map { true }) }
    var selectedTableIds = remember { mutableStateListOf<Int?>() }
    val context = LocalContext.current

    val date = getCurrentDateTime()
    val dateInString = date.toString("yyyy/MM/dd HH:mm:ss")

    Column {
        Row {
            OutlinedTextField(
                value = inputtime,
                onValueChange = { inputtime = it },
                label = { Text(text = "Time") },
                leadingIcon = { Icon(imageVector = Icons.Default.Timer, contentDescription = "Time Icon") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(4f)
            )
            Button(
                onClick = {
                    selectedTableIds.clear()
                    val inputDate = inputtime.toDate("yyyy/MM/dd HH:mm:ss")
                    if (inputDate != null) {
                        reservationstate = tablesItemLists.map { table ->
                            val reservationsForTable = TablesReservationsItemLists
                                .filter { it.tables_id == table.tables_id }
                                .mapNotNull { tableReservation ->
                                    reservationItemList.find { it.reservation_id == tableReservation.reservations_id }
                                }
                            checkReservation(reservationsForTable, inputDate)
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
                Text(text = "Submit")
            }
        }

        Button(onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Đặt bàn")
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(tablesItemLists.size) { index ->
                TableItem(
                    tables = tablesItemLists[index],
                    onClick = { onTableClick(tablesItemLists[index].tables_id) },
                    selectTable = {
                        if (reservationstate[index]) {
                            if (selectedTableIds.contains(tablesItemLists[index].tables_id)) {
                                selectedTableIds.remove(tablesItemLists[index].tables_id)
                            } else {
                                selectedTableIds.add(tablesItemLists[index].tables_id)
                            }
                        }
                    },
                    reservationState = reservationstate[index],
                    isSelected = selectedTableIds.contains(tablesItemLists[index].tables_id)
                )
            }
        }
    }
}

@Composable
fun ContentRight(selectedTableId: Int?) {
    if (selectedTableId != null) {
        // Lấy tất cả các reservation liên quan đến bàn đã chọn
        val reservationsForTable = TablesReservationsItemLists
            .filter { it.tables_id == selectedTableId }
            .mapNotNull { tableReservation ->
                reservationItemList.find { it.reservation_id == tableReservation.reservations_id }
            }

        if (reservationsForTable.isNotEmpty()) {
            Column {
                Text(text = "Danh sách đặt bàn cho bàn số ${selectedTableId + 1}:")
                reservationsForTable.forEach { reservation ->
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "Tên: ${reservation.name}")
                        Text(text = "Số điện thoại: ${reservation.phone}")
                        Text(text = "Thời gian: ${reservation.time.toString("yyyy/MM/dd HH:mm:ss")}")
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
       val reservation1Date = reservation.time
        if (reservation1Date != null && reservation1Date.time < inputTime.time &&  inputTime.time < reservation1Date.time + 2 * 60 * 60 * 1000 ) {
            return false
        }
    }
    for (reservation in reservationList) {
        val reservation1Date = reservation.time
        if (reservation1Date != null && reservation1Date.time < inputTimeEnd &&  inputTimeEnd <reservation1Date.time + 2 * 60 * 60 * 1000 ) {
            return false
        }
    }
    return true
}
