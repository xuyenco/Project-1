package com.example.project1.ui.activity.orderMenu

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.AssignOrderItemRequest
import com.example.project1.data.CreateOrderRequest
import com.example.project1.data.Items
import com.example.project1.data.Orders_Items
import com.example.project1.data.Orders_Tables
import com.example.project1.data.Reservation
import com.example.project1.data.Tables
import com.example.project1.retrofit.client.ApiClient
import kotlinx.coroutines.launch
import java.util.Date

suspend fun createNewOrder(
    description: String,
    selectedTables: List<Tables>,
    selectedMenus: List<Items>,
    quantities: Map<Int, Int>,
    reservationId: Int
): String {
    return try {
        // Bước 1: Gọi API tạo order mới
        val orderResponse = ApiClient.orderService.createOrder(
            CreateOrderRequest(
                description = description,
                status = "Đang chờ"
            )
        )

        if (orderResponse.isSuccessful) {
            val createdOrder = orderResponse.body()!!
            val orderId = createdOrder.orders_id

            // Bước 2: Gọi API gán các tables_id vào order mới
            for (table in selectedTables) {
                val assignTableResponse = ApiClient.orderService.assignOrder(
                    Orders_Tables(
                        orders_id = orderId,
                        tables_id = table.tables_id,
                        created_at = Date(),
                        updated_at = Date()
                    )
                )

                if (!assignTableResponse.isSuccessful) {
                    return "Failed to assign table ${table.tables_id}: ${assignTableResponse.code()}"
                }
            }
            // Chuẩn bị danh sách AssignOrderItemRequest cho tất cả items
            val assignItemRequests = selectedMenus.map { menu ->
                AssignOrderItemRequest(
                    items_id = menu.items_id,
                    orders_id = orderId,
                    quantity = quantities[menu.items_id] ?: 1
                )
            }

            // Gọi API với danh sách assignItemRequests
            val assignItemResponse = ApiClient.orderService.assignItemsToOrder(assignItemRequests)

            if (assignItemResponse.isSuccessful) {
                // Bước 4: Cập nhật status của reservation
                val updateReservationResponse = ApiClient.reservationService.updateReservationStatus(
                    reservationId,
                    mapOf("status" to "Ordered")
                )

                if (updateReservationResponse.isSuccessful) {
                    "Order created and reservation updated successfully"
                } else {
                    "Order created but failed to update reservation: ${updateReservationResponse.code()}"
                }
            } else {
                "Failed to assign items: ${assignItemResponse.code()}"
            }
        } else {
            "Failed to create order: ${orderResponse.code()}"
        }
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}
suspend fun fetchOrderDetails(
    tableId: Int
): Triple<List<Items>, Map<Int, Int>, String?>? {
    return try {
        Log.d("fetchOrderDetails", "Fetching order details for tableId: $tableId")
        // Gọi API để lấy orderId dựa vào tableId
        val result = ApiClient.orderService.getOrderIdByTable(tableId)
        val orderId = result.orders.first().orders_id
        Log.d("fetchOrderDetails", "Received orderId: $orderId")

        // Gọi API để lấy danh sách các món và description của order
        val orderItemsResponse = ApiClient.orderService.getOrderItems(orderId)
        val description = orderItemsResponse.description // Giả sử description có trong phản hồi API

        // Tạo danh sách Items và quantities dựa trên kết quả API trả về
        val orderItems = orderItemsResponse.items.map { item ->
            Items(
                items_id = item.items_id,
                name = item.name,
                image_url = item.image_url,
                unit = item.unit,
                category = item.category,
                price = item.price,
                created_at = item.created_at,
                updated_at = item.updated_at
            )
        }
        val quantities = orderItemsResponse.items.associate { it.items_id to it.quantity_used }

        Log.d("fetchOrderDetails", "Order items: $orderItems")
        Log.d("fetchOrderDetails", "Quantities: $quantities")
        Log.d("fetchOrderDetails", "Description: $description")

        Triple(orderItems, quantities, description)
    } catch (e: Exception) {
        Log.e("fetchOrderDetails", "Error fetching order details", e)
        null
    }
}


@Composable
fun ContentRight(
    reservation: Reservation?,
    selectedTables: List<Tables>,
    selectedMenus: List<Items>,
    quantities: Map<Int, Int>, // Map cho quantity
    onIncreaseQuantity: (Int) -> Unit,
    onDecreaseQuantity: (Int) -> Unit,
    onRemoveMenuItem: (Items) -> Unit,
    onCancel: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("") }

    // State để lưu danh sách món và số lượng khi fetch được từ API
    val orderItems = remember { mutableStateOf<List<Items>>(emptyList()) }
    val quantitiesState = remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    val descriptionState = remember { mutableStateOf<String>("") }

    // Gọi API khi Reservation có status "ordered"
    LaunchedEffect(reservation) {
        if (reservation?.status == "Ordered") {
            // Lấy id_table đầu tiên trong danh sách bàn của reservation
            val tableId = selectedTables.firstOrNull()?.tables_id
            if (tableId != null) {
                val result = fetchOrderDetails(tableId)
                result?.let { (items, quantities,description) ->
                    Log.d("ContentRight", "Fetched order items: $items")
                    Log.d("ContentRight", "Fetched quantities: $quantities")

                    orderItems.value = items
                    quantitiesState.value = quantities
                    descriptionState.value = description ?: ""
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Reservation ID: ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        var reservationID : String = ""
        if (reservation != null) {
            reservationID = reservation.reservations_id.toString()
        }
        Text(
            text = reservationID,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Reserved Table: ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = selectedTables.joinToString(separator = ", ") { it.name },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Order: ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        // Sử dụng orderItems và quantitiesState khi reservation?.status == "Ordered"
        val displayedMenus = if (reservation?.status == "Ordered") orderItems.value else selectedMenus
        val displayedQuantities = if (reservation?.status == "Ordered") quantitiesState.value else quantities

        MenuListScreen(
            selectedMenus = displayedMenus,
            quantities = displayedQuantities,
            onIncreaseQuantity = onIncreaseQuantity,
            onDecreaseQuantity = onDecreaseQuantity,
            onRemoveItem = onRemoveMenuItem
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Description: ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        TextField(
            value = if (reservation?.status == "Ordered") descriptionState.value else description,
            onValueChange = { description = it },
            label = { Text("Enter description") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = 5 // Limit to 5 lines for description
        )
        Spacer(modifier = Modifier.height(16.dp))

        val totalAmount = remember(orderItems.value, quantitiesState.value, selectedMenus, quantities, reservation?.status) {
            if (reservation?.status == "Ordered") {
                // Tính toán tổng tiền khi trạng thái của reservation là "Ordered"
                orderItems.value.sumOf { item ->
                    val quantity = quantitiesState.value[item.items_id] ?: 0
                    item.price * quantity
                }
            } else {
                // Tính toán tổng tiền khi trạng thái của reservation không phải là "Ordered"
                selectedMenus.sumOf { item ->
                    val quantity = quantities[item.items_id] ?: 0
                    item.price * quantity
                }
            }
        }
        Text(
            text = "Total Amount: $$totalAmount",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Button(
                onClick = { onCancel() }, // Gọi hàm onCancel khi nhấn nút Cancel
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    scope.launch {
                        if (reservation != null) {
                            status = createNewOrder(description, selectedTables, selectedMenus, quantities,reservation.reservations_id)
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("OK")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị kết quả
        StatusSnackbar(status = status)
    }
}

@Composable
fun MenuListScreen(
    selectedMenus: List<Items>,
    quantities: Map<Int, Int>,
    onIncreaseQuantity: (Int) -> Unit,
    onDecreaseQuantity: (Int) -> Unit,
    onRemoveItem: (Items) -> Unit
) {
    Log.d("MenuListScreen", "Selected menus: $selectedMenus")
    Log.d("MenuListScreen", "Quantities: $quantities")
    Surface(
        modifier = Modifier
            .height(400.dp)
            .fillMaxSize(),
        color = Color.White
    ) {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(selectedMenus) { item ->
                MenuItemRow(
                    menu = item,
                    quantity = quantities[item.items_id] ?: 1,
                    backgroundColor = Color.White,
                    onIncreaseQuantity = onIncreaseQuantity,
                    onDecreaseQuantity = onDecreaseQuantity,
                    onRemoveItem = { onRemoveItem(item) }
                )
            }
        }
    }
    Spacer(modifier = Modifier.width(8.dp))
}

@Composable
fun MenuItemRow(
    menu: Items,
    quantity: Int,
    backgroundColor: Color,
    onIncreaseQuantity: (Int) -> Unit,
    onDecreaseQuantity: (Int) -> Unit,
    onRemoveItem: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(backgroundColor)
    ) {
        TextButton(
            onClick = { onRemoveItem(menu.items_id) },
            modifier = Modifier
                .wrapContentSize()
                .weight(1f)
        ) {
            Text("X")
        }
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(5f),
            text = menu.name
        )
        TextButton(onClick = { onDecreaseQuantity(menu.items_id) }) {
            Text("-")
        }
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            text = "$quantity")
        TextButton(onClick = { onIncreaseQuantity(menu.items_id) }) {
            Text("+")
        }
    }
}
@Composable
fun StatusSnackbar(status: String) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(status) {
        val messageColor = if (status.contains("successfully")) Color.Green else Color.Red
        val result = scope.launch {
            snackbarHostState.showSnackbar(
                message = status,
                duration = SnackbarDuration.Short
            )
        }
    }

    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { snackbarData ->
            Snackbar(
                snackbarData = snackbarData,
                modifier = Modifier.background(
                    color = if (status.contains("successfully")) Color.Green else Color.Red
                ),
                contentColor = Color.White
            )
        }
    )
}