package com.example.project1.ui.activity.orderMenu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.project1.data.Items
import com.example.project1.data.Menu
import com.example.project1.data.Reservation
import com.example.project1.data.Tables
import com.example.project1.data.Tables_Reservations
import com.example.project1.ui.section.MenuItem
import com.example.project1.ui.section.TableOrderItem
import java.lang.StringBuilder

@Composable
fun GridItem(
    items: List<Any>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (Any) -> Unit
) {
    val columns = if (items.isNotEmpty() && items.first() is Reservation) {
        GridCells.Fixed(2)
    } else {
        GridCells.Fixed(5)
    }

    LazyVerticalGrid(
        columns = columns,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier,
        content = {
            items(items.size) { index ->
                itemContent(items[index])
            }
        }
    )
}
@Composable
fun ContentLeft(
    reservations: List<Reservation>,
    tableItemList: List<Tables>,
    tablesReservations: List<Tables_Reservations>,
    menuList: List<Items>,
    selectedReservation: Reservation?,
    onReservationClick: (Reservation) -> Unit,
    onMenuItemClick: (Items) -> Unit
) {
    val (items, setItems) = remember { mutableStateOf<List<Any>>(reservations) }
    val (itemsType, setItemsType) = remember { mutableStateOf("reservations") } // Trạng thái lưu loại danh sách hiện tại
    val searchText = remember { mutableStateOf("") } // Trạng thái lưu trữ văn bản tìm kiếm

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Thanh tìm kiếm
        OutlinedTextField(
            value = searchText.value,
            onValueChange = { newText ->
                searchText.value = newText
                // Lọc danh sách dựa trên văn bản tìm kiếm và loại danh sách hiện tại
                setItems(
                    when (itemsType) {
                        "reservations" -> reservations.filter { it.name?.contains(newText, ignoreCase = true) == true }
                        "menu" -> menuList.filter { it.name?.contains(newText, ignoreCase = true) == true }
                        else -> reservations
                    }
                )
            },
            label = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Hiển thị GridItem
        GridItem(
            items = items,
            modifier = Modifier
                .weight(1f)
        ) { item ->
            when (item) {
                is Reservation -> {
                    val isSelected = selectedReservation == item
                    // Tạo danh sách bàn (tableList) cho mỗi Reservation
                        val tableList = tablesReservations
                        .filter { it.reservations_id == item.reservations_id } // Lọc các Tables_Reservations liên quan đến Reservation
                        .mapNotNull { tablesRes ->
                            tableItemList.find { it.tables_id == tablesRes.tables_id } // Tìm các Tables tương ứng
                        }
                        .joinToString(", ") { it.name } // Tạo chuỗi danh sách các bàn

                    // Hiển thị TableOrderItem với chuỗi danh sách bàn
                    TableOrderItem(
                        reservation = item,
                        tablesList = tableList,
                        onClick = { onReservationClick(item) } ,
                        isSelected = isSelected
                    )
                }
                is Items -> {
                    // Hiển thị các MenuItem
                    MenuItem(item, onClick = { onMenuItemClick(item) })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nút chuyển đổi giữa các danh sách
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    setItems(reservations) // Quay lại danh sách Reservations
                    setItemsType("reservations") // Cập nhật loại danh sách
                },
                modifier = Modifier.weight(1f),
                enabled = itemsType != "reservations" // Chỉ kích hoạt nút nếu đang ở danh sách khác
            ) {
                Text("Back to Reservations")
            }
            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    setItems(menuList) // Chuyển sang danh sách Menu
                    setItemsType("menu") // Cập nhật loại danh sách
                },
                modifier = Modifier.weight(1f),
                enabled = itemsType != "menu" // Chỉ kích hoạt nút nếu đang không ở danh sách Menu
            ) {
                Text("Show Menu")
            }
        }
    }
}