package com.example.project1.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.project1.data.Dishes
import com.example.project1.data.Order
import com.example.project1.data.OrderDetail
import com.example.project1.data.Table
import com.example.project1.data.Table_Order
import com.example.project1.ui.section.OrderTabItem
import com.example.project1.ui.theme.Project1Theme

private val tableList = listOf(
    Table(
        id = 0,
        tableNumber = 1,
        tableSeatNumber = 2,
        tablePosition = 2,
        tableStatus = true
    ), Table(
        id = 1,
        tableNumber = 2,
        tableSeatNumber = 2,
        tablePosition = 2,
        tableStatus = true
    ), Table(
        id = 2,
        tableNumber = 3,
        tableSeatNumber = 2,
        tablePosition = 2,
        tableStatus = true
    ), Table(
        id = 3,
        tableNumber = 3,
        tableSeatNumber = 3,
        tablePosition = 3,
        tableStatus = true
    ), Table(
        id = 4,
        tableNumber = 4,
        tableSeatNumber = 4,
        tablePosition = 4,
        tableStatus = true
    ), Table(
        id = 5,
        tableNumber = 5,
        tableSeatNumber = 5,
        tablePosition = 5,
        tableStatus = true
    ), Table(
        id = 6,
        tableNumber = 6,
        tableSeatNumber = 6,
        tablePosition = 6,
        tableStatus = true
    )

)
private val tableOrderList = listOf(
    Table_Order(
       tableId = 0,
       orderId = 0
    ),Table_Order(
        tableId = 1,
        orderId = 1
    ),Table_Order(
        tableId = 2,
        orderId = 2
    ),Table_Order(
        tableId = 3,
        orderId = 3
    ),
)
private val OrderList = listOf(
    Order(
        id = 0,
        state = false
    ),Order(
        id = 1,
        state = false
    ),Order(
        id = 2,
        state = false
    ),Order(
        id = 3,
        state = false
    ),Order(
        id = 0,
        state = false
    ),Order(
        id = 1,
        state = false
    ),Order(
        id = 2,
        state = false
    ),Order(
        id = 3,
        state = false
    ),
)
private val OrderDishesList = listOf(
    OrderDetail(
        idOrder = 0,
        idDish = 0,
        note = "Không hành",
        quantity = 1
    ),
    OrderDetail(
        idOrder = 0,
        idDish = 1,
        note = "Ít nước béo",
        quantity = 1
    ),
    OrderDetail(
        idOrder = 0,
        idDish = 2,
        note = "",
        quantity = 4
    ),
    OrderDetail(
        idOrder = 0,
        idDish = 3,
        note = "",
        quantity = 2
    ),

    OrderDetail(
        idOrder = 1,
        idDish = 0,
        note = "Không hành",
        quantity = 1
    ),
    OrderDetail(
        idOrder = 1,
        idDish = 1,
        note = "Ít nước béo",
        quantity = 1
    ),
    OrderDetail(
        idOrder = 1,
        idDish = 2,
        note = "",
        quantity = 4
    ),
    OrderDetail(
        idOrder = 1,
        idDish = 3,
        note = "",
        quantity = 2
    ),

    OrderDetail(
        idOrder = 2,
        idDish = 0,
        note = "Không hành",
        quantity = 1
    ),
    OrderDetail(
        idOrder = 2,
        idDish = 1,
        note = "Ít nước béo",
        quantity = 1
    ),
    OrderDetail(
        idOrder = 2,
        idDish = 2,
        note = "",
        quantity = 4
    ),
    OrderDetail(
        idOrder = 2,
        idDish = 3,
        note = "",
        quantity = 2
    ),OrderDetail(
        idOrder = 2,
        idDish = 2,
        note = "",
        quantity = 4
    ),
    OrderDetail(
        idOrder = 2,
        idDish = 3,
        note = "",
        quantity = 2
    ),

    OrderDetail(
        idOrder = 3,
        idDish = 0,
        note = "Không hành",
        quantity = 1
    ),
    OrderDetail(
        idOrder = 3,
        idDish = 1,
        note = "Ít nước béo",
        quantity = 1
    ),
    OrderDetail(
        idOrder = 3,
        idDish = 2,
        note = "",
        quantity = 4
    ),
    OrderDetail(
        idOrder = 3,
        idDish = 3,
        note = "",
        quantity = 2
    ),OrderDetail(
        idOrder = 3,
        idDish = 2,
        note = "",
        quantity = 4
    ),
    OrderDetail(
        idOrder = 3,
        idDish = 3,
        note = "",
        quantity = 2
    ),OrderDetail(
        idOrder = 3,
        idDish = 2,
        note = "",
        quantity = 4
    ),
    OrderDetail(
        idOrder = 3,
        idDish = 3,
        note = "",
        quantity = 2
    ),

)
private val DishesList = listOf(
    Dishes(
        id = 0,
        name = "Ba chí cháy cạnh",
        unit = "Đĩa",
        price = 10000,
        category = "Thịt"
    ),
    Dishes(
        id = 1,
        name = "Cơm trắng",
        unit = "Đĩa",
        price = 10000,
        category = "Thịt"
    ),
    Dishes(
        id = 2,
        name = "Pepsi",
        unit = "Lon",
        price = 10000,
        category = "Đồ uống"
    ),
    Dishes(
        id = 3,
        name = "Cá kho riềng",
        unit = "Đĩa",
        price = 10000,
        category = "Thịt"
    ),
)
class OrderTab : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Project1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OrderTabScreen()
                }
            }
        }
    }
}

@Composable
fun OrderTabScreen() {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(5),
        contentPadding = PaddingValues(5.dp),
        content = {
            items(OrderList.size) { index ->
                val order = OrderList[index]

                // Lọc các Table liên kết với Order hiện tại
                val tablesForOrder = tableOrderList
                    .filter { it.orderId == order.id }
                    .mapNotNull { tableOrder ->
                        tableList.find { it.id == tableOrder.tableId }
                    }

                val dishesForOrder = OrderDishesList
                    .filter { it.idOrder == order.id }
                    .mapNotNull { orderDetail ->
                        DishesList.find { it.id == orderDetail.idDish }
                    }

                // Gọi OrderTabItem với danh sách tables và dishes đã lọc
                OrderTabItem(
                    dishesList = dishesForOrder,
                    tableList = tablesForOrder
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}




