package com.example.project1.ui.activity.OrderTab

import android.util.Log
import androidx.compose.foundation.layout.Column
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
import com.example.project1.DataResponse.ItemsResponseForItemByOrder
import com.example.project1.data.Orders
import com.example.project1.data.Tables
import com.example.project1.ui.activity.getAllOrders
import com.example.project1.ui.activity.getItemByOrderId
import com.example.project1.ui.activity.getTableByReservationId
import kotlinx.coroutines.delay

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

                    //yêu cầu để ra là order sẽ hiện lên 1 hoặc nhiều item dương, nhưng sẽ không có item âm, item âm sẽ trừ luôn vào item dương, trừ dần đến hết
                    if (itemsByOrder != null) {
                        // Lưu 2 giá trị item âm và dương riêng biệt để xử lý
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


                        if (!checkedStatesMap.containsKey(order.orders_id)) {
                            checkedStatesMap[order.orders_id] = List(finalItemsList.size) { false }
                        } else {
                            val currentCheckedStates = checkedStatesMap[order.orders_id]!!
                            if (currentCheckedStates.size != finalItemsList.size) {
                                checkedStatesMap[order.orders_id] = List(finalItemsList.size) { index ->
                                    currentCheckedStates.getOrNull(index) ?: false
                                }
                            }
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
            delay(10000L)
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
