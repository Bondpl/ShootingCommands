package com.example.komendystrzelanie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.komendystrzelanie.ui.theme.KomendyStrzelanieTheme
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.komendystrzelanie.ui.screens.home.HomeScreen
import com.example.komendystrzelanie.ui.screens.settings.SettingsScreen


data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val screen: @Composable () -> Unit
)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KomendyStrzelanieTheme {
                val items = listOf(
                    BottomNavigationItem(
                        "Home",
                        Icons.Filled.Home,
                        Icons.Outlined.Home,
                        screen = { HomeScreen() }
                    ),
                    BottomNavigationItem(
                        "Settings",
                        Icons.Filled.Settings,
                        Icons.Outlined.Settings,
                        screen = { SettingsScreen() }
                    )
                )

                var selectedItemIndex by rememberSaveable { mutableStateOf(0) }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            items.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    selected = selectedItemIndex == index,
                                    onClick = { selectedItemIndex = index },
                                    icon = {
                                        if (selectedItemIndex == index) {
                                            Icon(
                                                imageVector = item.selectedIcon,
                                                contentDescription = item.title
                                            )
                                        } else {
                                            Icon(
                                                imageVector = item.unselectedIcon,
                                                contentDescription = item.title
                                            )
                                        }
                                    },
                                    label = { Text(item.title) },
                                    alwaysShowLabel = false
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    // Display the content based on the selected item
                    Box(modifier = Modifier.padding(innerPadding)) {
                        items[selectedItemIndex].screen()
                    }
                }
            }
        }
    }
}

const val DELAY_AFTER_PLAYING_SPECIFIC_COMMANDS = 3000L // Delay after playing specific audio files


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    KomendyStrzelanieTheme {
        HomeScreen()
    }
}