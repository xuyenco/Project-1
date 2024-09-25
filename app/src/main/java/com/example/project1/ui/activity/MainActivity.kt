package com.example.project1.ui.activity

import android.os.Bundle
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
import com.example.project1.data.Reservation
import com.example.project1.data.SideNavbar
import com.example.project1.data.Tables
import com.example.project1.ui.section.NavigationDrawerItem
import com.example.project1.ui.theme.Project1Theme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay


private val navBarItemList = listOf(
    SideNavbar(
        title = "Home",
        icon = Icons.Default.Home
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
    var tablesLists by remember { mutableStateOf<List<Tables>>(emptyList()) }
    var reservationList by remember { mutableStateOf<List<Reservation>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    val navController = rememberNavController() // NavController để điều hướng giữa các màn hình

    LaunchedEffect(Unit) {
        while (true) {
            try {
                // Fetch data from the API
                tablesLists = getAllTables()
                reservationList = getAllReservations()
                isLoading = false
                isError = false // Reset error if successful
//                Log.e("API repeater", "Successful")
            } catch (e: Exception) {
                isError = true
                isLoading = false
            }
            delay(1000L) // Wait for 1 second before fetching again
        }
    }

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
                            }
                        }
                    )
                }
            }
        }
    ) {
        if (isLoading) {
            // Show loading indicator
            CircularProgressIndicator()
        } else if (isError) {
            // Show error message if something went wrong
            Text("Error loading tables data")
        } else if (tablesLists.isEmpty() || reservationList.isEmpty()) {
            // Show empty state if no data is returned
            Text("No Data available")
        } else {
            // Screen content
//            ThreeColumnLayout(tablesLists,reservationList)
            NavHost(navController = navController, startDestination = "Home") {
                composable("order_tab") {
                    OrderTabScreen(tablesLists) // Màn hình OrderTab
                }
                composable("reservation_tab") {
                    ReservationTabScreen(tablesLists,reservationList) // Màn hình ReservationTab
                }
                composable("home") {
                    MainActivityScreen()
                }
            }// Use the list of tables when available
        }
    }
}