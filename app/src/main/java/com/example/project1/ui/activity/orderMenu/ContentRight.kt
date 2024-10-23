package com.example.project1.ui.activity.orderMenu

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
import com.example.project1.data.Items
import com.example.project1.data.Menu
import com.example.project1.data.Tables


@Composable
fun ContentRight(
    selectedTables: List<Tables>,
    selectedMenus: List<Items>,
    onRemoveMenuItem: (Items) -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
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
        MenuListScreen(selectedMenus, onRemoveItem = onRemoveMenuItem)
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
                onClick = {},
                modifier = Modifier.weight(1f)
            ) {
                Text("Add order")
            }
        }
    }
}
@Composable
fun MenuListScreen(selectedMenus: List<Items>, onRemoveItem: (Items) -> Unit) {
    var quantities by remember { mutableStateOf(selectedMenus.associate { it.items_id to 1 }) }

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
                    quantity = quantities[item.items_id] ?: 1,
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
