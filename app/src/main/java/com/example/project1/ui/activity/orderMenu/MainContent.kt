package com.example.project1.ui.activity.orderMenu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project1.data.Items
import com.example.project1.data.Reservation
import com.example.project1.data.Tables
import com.example.project1.data.Tables_Reservations
import com.example.project1.ui.activity.getAllItems
import com.example.project1.ui.activity.getAllReservations
import com.example.project1.ui.activity.getAllTables
import com.example.project1.ui.activity.getAllTablesReservations
import kotlinx.coroutines.delay
import java.util.Date

val itemsList = listOf(
    Items(1, "Cá lóc nướng trui", "https://statics.vincom.com.vn/xu-huong/0-0-0-0-mon-nhau-don-gian/image11.png", "portion", "Main Course", 100000, Date(), Date()),
    Items(2, "Cá hồi sốt chanh mật ong", "https://statics.vincom.com.vn/xu-huong/0-0-0-0-mon-nhau-don-gian/image8.png", "portion", "Main Course", 150000, Date(), Date()),
    Items(3, "Gà xé phay chua ngọt", "https://statics.vincom.com.vn/xu-huong/0-0-0-0-mon-nhau-don-gian/image15.png", "portion", "Main Course", 90000, Date(), Date()),
    Items(4, "Thịt ngan hấp bia", "https://statics.vincom.com.vn/xu-huong/0-0-0-0-mon-nhau-don-gian/image4.png", "portion", "Main Course", 200000, Date(), Date()),
    Items(5, "Gà nấu nấm", "https://statics.vincom.com.vn/xu-huong/0-0-0-0-mon-nhau-don-gian/image1.png", "portion", "Main Course", 125000, Date(), Date()),
    Items(6, "Giò heo muối xông khói", "https://statics.vincom.com.vn/xu-huong/0-0-0-0-mon-nhau-don-gian/image20.png", "portion", "Course", 85000, Date(), Date())
)

// List of 5 Reservations
val reservationsList = listOf(
    Reservation(1, 10, "Alice Smith", "0123456789", "alice@example.com","pending", Date(), Date(), Date()),
    Reservation(2, 8, "Bob Johnson", "0987654321", "bob@example.com","pending", Date(), Date(), Date()),
    Reservation(3, 6, "Charlie Brown", "1122334455", "charlie@example.com","arrived", Date(), Date(), Date()),
    Reservation(4, 12, "David Wilson", "2233445566", "david@example.com","pending", Date(), Date(), Date()),
    Reservation(5, 14, "Eva Green", "3344556677", "eva@example.com","ordered", Date(), Date(), Date())
)

// List of Tables
val tablesList = listOf(
    Tables(1, "Table 1", 4, "Near Window", "Available", Date(), Date()),
    Tables(2, "Table 2", 6, "Center Hall", "Available", Date(), Date()),
    Tables(3, "Table 3", 2, "Near Entrance", "Reserved", Date(), Date()),
    Tables(4, "Table 4", 8, "VIP Area", "Reserved", Date(), Date()),
    Tables(5, "Table 5", 5, "Near Kitchen", "Available", Date(), Date()),
    Tables(6, "Table 6", 4, "Balcony", "Reserved", Date(), Date()),
    Tables(7, "Table 7", 10, "Private Room", "Reserved", Date(), Date()),
    Tables(8, "Table 8", 6, "Outdoor", "Available", Date(), Date()),
    Tables(9, "Table 9", 12, "VIP Area", "Reserved", Date(), Date()),
    Tables(10, "Table 10", 4, "Near Bar", "Available", Date(), Date())
)

