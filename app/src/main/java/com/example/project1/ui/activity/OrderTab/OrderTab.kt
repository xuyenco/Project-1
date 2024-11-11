package com.example.project1.ui.activity.OrderTab

import android.util.Log
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.project1.DataRequest.OrderRequest
import com.example.project1.DataResponse.ItemsResponseForItemByOrder
import com.example.project1.data.Orders
import com.example.project1.data.Tables
import com.example.project1.ui.activity.editOrder
import com.example.project1.ui.activity.getAllOrders
import com.example.project1.ui.activity.getItemByOrderId
import com.example.project1.ui.activity.getTableByReservationId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun OrderTabScreen() {
    var isFirstLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    var ordersLists by remember { mutableStateOf(emptyList<Orders>()) }
    val ordersItemsList = remember { SnapshotStateMap<Orders, SnapshotStateList<ItemsResponseForItemByOrder>>() }
    val ordersTablesList = remember { SnapshotStateMap<Orders, SnapshotStateList<Tables>>() }
    val checkedStatesMap = remember { SnapshotStateMap<Int, List<Boolean>>() }

    LaunchedEffect(Unit) {
        while (true) {
            try {
                if (isFirstLoading) isFirstLoading = true
                isError = false
                isRefreshing = true

                // Fetch data từ API
                val newOrdersLists = getAllOrders().filter { it.status == "Đang chờ" }

                // Cập nhật ordersItemsList mà không reset trạng thái checkbox
                for (order in newOrdersLists) {
                    val itemsByOrder = getItemByOrderId(order.orders_id)
                    val tableByOrder = getTableByReservationId(order.reservations_id)

                    if (itemsByOrder != null) {
                        // test some shit
                        val negativeItems = itemsByOrder.items.filter { it.quantity_used < 0 }
                        val positiveItems = itemsByOrder.items.filter { it.quantity_used > 0 }.toMutableList()

                        // Duyệt qua từng phần tử âm và trừ vào phần tử dương tương ứng
                        for (negItem in negativeItems) {
                            var remainingNegativeQuantity = negItem.quantity_used

                            // Duyệt qua các phần tử dương để trừ
                            for (posItem in positiveItems) {
                                if (posItem.items_id == negItem.items_id && remainingNegativeQuantity < 0) {
                                    // Tính số lượng còn lại của phần tử dương sau khi trừ
                                    val newQuantity = posItem.quantity_used + remainingNegativeQuantity
                                    if (newQuantity >= 0) {
                                        // Nếu kết quả >= 0, cập nhật và kết thúc trừ cho item này
                                        posItem.quantity_used = newQuantity
                                        remainingNegativeQuantity = 0
                                        break
                                    } else {
                                        // Nếu kết quả < 0, tiếp tục trừ từ item dương kế tiếp
                                        posItem.quantity_used = 0
                                        remainingNegativeQuantity = newQuantity // newQuantity là giá trị âm còn lại để trừ
                                    }
                                }
                            }
                        }

                        // Lọc lại các item có quantity > 0 sau khi trừ
                        val finalItemsList = positiveItems.filter { it.quantity_used > 0 }.toMutableList()

                        // Lưu lại kết quả cuối cùng
                        ordersItemsList[order] = finalItemsList.toMutableStateList()


                        //old shit
//                        val finalItemsList = mutableListOf<ItemsResponseForItemByOrder>()
//
//                        for (item in itemsByOrder.items) {
//                            if (item.quantity_used < 0) {
//                                // Nếu item có quantity_used âm, tìm item tương ứng trong finalItemsList và cộng dồn
//                                val existingItem = finalItemsList.find { it.items_id == item.items_id }
//                                existingItem?.let {
//                                    it.quantity_used += item.quantity_used
//                                }
//                            } else {
//                                // Nếu item có quantity_used dương, thêm vào finalItemsList
//                                finalItemsList.add(item)
//                            }
//                        }
//
//                        // Lưu finalItemsList vào ordersItemsList sau khi xử lý
//                        ordersItemsList[order] = finalItemsList.toMutableStateList()

                        // Nếu chưa có trong map, tạo list checkbox ban đầu cho order này
                        if (!checkedStatesMap.containsKey(order.orders_id)) {
                            checkedStatesMap[order.orders_id] = List(positiveItems.size) { false }
                        }
                    }

                    if (tableByOrder != null) {
                        ordersTablesList[order] = tableByOrder.tables.toMutableStateList()
                    }
                }



                // Xóa các order đã hoàn thành khỏi ordersItemsList
                val completedOrders = ordersItemsList.keys.filterNot { newOrdersLists.contains(it) }
                completedOrders.forEach {
                    ordersItemsList.remove(it)
                }

                ordersLists = newOrdersLists

                if (isFirstLoading) isFirstLoading = false
                isRefreshing = false
            } catch (e: Exception) {
                isError = true
                isRefreshing = false
                Log.e("Error Api OrderTab", e.toString())
                if (isFirstLoading) isFirstLoading = false
            }
            delay(500000L)
        }
    }

    when {
        isFirstLoading -> {
            CircularProgressIndicator()
        }
        isError -> {
            Text("Error loading data")
        }
        ordersLists.isEmpty() -> {
            Text("No Data available")
        }
        else -> {
            Column {
                if (isRefreshing) {
                    Text("Refreshing data...")
                }
                // Hiển thị nội dung chính
                OrderTabContent(ordersItemsList, ordersTablesList, checkedStatesMap)
            }
        }
    }
}

@Composable
fun OrderTabContent(
    ordersItemsList: SnapshotStateMap<Orders, SnapshotStateList<ItemsResponseForItemByOrder>>,
    ordersTablesList: SnapshotStateMap<Orders, SnapshotStateList<Tables>>,
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
                //Lấy description cho order
                val description = order.description

                // Lấy trạng thái checkbox hiện tại từ map
                val checkedStates = checkedStatesMap[order.orders_id] ?: List(itemsForOrder.size) { false }

                OrderTabItem(
                    itemsList = itemsForOrder,
                    tablesList = tablesForOrder,
                    description = description,
                    checkedStates = checkedStates,
                    onCheckedChange = { updatedStates ->
                        checkedStatesMap[order.orders_id] = updatedStates
                    },
                    onOrderCompleted = {
                        val orderTemp = ordersItemsList.keys.firstOrNull { it.orders_id == order.orders_id }

                        val orderRequest = orderTemp?.let {
                            OrderRequest(status = "Hoàn thành")
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

