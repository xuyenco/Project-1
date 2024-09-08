package com.example.project1.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project1.data.Menu
import com.example.project1.data.SideNavbar
import com.example.project1.data.Table
import com.example.project1.ui.section.MenuItem
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

private val menuList = listOf(
    Menu(1, "Pizza", "Main Course", 10.99, "https://www.foodandwine.com/thmb/Wd4lBRZz3X_8qBr69UOu2m7I2iw=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/classic-cheese-pizza-FT-RECIPE0422-31a2c938fc2546c9a07b7011658cfd05.jpg"),
    Menu(2, "Burger", "Main Course", 8.99, "https://product.hstatic.net/1000389344/product/burger_web_2daf139345214f3eb6caa111ae710674_master.jpg"),
    Menu(3, "Ice Cream", "Dessert", 5.49, "https://upload.wikimedia.org/wikipedia/commons/2/2e/Ice_cream_with_whipped_cream%2C_chocolate_syrup%2C_and_a_wafer_%28cropped%29.jpg"),
    Menu(1, "Pizza", "Main Course", 10.99, "https://www.foodandwine.com/thmb/Wd4lBRZz3X_8qBr69UOu2m7I2iw=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/classic-cheese-pizza-FT-RECIPE0422-31a2c938fc2546c9a07b7011658cfd05.jpg"),
    Menu(2, "Burger", "Main Course", 8.99, "https://product.hstatic.net/1000389344/product/burger_web_2daf139345214f3eb6caa111ae710674_master.jpg"),
    Menu(3, "Ice Cream", "Dessert", 5.49, "https://upload.wikimedia.org/wikipedia/commons/2/2e/Ice_cream_with_whipped_cream%2C_chocolate_syrup%2C_and_a_wafer_%28cropped%29.jpg")
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
fun GridItem(
    items: List<Any>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (Any) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier,
        content = {
            items(items.size) { index ->
                itemContent(items[index])
            }
        }
    )
}
@Composable
fun ContentLeft() {
    val (items, setItems) = remember { mutableStateOf<List<Any>>(tableItemList) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        GridItem(
            items = items,
            modifier = Modifier
                .weight(1f)
        ) { item ->
            when (item) {
                is Table -> TableItem(item)
                is Menu -> MenuItem(item)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val newItems = if (items == tableItemList) {
                    menuList
                } else {
                    tableItemList
                }
                setItems(newItems)
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Show Menu")
        }
    }
}

@Composable
fun ContentRight() {
    // Nội dung của phần phải (chiếm 1/3 màn hình)
    Text(text = "Nội dung bên phải (1/3 màn hình)")
}
@Preview(showSystemUi = true, showBackground = true, device = "id:pixel_c")
@Composable
fun ScreenPreview(){
    HomeScreen()
}