package com.example.project1.ui.activity

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.project1.DataRequest.OrderRequest
import com.example.project1.data.Items
import com.example.project1.data.Orders
import com.example.project1.data.Orders_Items
import com.example.project1.data.Orders_Tables
import com.example.project1.data.Tables
import com.example.project1.ui.activity.SubComposable.OrderLayout
import com.example.project1.ui.section.OrderTabItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.notify
import java.util.Date

private val Orders_TablesList = listOf(
    Orders_Tables(
        orders_id = 0,
        tables_id = 0,
        created_at = Date(),
        updated_at = Date()
    ),Orders_Tables(
        orders_id = 1,
        tables_id = 1,
        created_at = Date(),
        updated_at = Date()
    ),Orders_Tables(
        orders_id = 2,
        tables_id = 2,
        created_at = Date(),
        updated_at = Date()
    ),Orders_Tables(
        orders_id = 3,
        tables_id = 3,
        created_at = Date(),
        updated_at = Date()
    ),Orders_Tables(
        orders_id = 4,
        tables_id = 4,
        created_at = Date(),
        updated_at = Date()
    ),Orders_Tables(
        orders_id = 5,
        tables_id = 5,
        created_at = Date(),
        updated_at = Date()
    ),Orders_Tables(
        orders_id = 6,
        tables_id = 6,
        created_at = Date(),
        updated_at = Date()
    ),Orders_Tables(
        orders_id = 7,
        tables_id = 7,
        created_at = Date(),
        updated_at = Date()
    ),
)
//@Composable
//fun OrderTabScreen() {
//
//    var tablesLists by remember { mutableStateOf<List<Tables>>(emptyList()) }
//    var ordersLists by remember { mutableStateOf<List<Orders>>(emptyList()) }
//    var itemsLists by remember { mutableStateOf<List<Items>>(emptyList()) }
//    var isLoading by remember { mutableStateOf(true) }
//    var isError by remember { mutableStateOf(false) }
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            try {
//                // Fetch data from the API
//                tablesLists = getAllTables()
//                ordersLists = getAllOrders()
//                itemsLists = getAllItems()
//                isLoading = false
//                isError = false // Reset error if successful
////                Log.e("API repeater", "Successful")
//            } catch (e: Exception) {
//                isError = true
//                isLoading = false
//            }
//            delay(1000L) // Wait for 1 second before fetching again
//        }
//    }
//
//    if (isLoading) {
//        // Show loading indicator
//        CircularProgressIndicator()
//    } else if (isError) {
//        // Show error message if something went wrong
//        Text("Error loading tables data")
//    } else if (tablesLists.isEmpty() || ordersLists.isEmpty() || itemsLists.isEmpty()) {
//        // Show empty state if no data is returned
//        Text("No Data available")
//    } else {
//        OrderTabContent(tablesLists, ordersLists,itemsLists)
//    }
//}
//
//@Composable
//fun OrderTabContent(tablesLists : List<Tables>, ordersLists : List<Orders>, itemsLists : List<Items>) {
//    // Lưu trữ danh sách orders chưa hoàn thành
//    var orders by remember { mutableStateOf(ordersLists.filter { it.status == "Pending" }) }
//
//    // Lưu trữ trạng thái checkbox cho từng order
//    val checkedStatesMap = remember { mutableStateOf(mutableMapOf<Int, List<Boolean>>()) }
//    LazyVerticalStaggeredGrid(
//        columns = StaggeredGridCells.Fixed(5),
//        contentPadding = PaddingValues(5.dp),
//        content = {
//            items(orders.size) { index ->
//                val order = orders[index]
//
//                // Lọc các Table liên kết với Order hiện tại
//                val tablesForOrder = Orders_TablesList
//                    .filter { it.orders_id == order.orders_id }
//                    .mapNotNull { tableOrder ->
//                        tablesLists.find { it.tables_id == tableOrder.tables_id }
//                    }
//
//                val dishesForOrder = Orders_ItemsList
//                    .filter { it.orders_id == order.orders_id }
//                    .mapNotNull { orderItems ->
//                        itemsLists.find { it.items_id == orderItems.items_id }
//                    }
//
//                // Lấy trạng thái checkbox hiện tại từ map hoặc khởi tạo nếu chưa có
//                val checkedStates = checkedStatesMap.value[order.orders_id] ?: List(dishesForOrder.size) { false }
//
//                OrderTabItem(
//                    itemsList = dishesForOrder,
//                    tablesList = tablesForOrder,
//                    checkedStates = checkedStates,
//                    onCheckedChange = { updatedStates ->
//                        // Cập nhật trạng thái checkbox trong map
//                        checkedStatesMap.value = checkedStatesMap.value.toMutableMap().apply {
//                            this[order.orders_id] = updatedStates
//                        }
//                    },
//                    onOrderCompleted = {
//                        // Cập nhật state của Order khi hoàn thành
//                        orders = orders.map {
//                            if (it.orders_id == order.orders_id)
//                                it.copy(status = "Completed")
//                            else it }
//                        // Lọc lại danh sách để chỉ còn các Order chưa hoàn thành
//                        orders = orders.filter { it.status == "Pending" }
//                        // Xóa trạng thái checkbox của order đã hoàn thành khỏi map
//                        checkedStatesMap.value = checkedStatesMap.value.toMutableMap().apply {
//                            remove(order.orders_id)
//                        }
//                    }
//                )
//            }
//        },
//        modifier = Modifier.fillMaxSize()
//    )
//}
@Composable
fun OrderTabScreenTest() {

    var tablesLists by remember { mutableStateOf<List<Tables>>(emptyList()) }
    var ordersLists by remember { mutableStateOf<List<Orders>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    // new data
    val ordersItemsList = remember { mutableStateMapOf<Orders, MutableList<Items>>() }
    val checkedStatesMap = remember { mutableStateMapOf<Int, List<Boolean>>() }

    LaunchedEffect(Unit) {
        while (true) {
            try {
                isLoading = true
                isError = false

                // Fetch data from the API
                val newTablesLists = getAllTables()
                val newOrdersLists = getAllOrders().filter { it.status == "Pending" }

                // Update ordersItemsList without resetting checkedStatesMap
                for (order in newOrdersLists) {
                    // Fetch items by order ID
                    val itemsByOrder = getItemByOrderId(order.orders_id)
                    ordersItemsList[order] = itemsByOrder.toMutableList()

                    // If there's no checkbox state saved for this order, initialize it
                    if (!checkedStatesMap.containsKey(order.orders_id)) {
                        checkedStatesMap[order.orders_id] = List(itemsByOrder.size) { false }
                    }
                }

                // Remove completed orders from the map (ordersItemsList)
                val completedOrders = ordersItemsList.keys.filterNot { newOrdersLists.contains(it) }
                completedOrders.forEach {
                    ordersItemsList.remove(it)
                }

                tablesLists = newTablesLists
                ordersLists = newOrdersLists
                isLoading = false
            } catch (e: Exception) {
                isError = true
                isLoading = false
            }
            delay(5000L)
        }
    }

    if (isLoading) {
        CircularProgressIndicator()
    } else if (isError) {
        Text("Error loading tables data")
    } else if (tablesLists.isEmpty() || ordersLists.isEmpty()) {
        Text("No Data available")
    } else {
        OrderTabContentTest(tablesLists, ordersItemsList, checkedStatesMap)
    }
}

@Composable
fun OrderTabContentTest(
    tablesLists: List<Tables>,
    ordersItemsList: MutableMap<Orders, MutableList<Items>>,
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
                val tablesForOrder = Orders_TablesList
                    .filter { it.orders_id == order.orders_id }
                    .mapNotNull { tableOrder ->
                        tablesLists.find { it.tables_id == tableOrder.tables_id }
                    }

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



fun formatOrdersItemsList(ordersItemsList: Map<Orders, MutableList<Items>>): String {
    val sb = StringBuilder()
    for ((order, items) in ordersItemsList) {
        sb.append("Order ID: ${order.orders_id}, Status: ${order.status}, Items:\n")
        for (item in items) {
            sb.append("  - Item Name: ${item.name}, Quantity: ${item.quantity}, Price: ${item.price}\n")
        }
        sb.append("\n")
    }
    return sb.toString()
}
