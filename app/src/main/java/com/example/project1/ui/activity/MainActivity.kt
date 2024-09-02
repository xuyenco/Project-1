package com.example.project1.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project1.data.SideNavbar
import com.example.project1.data.Table
import com.example.project1.ui.theme.Project1Theme
import com.example.project1.ui.section.NavigationDrawerItem
import com.example.project1.ui.section.TableItem
import com.google.accompanist.systemuicontroller.rememberSystemUiController



private val navBarItemList = listOf(
    SideNavbar(
        title = "Home",
        icon = Icons.Default.Home
    ),
    SideNavbar(
        title = "Đặt bàn",
        icon = Icons.Default.TableRestaurant
    ),
    SideNavbar(
        title = "Đặt món",
        icon = Icons.Default.Restaurant
    )
)

private val tableItemList = listOf(
    Table(
        id = 0,
        tableNumber = 1,
        tableSeatNumber = 2,
        tablePosition = 2,
        tableStatus = true
    ),Table(
        id = 1,
        tableNumber = 2,
        tableSeatNumber = 2,
        tablePosition = 2,
        tableStatus = true
    ),Table(
        id = 2,
        tableNumber = 3,
        tableSeatNumber = 2,
        tablePosition = 2,
        tableStatus = true
    ),Table(
        id = 1,
        tableNumber = 2,
        tableSeatNumber = 2,
        tablePosition = 2,
        tableStatus = true
    ),Table(
        id = 2,
        tableNumber = 3,
        tableSeatNumber = 2,
        tablePosition = 2,
        tableStatus = true
    ),Table(
        id = 1,
        tableNumber = 2,
        tableSeatNumber = 2,
        tablePosition = 2,
        tableStatus = true
    ),Table(
        id = 2,
        tableNumber = 3,
        tableSeatNumber = 2,
        tablePosition = 2,
        tableStatus = true
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
fun HomeScreen() {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Text("Drawer title", modifier = Modifier.padding(16.dp))
                Divider()
                for(item in navBarItemList) {
                    NavigationDrawerItem(sideNavbar = item)
                }
                // ...other drawer items
            }
        }
    ) {
        // Screen content
        ThreeColumnLayout()
    }

}

@Composable
fun ThreeColumnLayout() {
    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(2f) // Tỷ lệ phần này chiếm 2/3 màn hình
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            ContentLeft() // Nội dung phần 1 và 2
        }

        // Phần 2: 1/3 bên phải
        Column(
            modifier = Modifier
                .weight(1f) // Tỷ lệ phần này chiếm 1/3 màn hình
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            ContentRight() // Nội dung phần 3
        }
    }
}

@Composable
fun ContentLeft() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(tableItemList.size) { index -> // Số lượng item bạn muốn hiển thị
            TableItem(table = tableItemList[index])
        }
    }
}

@Composable
fun ContentRight() {
    // Nội dung của phần phải (chiếm 1/3 màn hình)
    Text(text = "Nội dung bên phải (1/3 màn hình)")
}
