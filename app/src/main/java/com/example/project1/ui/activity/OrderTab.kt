package com.example.project1.ui.activity

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.project1.data.Items
import com.example.project1.data.Orders
import com.example.project1.data.Orders_Items
import com.example.project1.data.Orders_Tables
import com.example.project1.data.Tables
import com.example.project1.ui.section.OrderTabItem
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
    )
)
private val Orders_ItemsList = listOf(
    Orders_Items(
        orders_id = 0,
        items_id = 0,
        quantity = 1,
        description = "Không hành",
        created_at = Date(),
        updated_at = Date()
    ),Orders_Items(
        orders_id = 0,
        items_id = 1,
        quantity = 1,
        description = "Không béo",
        created_at = Date(),
        updated_at = Date()
    ),Orders_Items(
        orders_id = 0,
        items_id = 2,
        quantity = 1,
        description = "Không hành",
        created_at = Date(),
        updated_at = Date()
    ), Orders_Items(
        orders_id = 0,
        items_id = 3,
        quantity = 1,
        description = "Không hành",
        created_at = Date(),
        updated_at = Date()
    ),Orders_Items(
        orders_id = 1,
        items_id = 0,
        quantity = 1,
        description = "Không hành",
        created_at = Date(),
        updated_at = Date()
    ),Orders_Items(
        orders_id = 1,
        items_id = 1,
        quantity = 1,
        description = "Không hành",
        created_at = Date(),
        updated_at = Date()
    ),Orders_Items(
        orders_id = 1,
        items_id = 2,
        quantity = 1,
        description = "Không hành",
        created_at = Date(),
        updated_at = Date()
    ),
)
private val OrdersLists = listOf(
    Orders(
        orders_id = 0,
        status = false,
        created_at = Date(),
        updated_at = Date()
    ),Orders(
        orders_id = 1,
        status = false,
        created_at = Date(),
        updated_at = Date()
    ),
)
private val ItemsLists = listOf(
    Items(
        items_id = 0,
        name = "Cá kho riềng",
        image_url = "image",
        quantity = 1,
        unit = "Đĩa",
        category = "Thịt",
        price = 10000,
        created_at = Date(),
        updated_at = Date()
    ),Items(
        items_id = 1,
        name = "Thịt ba chỉ rang cháy cạnh",
        image_url = "image",
        quantity = 1,
        unit = "Đĩa",
        category = "Thịt",
        price = 10000,
        created_at = Date(),
        updated_at = Date()
    ),Items(
        items_id = 2,
        name = "Canh bò hầm",
        image_url = "image",
        quantity = 1,
        unit = "Đĩa",
        category = "Canh",
        price = 10000,
        created_at = Date(),
        updated_at = Date()
    ),Items(
        items_id = 3,
        name = "Pepsi",
        image_url = "image",
        quantity = 1,
        unit = "Đĩa",
        category = "Đồ uống",
        price = 10000,
        created_at = Date(),
        updated_at = Date()
    ),
)
@Composable
fun OrderTabScreen(tablesLists : List<Tables>) {
    // Lưu trữ danh sách orders chưa hoàn thành
    var orders by remember { mutableStateOf(OrdersLists.filter { !it.status }) }
    // Lưu trữ trạng thái checkbox cho từng order
    val checkedStatesMap = remember { mutableStateOf(mutableMapOf<Int, List<Boolean>>()) }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(5),
        contentPadding = PaddingValues(5.dp),
        content = {
            items(orders.size) { index ->
                val order = orders[index]

                // Lọc các Table liên kết với Order hiện tại
                val tablesForOrder = Orders_TablesList
                    .filter { it.orders_id == order.orders_id }
                    .mapNotNull { tableOrder ->
                        tablesLists.find { it.tables_id == tableOrder.tables_id }
                    }

                val dishesForOrder = Orders_ItemsList
                    .filter { it.orders_id == order.orders_id }
                    .mapNotNull { orderItems ->
                        ItemsLists.find { it.items_id == orderItems.items_id }
                    }

                // Lấy trạng thái checkbox hiện tại từ map hoặc khởi tạo nếu chưa có
                val checkedStates = checkedStatesMap.value[order.orders_id] ?: List(dishesForOrder.size) { false }

                OrderTabItem(
                    itemsList = dishesForOrder,
                    tablesList = tablesForOrder,
                    checkedStates = checkedStates,
                    onCheckedChange = { updatedStates ->
                        // Cập nhật trạng thái checkbox trong map
                        checkedStatesMap.value = checkedStatesMap.value.toMutableMap().apply {
                            this[order.orders_id] = updatedStates
                        }
                    },
                    onOrderCompleted = {
                        // Cập nhật state của Order khi hoàn thành
                        orders = orders.map { if (it.orders_id == order.orders_id) it.copy(status = true) else it }
                        // Lọc lại danh sách để chỉ còn các Order chưa hoàn thành
                        orders = orders.filter { !it.status }
                        // Xóa trạng thái checkbox của order đã hoàn thành khỏi map
                        checkedStatesMap.value = checkedStatesMap.value.toMutableMap().apply {
                            remove(order.orders_id)
                        }
                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
