package com.example.project1.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project1.data.Items
import com.example.project1.data.Orders
import com.example.project1.data.Reservation
import com.example.project1.data.SideNavbar
import com.example.project1.data.Tables
import com.example.project1.retrofit.client.ApiClient
import com.example.project1.ui.activity.SubComposable.OrderLayout
import com.example.project1.ui.section.NavigationDrawerItem
import com.example.project1.ui.theme.Project1Theme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
        setContent {
            Project1Theme {
                SetNavbarColor(color = MaterialTheme.colorScheme.background)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen()
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
                OrderLayout()
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
        Log.e("Api error", e.toString())
        emptyList()
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

//Get all orders
suspend fun getAllOrders(): List<Orders> {
    return try {
        withContext(Dispatchers.IO) {
            ApiClient.orderService.getAllOrders()
        }
    } catch (e: Exception) {
        Log.e("Api error", e.toString())
        emptyList()
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