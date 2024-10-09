package com.example.project1.ui.activity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.project1.DataRequest.OrderRequest
import com.example.project1.DataResponse.ItemsResponseForItemOrder
import com.example.project1.DataResponse.TableResponseForOrderTable
import com.example.project1.data.Orders
import com.example.project1.data.Tables
import com.example.project1.ui.section.OrderTabItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun OrderTabScreenTest() {
    var isFirstLoading by remember { mutableStateOf(true) } // Chỉ true lần đầu load dữ liệu
    var isRefreshing by remember { mutableStateOf(false) }  // Trạng thái khi refresh
    var isError by remember { mutableStateOf(false) }

    // Dữ liệu của orders, tables
    var ordersLists by remember { mutableStateOf(emptyList<Orders>()) }
    var tablesLists by remember { mutableStateOf(emptyList<Tables>()) }
    var ordersItemsList by remember { mutableStateOf(mutableMapOf<Orders, MutableList<ItemsResponseForItemOrder>>()) }
    var ordersTablesList by remember { mutableStateOf(mutableMapOf<Orders, MutableList<TableResponseForOrderTable>>()) }
    var checkedStatesMap by remember { mutableStateOf(mutableMapOf<Int, List<Boolean>>()) }

    LaunchedEffect(Unit) {
        while (true) {
            try {
                if (isFirstLoading) isFirstLoading = true
                isError = false
                isRefreshing = true

                // Fetch data từ API
                val newTablesLists = getAllTables()
                val newOrdersLists = getAllOrders().filter { it.status == "pending" }

                // Cập nhật ordersItemsList mà không reset trạng thái checkbox
                for (order in newOrdersLists) {
                    val itemsByOrder = getItemByOrderId(order.orders_id)
                    val tableByOrder = getTableByOrderId(order.orders_id)

                    if (itemsByOrder != null) {
                        ordersItemsList[order] = itemsByOrder.items.toMutableList()
                        // Nếu chưa có trong map, tạo list checkbox ban đầu cho order này
                        if (!checkedStatesMap.containsKey(order.orders_id)) {
                            checkedStatesMap[order.orders_id] = List(itemsByOrder.items.size) { false }
                        }
                    }

                    if (tableByOrder != null) {
                        ordersTablesList[order] = tableByOrder.tables.toMutableList()
                    }
                }

                // Xóa các order đã hoàn thành khỏi ordersItemsList
                val completedOrders = ordersItemsList.keys.filterNot { newOrdersLists.contains(it) }
                completedOrders.forEach {
                    ordersItemsList.remove(it)
                }

                tablesLists = newTablesLists
                ordersLists = newOrdersLists

                if (isFirstLoading) isFirstLoading = false
                isRefreshing = false
            } catch (e: Exception) {
                isError = true
                isRefreshing = false
                if (isFirstLoading) isFirstLoading = false
            }
            delay(1000L)
        }
    }

    when {
        isFirstLoading -> {
            CircularProgressIndicator()
        }
        isError -> {
            Text("Error loading tables data")
        }
        tablesLists.isEmpty() || ordersLists.isEmpty() -> {
            Text("No Data available")
        }
        else -> {
            Column {
                if (isRefreshing) {
                    Text("Refreshing data...")
                }
                // Hiển thị nội dung chính
                OrderTabContentTest(ordersItemsList, ordersTablesList, checkedStatesMap)
            }
        }
    }
}

@Composable
fun OrderTabContentTest(
    ordersItemsList: MutableMap<Orders, MutableList<ItemsResponseForItemOrder>>,
    ordersTablesList: MutableMap<Orders, MutableList<TableResponseForOrderTable>>,
    checkedStatesMap: MutableMap<Int, List<Boolean>>
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(5),
        contentPadding = PaddingValues(5.dp),
        content = {
            items(ordersItemsList.size) { index ->
                val order = ordersItemsList.keys.toList()[index]
                val itemsForOrder = ordersItemsList[order] ?: emptyList()

                // Lọc các Table liên kết với Order hiện tại
                val tablesForOrder = ordersTablesList[order] ?: emptyList()

                // Lấy trạng thái checkbox hiện tại từ map
                val checkedStates = checkedStatesMap[order.orders_id] ?: List(itemsForOrder.size) { false }

                OrderTabItem(
                    itemsList = itemsForOrder,
                    tablesList = tablesForOrder,
                    checkedStates = checkedStates,
                    onCheckedChange = { updatedStates ->
                        checkedStatesMap[order.orders_id] = updatedStates
                    },
                    onOrderCompleted = {
                        val orderTemp = ordersItemsList.keys.firstOrNull { it.orders_id == order.orders_id }

                        val orderRequest = orderTemp?.let {
                            OrderRequest(status = "Completed")
                        }
                        CoroutineScope(Dispatchers.Main).launch {
                            withContext(Dispatchers.IO) {
                                if (orderRequest != null) {
                                    editOrder(order.orders_id, orderRequest)
                                }
                            }
                            ordersItemsList.remove(orderTemp)
                            checkedStatesMap.remove(order.orders_id)
                        }
                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

