package com.ait.oncue.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ait.oncue.ui.theme.OnCueBackground
import com.ait.oncue.ui.theme.OnCueGradientColors
import com.ait.oncue.ui.theme.OnCueTextGray
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person

@Composable
fun OnCueBottomNavigationBar(
    selectedTab: Int,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Column {
        // Faint white line above the nav bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.1f))
        )

        NavigationBar(
            containerColor = OnCueBackground,
            contentColor = Color.White
        ) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home") },
                selected = selectedTab == 0,
                onClick = onHomeClick,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = OnCueGradientColors.first(),
                    unselectedIconColor = OnCueTextGray,
                    indicatorColor = Color.Transparent
                )
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                label = { Text("Profile") },
                selected = selectedTab == 1,
                onClick = onProfileClick,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = OnCueGradientColors.first(),
                    unselectedIconColor = OnCueTextGray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}