package com.example.project1.ui.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
@Composable
fun PopupBox(popupWidth: Float, popupHeight: Float, showPopup: Boolean, onClickOutside: () -> Unit, content: @Composable () -> Unit) {
    if (showPopup) {
        // Full screen background with semi-transparent overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)) // Dark overlay with 50% transparency
                .zIndex(10f), // Ensure the popup stays on top
            contentAlignment = Alignment.Center // Center the popup
        ) {
            // Popup itself
            Popup(
                alignment = Alignment.Center,
                properties = PopupProperties(
                    excludeFromSystemGesture = true
                ),
                onDismissRequest = { onClickOutside() } // Dismiss on clicking outside
            ) {
                // Box for the popup content
                Box(
                    modifier = Modifier
                        .width(popupWidth.dp)
                        .height(popupHeight.dp)
                        .background(Color.White)
                        .clip(RoundedCornerShape(8.dp)) // Rounded corners
                        .padding(16.dp), // Padding inside the popup
                    contentAlignment = Alignment.Center
                ) {
                    content() // User-defined content inside the popup
                }
            }
        }
    }
}
