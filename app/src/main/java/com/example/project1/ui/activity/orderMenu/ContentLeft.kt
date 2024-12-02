package com.example.project1.ui.activity.orderMenu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.project1.data.Items
import com.example.project1.data.Reservation
import com.example.project1.data.Tables
import com.example.project1.data.Tables_Reservations
import com.example.project1.retrofit.client.ApiClient
import com.example.project1.ui.section.MenuItem
import com.example.project1.ui.section.ReservationItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GridItem(
    items: List<Any>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (Any) -> Unit
) {
    val columns = if (items.isNotEmpty() && items.first() is Reservation) {
        GridCells.Fixed(2)
    } else {
        GridCells.Fixed(4)
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
    val (items, setItems) = remember { mutableStateOf<List<Any>>(reservations.sortedBy { it.reservations_id }) }
    val (itemsType, setItemsType) = remember { mutableStateOf("reservations") }
    val (filterStatus, setFilterStatus) = remember { mutableStateOf("Tất cả") }

    val orderStatuses = remember { mutableStateMapOf <Int, String>() }
    val scope = rememberCoroutineScope()

    val (nameSearch, setNameSearch) = remember { mutableStateOf("") }
    val (tableSearch, setTableSearch) = remember { mutableStateOf("") }

    LaunchedEffect(itemsType) {
        setNameSearch("")
        setTableSearch("")
    }

    LaunchedEffect(Unit) {
        while (true) {
            reservations.forEach { reservation ->
                scope.launch {
                    try {
                        val result = ApiClient.orderService.getOrderIdByReservationId(reservation.reservations_id)
                        val orderStatus = result.status
                        orderStatuses[reservation.reservations_id] = orderStatus
                    } catch (e: Exception) {
                        orderStatuses[reservation.reservations_id] = "Error"
                    }
                }
            }
            delay(5000L)
        }
    }
    LaunchedEffect(nameSearch, tableSearch, filterStatus, itemsType) {
        if (itemsType == "reservations") {
            setItems(
                reservations.filter { reservation ->
                    val tableList = tablesReservations
                        .filter { it.reservations_id == reservation.reservations_id }
                        .mapNotNull { tablesRes -> tableItemList.find { it.tables_id == tablesRes.tables_id } }
                        .joinToString(", ") { it.name }

                    (filterStatus == "Tất cả" || reservation.status == filterStatus) &&
                            (nameSearch.isEmpty() || reservation.name?.contains(nameSearch, ignoreCase = true) == true) &&
                            (tableSearch.isEmpty() || tableList.contains(tableSearch, ignoreCase = true))
                }.sortedBy { it.reservations_id }
            )
        } else if (itemsType == "menu") {
            setItems(menuList.filter { item ->
                (filterStatus == "Tất cả" || item.category == filterStatus) &&
                        (item.name?.contains(nameSearch, ignoreCase = true) == true) })
        }
    }
    LaunchedEffect(reservations, menuList, filterStatus) {
        if (itemsType == "reservations") {
            val filteredReservations = when (filterStatus) {
                "Tất cả" -> reservations
                "Chưa đặt món" -> reservations.filter { it.status == "Đã đến" }
                "Đã đặt món" -> reservations.filter { it.status == "Đã đặt món" }
                "Đã thanh toán" -> reservations.filter { it.status == "Hoàn thành" }
                else -> reservations
            }
            setItems(filteredReservations.sortedBy { it.reservations_id })
        } else if (itemsType == "menu") {
            val filteredMenuItems = when (filterStatus) {
                "Tất cả" -> menuList
                "Đồ uống" -> menuList.filter { it.category == "Đồ uống" }
                "Món ăn" -> menuList.filter { it.category == "Món ăn" }
                else -> menuList
            }
            setItems(filteredMenuItems)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // SearchBox cho Name
            OutlinedTextField(
                value = nameSearch,
                onValueChange = setNameSearch,
                label = { Text("Name") },
                modifier = Modifier.weight(7f)
            )

            // SearchBox cho Table (chỉ hiện khi itemsType == "reservations")
            if (itemsType == "reservations") {
                OutlinedTextField(
                    value = tableSearch,
                    onValueChange = setTableSearch,
                    label = { Text("Table") },
                    modifier = Modifier.weight(3f)
                )
            }
        }

        // Filter Dropdown
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Filter by:", modifier = Modifier.padding(end = 8.dp))

            var expanded by remember { mutableStateOf(false) }

            Box {
                Text(
                    text = filterStatus,
                    modifier = Modifier
                        .clickable { expanded = true }
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.primary
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    val filterOptions = if (itemsType == "reservations") {
                        listOf("Tất cả", "Chưa đặt món", "Đã đặt món", "Đã thanh toán")
                    } else {
                        listOf("Tất cả", "Đồ uống", "Món ăn")
                    }

                    filterOptions.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status) },
                            onClick = {
                                setFilterStatus(status)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        GridItem(
            items = items,
            modifier = Modifier.weight(1f)
        ) { item ->
            when (item) {
                is Reservation -> {
                    val isSelected = selectedReservation == item
                    val tableList = tablesReservations
                        .filter { it.reservations_id == item.reservations_id }
                        .mapNotNull { tablesRes -> tableItemList.find { it.tables_id == tablesRes.tables_id } }
                        .joinToString(", ") { it.name }

                    val orderStatus = orderStatuses[item.reservations_id] ?: "Loading..."

                    ReservationItem(
                        reservation = item,
                        tablesList = tableList,
                        onClick = { onReservationClick(item) },
                        isSelected = isSelected,
                        orderStatus = orderStatus
                    )
                }

                is Items -> {
                    MenuItem(item, onClick = { onMenuItemClick(item) })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    setItems(reservations.sortedBy { it.reservations_id })
                    setItemsType("reservations")
                    setFilterStatus("Tất cả")
                },
                modifier = Modifier.weight(1f),
                enabled = itemsType != "reservations"
            ) {
                Text("Back to Reservations")
            }
            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    setItems(menuList)
                    setItemsType("menu")
                    setFilterStatus("Tất cả")
                },
                modifier = Modifier.weight(1f),
                enabled = itemsType != "menu"
            ) {
                Text("Show Menu")
            }
        }
    }
}
