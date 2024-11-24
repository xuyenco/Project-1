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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.project1.DataRequest.OrderRequest
import com.example.project1.data.AssignOrderItemRequest
import com.example.project1.data.BillRequest
import com.example.project1.data.CreateOrderRequest
import com.example.project1.data.Items
import com.example.project1.data.OrderDetailResponse
import com.example.project1.data.Reservation
import com.example.project1.data.Tables
import com.example.project1.retrofit.client.ApiClient
import kotlinx.coroutines.launch
import java.util.Date

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
    var showBillDialog by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    var status by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    // State để lưu danh sách món và số lượng khi fetch được từ API
    val orderItems = remember { mutableStateOf<List<Items>>(emptyList()) }
    val quantitiesState = remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    val descriptionState = remember { mutableStateOf<String>("") }
    val orderIdState = remember { mutableStateOf<Int?>(null) }

    // Gọi API khi Reservation có status "Đã đặt món"
    LaunchedEffect(reservation) {
        if (reservation?.status == "Đã đặt món" || reservation?.status == "Hoàn thành") {
            val result = fetchOrderDetails(reservation.reservations_id)
            result?.let { response ->
                Log.d("ContentRight", "Fetched order items: ${response.items}")
                Log.d("ContentRight", "Fetched quantities: ${response.quantities}")

                orderItems.value = response.items
                quantitiesState.value = response.quantities
                descriptionState.value = response.description ?: ""
                orderIdState.value = response.orderId
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
        var reservationID = ""
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
        val displayedMenus = if (reservation?.status == "Đã đặt món") orderItems.value else selectedMenus
        val displayedQuantities = if (reservation?.status == "Đã đặt món") quantitiesState.value else quantities

        MenuListScreen(
            selectedMenus = displayedMenus,
            quantities = displayedQuantities,
            onIncreaseQuantity = { itemId ->
                if (reservation?.status == "Đã đặt món") {
                    quantitiesState.value = quantitiesState.value.toMutableMap().apply {
                        this[itemId] = (this[itemId] ?: 1) + 1
                    }
                } else {
                    onIncreaseQuantity(itemId)
                }
            },
            onDecreaseQuantity = { itemId ->
                if (reservation?.status == "Đã đặt món") {
                    quantitiesState.value = quantitiesState.value.toMutableMap().apply {
                        val currentQuantity = this[itemId] ?: 1
                        if (currentQuantity > 1) {
                            this[itemId] = currentQuantity - 1
                        }
                    }
                } else {
                    onDecreaseQuantity(itemId)
                }
            },
            onRemoveItem = onRemoveMenuItem
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Description: ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        TextField(
            value = if (reservation?.status == "Đã đặt món") descriptionState.value else description,
            onValueChange = { description = it },
            label = { Text("Enter description") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = 5 // Limit to 5 lines for description
        )
        Spacer(modifier = Modifier.height(16.dp))

        val totalAmount = remember(orderItems.value, quantitiesState.value, selectedMenus, quantities, reservation?.status) {
            if (reservation?.status == "Đã đặt món") {
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
                onClick = {
                    onCancel()
                    description = ""
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { showBillDialog = true }, // Hiển thị hộp thoại khi nhấn "Create Bill"
                modifier = Modifier.weight(1f)
            ) {
                Text("Create Bill")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    description = ""
                    scope.launch {
                        if (reservation != null) {
                            val statusMessage = if (reservation?.status == "Đã đặt món") {
                                updateExistingOrder(description,orderItems.value , quantities, quantitiesState = quantitiesState.value, reservation.reservations_id, orderIdState.value)
                            } else {
                                createNewOrder(description, selectedMenus, quantities, reservation?.reservations_id ?: -1)
                            }
                            status = statusMessage
                            Log.d("ShowStatus", "Status: $status")
                        }
                    }
                    onCancel()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("OK")
            }
        }

        // Hiển thị hộp thoại chi tiết hóa đơn nếu `showBillDialog` là true
        if (showBillDialog && reservation != null) {
            BillDialog(
                reservation = reservation,
                tablesList = selectedTables.joinToString(separator = ", ") { it.name },
                orderItems = orderItems.value,
                quantities = quantitiesState.value,
                createdAt = reservation.created_at.toString(),
                onClose = { showBillDialog = false },
                onSave = { staffId ->
                    orderIdState.value?.let { orderId ->
                        scope.launch {
                            val result = createBill(reservation.reservations_id, orderId, staffId)
                            alertMessage = if (result) {
                                "Bill created successfully."
                            } else {
                                "Failed to create bill."
                            }
                            showAlertDialog = true // Hiển thị AlertDialog thông báo kết quả
                        }
                    }
                }
            )
        }
        if (showAlertDialog) {
            AlertDialog(
                onDismissRequest = { showAlertDialog = false },
                title = { Text("Bill Status") },
                text = { Text(alertMessage) },
                confirmButton = {
                    Button(onClick = { showAlertDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị kết quả
        StatusSnackbar(snackbarHostState = snackbarHostState, status = status)
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
suspend fun createNewOrder(
    description: String,
    selectedMenus: List<Items>,
    quantities: Map<Int, Int>,
    reservationId: Int
): String {
    return try {
        Log.d("createNewOrder", "Reservation ID: $reservationId")
        // Bước 1: Gọi API tạo order mới
        val orderResponse = ApiClient.orderService.createOrder(
            CreateOrderRequest(
                reservations_id = reservationId,
                description = description,
                status = "Đang chờ"
            )
        )

        if (orderResponse.isSuccessful) {
            val createdOrder = orderResponse.body()!!
            val orderId = createdOrder.orders_id

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
                    mapOf("status" to "Đã đặt món")
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
suspend fun updateExistingOrder(
    description: String,
    selectedMenus: List<Items>,
    quantities: Map<Int, Int>,
    quantitiesState: Map<Int, Int>, // Truyền quantitiesState vào hàm
    reservationId: Int,
    orderId: Int?
): String {
    return try {
        Log.d("updateExistingOrder", "Reservation ID: $reservationId")
        Log.d("updateExistingOrder", "Order ID: $orderId")
        val assignOrderStatusResponse = ApiClient.orderService.editOrder(
            OrderRequest("Đang chờ"),
            orderId ?: return "Error: Missing order ID"
        )
        // Tính sự thay đổi số lượng cho mỗi món
        val itemUpdates = selectedMenus.mapNotNull { menu ->
            val currentQuantity = menu.quantity_used
            val existingQuantity = quantitiesState[menu.items_id] ?: 0
            val quantityDifference =existingQuantity- currentQuantity

            Log.d("updateExistingOrder", "Item ID: ${menu.items_id}, Current Quantity: $currentQuantity, Existing Quantity: $existingQuantity, Difference: $quantityDifference")

            if (quantityDifference != 0) {
                AssignOrderItemRequest(
                    items_id = menu.items_id,
                    orders_id = orderId ?: return "Error: Missing order ID",
                    quantity = quantityDifference
                )
            } else {
                null
            }
        }
        Log.d("updateExistingOrder", "Item updates: $itemUpdates")
        if (itemUpdates.isEmpty()) {
            Log.d("updateExistingOrder", "No items to update")
        }
        // Gọi API cập nhật order với danh sách itemUpdates
        val assignItemResponse = ApiClient.orderService.assignItemsToOrder(itemUpdates)
        if (assignItemResponse.isSuccessful) {
            Log.d("updateExistingOrder", "Order updated successfully")
            "Order updated successfully"
        } else {
            Log.e("updateExistingOrder", "Failed to update order items: ${assignItemResponse.code()}")
            "Failed to update order items: ${assignItemResponse.code()}"
        }
    } catch (e: Exception) {
        "Error updating order: ${e.message}"
    }
}
suspend fun fetchOrderDetails(
    reservationId: Int
): OrderDetailResponse? {
    return try {
        Log.d("fetchOrderDetails", "Fetching order details for reservationId: $reservationId")

        // Gọi API lấy orderId từ reservationId
        val result = ApiClient.orderService.getOrderIdByReservationId(reservationId)
        val orderId = result.orders_id
        Log.d("fetchOrderDetails", "Received orderId: $orderId")

        // Gọi API lấy chi tiết các món trong order và description
        val orderItemsResponse = ApiClient.orderService.getOrderItems(orderId)
        val description = orderItemsResponse.description

        // Nhóm các Items có cùng items_id và tính tổng quantity_used
        val aggregatedItems = orderItemsResponse.items.groupBy { it.items_id }.map { (id, itemsList) ->
            val totalQuantity = itemsList.sumOf { it.quantity_used }
            val firstItem = itemsList.first() // Lấy thông tin cơ bản từ item đầu tiên
            Items(
                items_id = id,
                name = firstItem.name,
                image_url = firstItem.image_url,
                unit = firstItem.unit,
                category = firstItem.category,
                price = firstItem.price,
                created_at = firstItem.created_at,
                updated_at = firstItem.updated_at
            ).apply {
                quantity_used = totalQuantity // Gán tổng quantity vào Items
            }
        }

        // Tạo map với items_id là key và tổng quantity là value
        val quantities = aggregatedItems.associate { it.items_id to it.quantity_used }

        Log.d("fetchOrderDetails", "Aggregated order items: $aggregatedItems")
        Log.d("fetchOrderDetails", "Quantities: $quantities")
        Log.d("fetchOrderDetails", "Description: $description")

        OrderDetailResponse(aggregatedItems, quantities, description, orderId)
    } catch (e: Exception) {
        Log.e("fetchOrderDetails", "Error fetching order details", e)
        null
    }
}
suspend fun createBill(reservationId: Int, ordersId: Int, staffId: Int): Boolean {
    return try {
        val response = ApiClient.orderService.createBill(
            BillRequest(
                orders_id = ordersId,
                staff_id = staffId // Truyền Staff ID
            )
        )
        if (response.isSuccessful) {
            Log.d("createBill", "Bill created successfully.")
            val assignOrderStatusResponse = ApiClient.orderService.editOrder(
                OrderRequest("Đang chờ"),
                ordersId
            )
            val updateReservationResponse = ApiClient.reservationService.updateReservationStatus(
                reservationId,
                mapOf("status" to "Hoàn thành")
            )
            if (updateReservationResponse.isSuccessful) {
                Log.d("createBill", "Reservation updated successfully")
            } else {
                Log.e("createBill", "Reservation update failed")
            }
            true
        } else {
            Log.e("createBill", "Failed to create bill: ${response.errorBody()?.string()}")
            false
        }
    } catch (e: Exception) {
        Log.e("createBill", "Error creating bill", e)
        false
    }
}
@Composable
fun BillDialog(
    reservation: Reservation,
    tablesList: String,
    orderItems: List<Items>,
    quantities: Map<Int, Int>,
    createdAt: String,
    onClose: () -> Unit,
    onSave: (Int) -> Unit
) {
    var staffId by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onClose) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Bill Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier
                    .fillMaxWidth()){
                    Text("Staff ID:")
                    OutlinedTextField(
                        value = staffId,
                        onValueChange = { staffId = it },
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .width(200.dp),
                        singleLine = true
                    )
                }
                Text("Reservation ID: ${reservation.reservations_id}")
                Spacer(modifier = Modifier.height(1.dp))


                Text("Reserved Tables: $tablesList")
                Text("Check-In Time: $createdAt")
                Text("Check-Out Time: ${getCurrentTime()}")
                Text("Customer Name: ${reservation.name}")
                Text("Phone: ${reservation.phone}")

                Spacer(modifier = Modifier.height(16.dp))

                // Hiển thị danh sách món ăn dưới dạng bảng
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("STT", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("Tên món", fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                    Text("Số lượng", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("Đơn giá", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("Thành tiền", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                }

                Divider()

                // Duyệt qua từng món và hiển thị chi tiết
                orderItems.forEachIndexed { index, item ->
                    val quantity = quantities[item.items_id] ?: 0
                    val itemTotal = item.price * quantity

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("${index + 1}", modifier = Modifier.weight(1f))
                        Text(item.name, modifier = Modifier.weight(2f))
                        Text("$quantity", modifier = Modifier.weight(1f))
                        Text("${item.price}", modifier = Modifier.weight(1f))
                        Text("$itemTotal", modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val totalAmount = orderItems.sumOf { item ->
                    val quantity = quantities[item.items_id] ?: 0
                    item.price * quantity
                }
                Text(
                    text = "Total Amount: $$totalAmount",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.End)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onClose) {
                        Text("Close")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onSave(staffId.toInt()) }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

// Hàm lấy giờ hiện tại
fun getCurrentTime(): String {
    val currentTime = System.currentTimeMillis()
    val dateFormatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
    return dateFormatter.format(currentTime)
}
@Composable
fun StatusSnackbar(snackbarHostState: SnackbarHostState, status: String) {
    LaunchedEffect(status) {
        if (status.isNotEmpty()) {
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
@Preview(
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_C)
@Composable
fun PreviewBillDialog() {
    val fakeReservation = Reservation(
        reservations_id = 12345,
        name = "John Doe",
        phone = "123-456-7890",
        email = "johndoe@example.com",
        quantity = 4,
        status = "Đã đặt món",
        time = Date(),
        created_at = Date(),
        updated_at = Date()
    )

    val fakeTablesList = "Table 1, Table 2, Table 3"

    val fakeOrderItems = listOf(
        Items(items_id = 1, name = "Pizza", image_url = "", unit = "piece", category = "Food", price = 100, created_at = Date(), updated_at = Date()),
        Items(items_id = 2, name = "Pasta", image_url = "", unit = "plate", category = "Food", price = 125, created_at = Date(), updated_at = Date()),
        Items(items_id = 3, name = "Soda", image_url = "", unit = "bottle", category = "Drink", price = 30, created_at =Date(), updated_at = Date())
    )

    val fakeQuantities = mapOf(
        1 to 2, // 2 Pizzas
        2 to 1, // 1 Pasta
        3 to 3  // 3 Sodas
    )

    BillDialog(
        reservation = fakeReservation,
        tablesList = fakeTablesList,
        orderItems = fakeOrderItems,
        quantities = fakeQuantities,
        createdAt = fakeReservation.created_at.toString(),
        onClose = {},
        onSave = {}
    )
}
