package com.example.project1.ui.activity.OrderTab

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.project1.DataRequest.OrderRequest
import com.example.project1.DataResponse.ItemsResponseForItemByOrder
import com.example.project1.data.Orders
import com.example.project1.data.Tables
import com.example.project1.ui.activity.editOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun OrderTabContent(
    ordersItemsList: SnapshotStateMap<Orders, SnapshotStateList<ItemsResponseForItemByOrder>>,
    ordersTablesList: SnapshotStateMap<Orders, SnapshotStateList<Tables>>,
    checkedStatesMap: MutableMap<Int, List<Boolean>>
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(5),
        contentPadding = PaddingValues(10.dp),
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
                    onCheckedChange = { updatedStates -> checkedStatesMap[order.orders_id] = updatedStates },
                    onOrderCompleted = {
                        val orderTemp = ordersItemsList.keys.firstOrNull { it.orders_id == order.orders_id }

                        val orderRequest = orderTemp?.let {
                            OrderRequest(status = "Hoàn thành")
                        }
                        CoroutineScope(Dispatchers.Main).launch {
//
                            withContext(Dispatchers.IO) {
                                if (orderRequest != null) {
                                    editOrder(order.orders_id, orderRequest)
                                }
                            }
                            ordersItemsList.remove(orderTemp)
//                            checkedStatesMap.remove(order.orders_id)
                        }
                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

