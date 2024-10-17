package com.example.project1.ui.activity.SubComposable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.Menu
import com.example.project1.data.Tables
import com.example.project1.ui.section.MenuItem
import com.example.project1.ui.section.TableOrderItem
import java.util.Date

val menuList = listOf(
    Menu(1, "Cá lóc nướng trui", "Main Course", 100000, "https://statics.vincom.com.vn/xu-huong/0-0-0-0-mon-nhau-don-gian/image11.png"),
    Menu(2, "Cá hồi sốt chanh mật ong", "Main Course", 150000, "https://statics.vincom.com.vn/xu-huong/0-0-0-0-mon-nhau-don-gian/image8.png"),
    Menu(3, "Gà xé phay chua ngọt", "Main Course", 90000, "https://statics.vincom.com.vn/xu-huong/0-0-0-0-mon-nhau-don-gian/image15.png"),
    Menu(1, "Thịt ngan hấp bia", "Main Course", 200000, "https://statics.vincom.com.vn/xu-huong/0-0-0-0-mon-nhau-don-gian/image4.png"),
    Menu(2, "Gà nấu nấm ", "Main Course", 125000, "https://statics.vincom.com.vn/xu-huong/0-0-0-0-mon-nhau-don-gian/image1.png"),
    Menu(3, "Giò heo muối xông khói ", "Course", 85000, "https://statics.vincom.com.vn/xu-huong/0-0-0-0-mon-nhau-don-gian/image20.png")
)

val tableItemList = listOf(
    Tables(
        tables_id = 1,
        name = "Table 1",
        quantity = 4,
        location = "First Floor",
        status = "Available",
        created_at = Date(),
        updated_at = Date()
    ),
    Tables(
        tables_id = 2,
        name = "Table 2",
        quantity = 6,
        location = "Second Floor",
        status = "Occupied",
        created_at = Date(),
        updated_at = Date()
    ),
    Tables(
        tables_id = 3,
        name = "Table 3",
        quantity = 2,
        location = "First Floor",
        status = "Available",
        created_at = Date(),
        updated_at = Date()
    ),
    Tables(
        tables_id = 4,
        name = "Table 4",
        quantity = 8,
        location = "Patio",
        status = "Reserved",
        created_at = Date(),
        updated_at = Date()
    )
)
@Composable
fun OrderLayout() {
    val (selectedTables, setSelectedTables) = remember { mutableStateOf<List<Tables>>(emptyList()) }
    val (selectedMenus, setSelectedMenus) = remember { mutableStateOf<List<Menu>>(emptyList()) }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(2f) // Tỷ lệ phần này chiếm 2/3 màn hình
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            ContentLeft(
                selectedTables = selectedTables,
                onTableItemClick = { tableItem ->
                    if (selectedTables.contains(tableItem)) {
                        setSelectedTables(selectedTables - tableItem)
                    } else {
                        setSelectedTables(selectedTables + tableItem)
                    }
                },
                onMenuItemClick = { menuItem ->
                    if (!selectedMenus.contains(menuItem) && selectedTables.isNotEmpty())
                        setSelectedMenus(selectedMenus + menuItem)
                }
            )
        }

        // Phần 2: 1/3 bên phải
        Column(
            modifier = Modifier
                .weight(1f) // Tỷ lệ phần này chiếm 1/3 màn hình
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            ContentRight(selectedTables,
                selectedMenus,
                onRemoveMenuItem = { menuItem ->
                    setSelectedMenus(selectedMenus - menuItem)
                })
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
fun ContentLeft(selectedTables: List<Tables>, onTableItemClick: (Tables) -> Unit, onMenuItemClick: (Menu) -> Unit) {
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
                is Tables ->{
                    TableOrderItem(item, onClick = { onTableItemClick(item) })
                }
                is Menu -> MenuItem(item, onClick ={ onMenuItemClick(item)})
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    setItems(tableItemList)
                },
                modifier = Modifier.weight(1f),
                enabled = items == menuList
            ) {
                Text("Back")
            }
            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    setItems(menuList)
                },
                modifier = Modifier.weight(1f),
                enabled = items == tableItemList
            ) {
                Text("Show Menu")
            }
        }
    }
}

@Composable
fun ContentRight(selectedTables: List<Tables>, selectedMenus: List<Menu>, onRemoveMenuItem: (Menu) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Bàn đã chọn: ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = selectedTables.joinToString(separator = ", ") { "${it.name}" },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Order: ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        MenuListScreen(selectedMenus, onRemoveItem = onRemoveMenuItem)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {}
        ) {
            Text("Add order")
        }
    }
}
@Composable
fun MenuListScreen(selectedMenus: List<Menu>, onRemoveItem: (Menu) -> Unit) {
    var quantities by remember { mutableStateOf(selectedMenus.associate { it.id to 1 }) }

    Surface(
        modifier = Modifier
            .height(600.dp)
            .fillMaxSize(),
        color = Color.White
    ) {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(selectedMenus) { item ->
                MenuItemRow(
                    menu = item,
                    quantity = quantities[item.id] ?: 1,
                    backgroundColor = Color.White,
                    onIncreaseQuantity = { id ->
                        quantities = quantities.toMutableMap().apply {
                            this[id] = (this[id] ?: 1) + 1
                        }
                    },
                    onDecreaseQuantity = { id ->
                        quantities = quantities.toMutableMap().apply {
                            val currentQuantity = this[id] ?: 1
                            if (currentQuantity > 1) {
                                this[id] = currentQuantity - 1
                            }
                        }
                    },
                    onRemoveItem = {
                        onRemoveItem(item) // Gọi hàm onRemoveItem từ ContentRight
                    }
                )
            }
        }
    }
    Spacer(modifier = Modifier.width(8.dp))

}

@Composable
fun MenuItemRow(
    menu: Menu,
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
            onClick = { onRemoveItem(menu.id) },
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
            text = menu.itemName
        )
        TextButton(onClick = { onDecreaseQuantity(menu.id) }) {
            Text("-")
        }
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            text = "$quantity")
        TextButton(onClick = { onIncreaseQuantity(menu.id) }) {
            Text("+")
        }
    }
}
