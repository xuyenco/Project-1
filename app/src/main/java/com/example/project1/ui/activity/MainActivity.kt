package com.example.project1.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project1.DataRequest.OrderRequest
import com.example.project1.DataRequest.ReservationRequest
import com.example.project1.DataRequest.Tables_ReservationRequest
import com.example.project1.DataResponse.ItemByOrderIdResponse
import com.example.project1.DataResponse.ReservationByTableIdResponse
import com.example.project1.DataResponse.TableByReservationIdResponse
import com.example.project1.data.Items
import com.example.project1.data.Orders
import com.example.project1.data.Reservation
import com.example.project1.data.SideNavbar
import com.example.project1.data.Tables
import com.example.project1.data.Tables_Reservations
import com.example.project1.retrofit.client.ApiClient
import com.example.project1.retrofit.client.RetrofitClient
import com.example.project1.ui.activity.Login.LoginScreen
import com.example.project1.ui.activity.orderMenu.OrderLayoutScreen
import com.example.project1.ui.activity.OrderTab.OrderTabScreen
import com.example.project1.ui.activity.ReservationTab.ReservationTabScreen
import com.example.project1.ui.section.NavigationDrawerItem
import com.example.project1.ui.theme.Project1Theme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


private val navBarItemList = listOf(
    SideNavbar(
        title = "Home",
        icon = Icons.Default.Home
    ),
    SideNavbar(
        title = "Order Menu",
        icon = Icons.Default.TableRestaurant
    ),
    SideNavbar(
        title = "Reservation Tab",
        icon = Icons.Default.TableRestaurant
    ),
    SideNavbar(
        title = "Order Tab",
        icon = Icons.Default.Restaurant
    )

)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.initialize(applicationContext)
        setContent {
            Project1Theme {
                SetNavbarColor(color = MaterialTheme.colorScheme.background)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "home") {
                        composable("login") { LoginScreen(navController) }
                        composable("home") { HomeScreen() }
                    }
//                    LoginScreen(navController)
//                    HomeScreen()
                }

            }
        }
    }
}
@Composable
private fun SetNavbarColor(color : Color){
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(color = color)
    }
}

@Composable
fun MainActivityScreen(){
    Text(text = "Đây là màn hình chính")
}

@Composable
fun HomeScreen() {
    val navController = rememberNavController() // NavController để điều hướng giữa các màn hình

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Text("Drawer title", modifier = Modifier.padding(16.dp))
                Divider()
                for (item in navBarItemList) {
                    NavigationDrawerItem(
                        sideNavbar = item,
                        onClick = {
                            when (item.title) {
                                "Order Tab" -> navController.navigate("order_tab")
                                "Reservation Tab" -> navController.navigate("reservation_tab")
                                "Home" -> navController.navigate("home")
                                "Order Menu" -> navController.navigate("order_menu")
                            }
                        }
                    )
                }
            }
        }
    ) {
        NavHost(navController = navController, startDestination = "home") {
            composable("order_tab") {
                OrderTabScreen() // Màn hình OrderTab
            }
            composable("reservation_tab") {
                ReservationTabScreen() // Màn hình ReservationTab
            }
            composable("home") {
                MainActivityScreen()
            }
            composable("order_menu") {
                OrderLayoutScreen()
            }
        }// Use the list of tables when available
    }
}
// retrofit API
// Get all tables
suspend fun getAllTables(): List<Tables> {
    return try {
        withContext(Dispatchers.IO) {
            ApiClient.tableService.getAllTable()
        }
    } catch (e: Exception) {
        Log.e("Api getAllTables error", e.toString())
        emptyList()
    }
}
//get table by reservationID
suspend fun getTableByReservationId(reservationId: Int): TableByReservationIdResponse? {
    return try{
        withContext(Dispatchers.IO) {
            ApiClient.tableReservationsService.getTablesByReservationId(reservationId)
        }
    } catch (e: Exception) {
        Log.e("Api getTableByReservationId error", e.toString())
        null
    }
}


// Get all reservations
suspend fun getAllReservations(): List<Reservation> {
    return try {
        withContext(Dispatchers.IO) {
            ApiClient.reservationService.getAllReservation()
        }
    } catch (e: Exception) {
        Log.e("Api error", e.toString())
        emptyList()
    }
}
// Get reservation by ID
suspend fun getReservationById(id: Int): Reservation? {
    return try {
        withContext(Dispatchers.IO) {
            ApiClient.reservationService.getReservationById(id)
        }
    } catch (e: Exception) {
        Log.e("Api error", e.toString())
        null
    }
}

