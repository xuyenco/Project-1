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
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.project1.data.Menu
import com.example.project1.data.Tables

@Composable
fun TableOrderItem(tables: Tables, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(250.dp, 200.dp)
            .padding(10.dp)
            .border(width = 1.dp, color = Color.Black)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.TableBar,
                contentDescription = "Icon bàn",
                modifier = Modifier
                    .padding(5.dp)
                    .size(70.dp)
            )
            Text(text = "${tables.name}")
        }
    }
}

@Composable
fun MenuItem(menu: Menu, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(250.dp, 200.dp)
            .padding(10.dp)
            .border(width = 1.dp, color = Color.Black)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = menu.imageUrl),
                contentDescription = "Hình ảnh món ăn",
                modifier = Modifier
                    .padding(5.dp)
                    .size(70.dp)
            )
            Text(text = "${menu.itemName} \n ${menu.itemPrice} vnd")
        }
    }
}