package com.example.project1.ui.section
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.project1.data.SideNavbar

@Composable
fun NavigationDrawerItem(sideNavbar: SideNavbar) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .padding(start = 5.dp, top = 5.dp, bottom = 5.dp)
            .fillMaxWidth()
            .clickable {
                // Sử dụng đúng kiểu dữ liệu cho các tham số của Toast.makeText
                Toast.makeText(context, "Clicked on ${sideNavbar.title}", Toast.LENGTH_SHORT).show()
            }
    ) {
        Row {
            Icon(imageVector = sideNavbar.icon, contentDescription = sideNavbar.title)
            Text(text = sideNavbar.title)
        }
    }
}