// List of Tables_Reservations with multiple tables assigned to each reservation
val tablesReservationsList = listOf(
    // Reservation 1 has 3 tables
    Tables_Reservations(1, 1, Date(), Date()),
    Tables_Reservations(2, 1, Date(), Date()),
    Tables_Reservations(3, 1, Date(), Date()),

    // Reservation 2 has 3 tables
    Tables_Reservations(4, 2, Date(), Date()),
    Tables_Reservations(5, 2, Date(), Date()),
    Tables_Reservations(6, 2, Date(), Date()),

    // Reservation 3 has 2 tables
    Tables_Reservations(7, 3, Date(), Date()),
    Tables_Reservations(8, 3, Date(), Date()),

    // Reservation 4 has 4 tables
    Tables_Reservations(9, 4, Date(), Date()),
    Tables_Reservations(1, 4, Date(), Date()),
    Tables_Reservations(2, 4, Date(), Date()),
    Tables_Reservations(3, 4, Date(), Date()),

    // Reservation 5 has 3 tables
    Tables_Reservations(4, 5, Date(), Date()),
    Tables_Reservations(5, 5, Date(), Date()),
    Tables_Reservations(6, 5, Date(), Date())
)
@Composable
fun OrderLayoutScreen() {
    var tableList by remember { mutableStateOf<List<Tables>>(emptyList()) }
    var menuList by remember { mutableStateOf<List<Items>>(emptyList()) }
    var tablesReservationsList by remember { mutableStateOf<List<Tables_Reservations>>(emptyList()) }
    var reservationsList by remember { mutableStateOf<List<Reservation>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            try {
                // Fetch data từ API
                tableList = getAllTables()
                menuList = getAllItems()
                reservationsList = getAllReservations()
                tablesReservationsList = getAllTablesReservations() 

                isLoading = false
                isError = false // Reset error nếu thành công
            } catch (e: Exception) {
                isError = true
                isLoading = false
            }

            delay(5000L) // Chờ 5 giây trước khi fetch lại
        }
    }

    // Hiển thị giao diện dựa trên trạng thái
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator() // ProgressBar tròn
        }
    } else if (isError) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Có lỗi xảy ra...")
        }
    } else {
        OrderLayout(reservationsList, tableList, tablesReservationsList, menuList)
    }
}
@Composable
fun OrderLayout(
    reservations: List<Reservation>,
    tableItemList: List<Tables>,
    tablesReservations: List<Tables_Reservations>,
    menuList: List<Items>
) {
    val selectedTables = remember { mutableStateOf<List<Tables>>(emptyList()) }
    val (selectedMenus, setSelectedMenus) = remember { mutableStateOf<List<Items>>(emptyList()) }
    val selectedReservation = remember { mutableStateOf<Reservation?>(null) } // Trạng thái cho Reservation đã chọn
    val quantities = remember { mutableStateOf<Map<Int, Int>>(emptyMap()) } // Map cho quantity

    fun resetSelections() {
        selectedTables.value = emptyList()  // Reset bàn đã chọn
        setSelectedMenus(emptyList())  // Reset danh sách món đã chọn
        quantities.value = emptyMap()
        selectedReservation.value = null
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // Phần 1: 2/3 bên trái
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            ContentLeft(
                reservations,
                tableItemList,
                tablesReservations,
                menuList,
                selectedReservation = selectedReservation.value, // Truyền Reservation đã chọn
                onReservationClick = { reservation ->
                    selectedReservation.value = reservation // Cập nhật Reservation đã chọn

                    // Lấy danh sách bàn được đặt trong reservation
                    val reservedTables = tablesReservations
                        .filter { it.reservations_id == reservation.reservations_id }
                        .mapNotNull { tablesRes ->
                            tableItemList.find { it.tables_id == tablesRes.tables_id }
                        }

                    // Cập nhật selectedTables khi click vào Reservation
                    selectedTables.value = reservedTables
                },
                onMenuItemClick = { menuItem ->
                    if (!selectedMenus.contains(menuItem)) {
                        setSelectedMenus(selectedMenus + menuItem)
                        quantities.value = quantities.value.toMutableMap().apply {
                            this[menuItem.items_id] = 1 // Mặc định số lượng là 1 khi thêm món mới
                        }
                    }
                }
            )
        }

        // Phần 2: 1/3 bên phải
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            ContentRight(
                reservation = selectedReservation.value,
                selectedTables = selectedTables.value,
                selectedMenus = selectedMenus,
                quantities = quantities.value,
                onIncreaseQuantity = { itemId ->
                    quantities.value = quantities.value.toMutableMap().apply {
                        this[itemId] = (this[itemId] ?: 1) + 1
                    }
                },
                onDecreaseQuantity = { itemId ->
                    quantities.value = quantities.value.toMutableMap().apply {
                        val currentQuantity = this[itemId] ?: 1
                        if (currentQuantity > 1) {
                            this[itemId] = currentQuantity - 1
                        }
                    }
                },
                onRemoveMenuItem = { menuItem ->
                    setSelectedMenus(selectedMenus - menuItem)
                    quantities.value = quantities.value - menuItem.items_id
                },
                onCancel = { resetSelections() }
            )
        }
    }
}
@Preview(
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_C
)
@Composable
fun PreviewMenuLayout(){
    OrderLayout(reservationsList, tablesList, tablesReservationsList, itemsList)
}
