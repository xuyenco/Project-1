package com.example.project1.ui.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.project1.data.Menu
import com.example.project1.data.Table

@Composable
fun ItemBox(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    icon: ImageVector? = null,
    title: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(250.dp, 200.dp)
            .padding(10.dp)
            .border(width = 1.dp, color = Color.Black)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (imageUrl != null) {
                // Nếu có URL ảnh, dùng Coil để hiển thị ảnh
                Image(
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(5.dp)
                        .size(70.dp)
                )
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(5.dp)
                        .size(70.dp)
                )
            }
            Text(text = title)
        }
    }
}

@Composable
fun TableItem(table: Table, onClick: () -> Unit) {
    ItemBox(
        icon = Icons.Default.TableBar,
        title = "Bàn số ${table.tableNumber}",
        onClick = onClick
    )
}

@Composable
fun MenuItem(menu: Menu, onClick: () -> Unit) {
    ItemBox(
        imageUrl = menu.imageUrl,
        title = "${menu.itemName} \n ${menu.itemPrice} vnd",
        onClick = onClick
    )
}