// Get reservation by table ID
suspend fun getReservationByTableId(tableId: Int): ReservationByTableIdResponse? {
    return try {
        withContext(Dispatchers.IO) {
            ApiClient.tableReservationsService.getReservationsByTableId(tableId)
        }
    } catch (e: Exception) {
        Log.e("Api getReservationByTableId error", e.toString())
        null
    }
}

// Create a new reservation
suspend fun createReservation(reservation: ReservationRequest): Reservation? {
    return try {
        withContext(Dispatchers.IO) {
            ApiClient.reservationService.createReservation(reservation)
        }
    } catch (e: Exception) {
        Log.e("Api error", e.toString())
        null
    }
}

// Edit an existing reservation by ID
suspend fun editReservation(id: Int, reservation: ReservationRequest): Reservation? {
    return try {
        withContext(Dispatchers.IO) {
            ApiClient.reservationService.editReservation(reservation, id)
        }
    } catch (e: Exception) {
        Log.e("Api error", e.toString())
        null
    }
}

// Delete a reservation by ID
suspend fun deleteReservation(id: Int): String {
    return try {
        withContext(Dispatchers.IO) {
            ApiClient.reservationService.deleteReservation(id)
        }
    } catch (e: Exception) {
        Log.e("Api error", e.toString())
        "Failed to delete reservation"
    }
}

// Get all tables_reservations
suspend fun getAllTablesReservations(): List<Tables_Reservations> {
    return try {
        withContext(Dispatchers.IO) {
            ApiClient.reservationTableService.getReservationTable()
        }
    } catch (e: Exception) {
        Log.e("Api error", e.toString())
        emptyList()
    }
}
//Get all orders
suspend fun getAllOrders(): List<Orders> {
    return try {
        withContext(Dispatchers.IO) {
            ApiClient.orderService.getAllOrders()
        }
    } catch (e: Exception) {
        Log.e("Api get all Order error", e.toString())
        emptyList()
    }
}

//Get order by id
suspend fun getOrderById(id: Int): Orders? {
    return try {
        withContext(Dispatchers.IO) {
            ApiClient.orderService.getOrderById(id)
        }
    } catch (e: Exception) {
        Log.e("Api error", e.toString())
        null
    }
}

// Create a new order
suspend fun createOrder(order: OrderRequest): Orders? {
    return try {
        withContext(Dispatchers.IO) {
            ApiClient.orderService.createOrder(order)
        }
    } catch (e: Exception) {
        Log.e("Api error", e.toString())
        null
    }
}

// Edit an existing order by ID
suspend fun editOrder(id: Int, order: OrderRequest): Orders? {
    return try {
        withContext(Dispatchers.IO) {
            ApiClient.orderService.editOrder(order, id)
        }
    } catch (e: Exception) {
        Log.e("Api edit order error", e.toString())
        null
    }
}

// Delete an order by ID
suspend fun deleteOrder(id: Int): String {
    return try {
        withContext(Dispatchers.IO) {
            ApiClient.orderService.deleteOrder(id)
        }
    } catch (e: Exception) {
        Log.e("Api error", e.toString())
        "Failed to delete order"
    }
}

//Get all items
suspend fun getAllItems(): List<Items> {
    return try {
        withContext(Dispatchers.IO) {
            ApiClient.itemService.getAllItems()
        }
    } catch (e: Exception) {
        Log.e("Api error", e.toString())
        emptyList()
    }
}

//Get items by orderId
suspend fun getItemByOrderId(orderId: Int): ItemByOrderIdResponse? {
    return try {
        withContext(Dispatchers.IO) {
            ApiClient.orderItemService.getItemByOrderId(orderId)
        }
    } catch (e: Exception) {
        Log.e("Api getItemByOrderId error", e.toString())
        null
    }
}

//Delete TableReservation
suspend fun deleteTableReservation(tableReservation: Tables_ReservationRequest): String {
    return try {
        withContext(Dispatchers.IO) {
            ApiClient.tableReservationsService.deleteTableReservation(tableReservation)
        }
    } catch (e: Exception) {
        Log.e("Api deleteTableReservation error", e.toString())
        "Failed to delete table reservation"
    }
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}
fun String.toDate(format: String, locale: Locale = Locale.getDefault()): Date? {
    return try {
        val formatter = SimpleDateFormat(format, locale)
        formatter.parse(this)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}