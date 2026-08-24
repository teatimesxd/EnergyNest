package com.example.energynest

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// Color Definitions matching the UI
val SidebarBgColor = Color(0xFFE9ECF0)
val BrandGreen = Color(0xFF00B060)
val SearchBgColor = Color(0xFFDEE2E6)
val DividerColor = Color(0xFF8E8E8E)

@Composable
fun AppSidebarLayout() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = SidebarBgColor,
                modifier = Modifier.width(310.dp)
            ) {
                SidebarContent()
            }
        }
    ) {
        // Main Screen Content
        Scaffold(
            topBar = {
                TopAppBarContent(
                    onMenuClick = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
                // Main application screen content goes here
            }
        }
    }
}

@Composable
fun TopAppBarContent(onMenuClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Outlined.ViewSidebar,
                    contentDescription = "Toggle Sidebar",
                    tint = Color.Black
                )
            }
            IconButton(onClick = { /* Notification Click */ }) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.Black
                )
            }
        }
        HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))
    }
}

@Composable
fun SidebarContent() {
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(vertical = 24.dp)
    ) {
        // --- HEADER / LOGO ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ElectricBolt,
                    contentDescription = "EnergyNest Logo",
                    tint = BrandGreen,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "EnergyNest",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandGreen
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Spark Green Energy To Malaysia",
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                color = Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- SEARCH BAR ---
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search CREAM", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = Color.Black
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SearchBgColor,
                    unfocusedContainerColor = SearchBgColor,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- MAIN MENU ITEMS ---
        Column(modifier = Modifier.weight(1f)) {
            SidebarMenuItem(icon = Icons.Outlined.Home, title = "Home") {}
            SidebarMenuItem(icon = Icons.Outlined.History, title = "View Payment History") {}
            SidebarMenuItem(icon = Icons.Outlined.BarChart, title = "View Electric Analysis") {}
            SidebarMenuItem(icon = Icons.Outlined.Build, title = "Services") {}
        }

        // --- DIVIDER ---
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            thickness = 1.dp,
            color = DividerColor
        )

        // --- BOTTOM MENU ITEMS ---
        SidebarMenuItem(icon = Icons.Outlined.Settings, title = "Settings") {}
        SidebarMenuItem(icon = Icons.Outlined.HelpOutline, title = "Help & feedback") {}
    }
}

@Composable
fun SidebarMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) { //hi
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}