package com.example.energynest

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BrandGreen = Color(0xFF00B87C)

data class DrawerMenuItem(
    val title: String,
    val icon: ImageVector,
    val route: String? = null
)

@Composable
fun SidebarDrawerContent(
    currentRoute: String? = null,
    onCloseDrawer: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }

    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        modifier = Modifier.width(300.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ---- Animated Logo ----
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "logo_pulse")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.05f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "logo_scale"
                )

                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.energynest_icon_1),
                    contentDescription = "EnergyNest Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(140.dp)
                        .scale(scale)
                )

                Text(
                    text = "EnergyNest",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Text(
                text = "Spark Green Energy To Malaysia",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            // ---- Search Field ----
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search CREAM", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray,
                        modifier = Modifier.size(22.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(50.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandGreen,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = BrandGreen,
                ),
                singleLine = true
            )

            // ---- Main Nav Items ----
            val menuItems = listOf(
                DrawerMenuItem("Home", Icons.Default.Home, Screen.Home.route),
                DrawerMenuItem("Smart Sell", Icons.Default.Sell, Screen.SmartSell.route),
                DrawerMenuItem("CREAM Leasing", Icons.Default.Analytics, Screen.Cream.route),
                DrawerMenuItem("Services", Icons.Default.Build, Screen.Services.route)
            )

            menuItems.forEachIndexed { index, item ->
                val isSelected = currentRoute == item.route
                AnimatedSidebarItem(
                    title = item.title,
                    icon = item.icon,
                    isSelected = isSelected,
                    delay = index * 80,
                    onClick = {
                        item.route?.let { route ->
                            onNavigate(route)
                        } ?: onCloseDrawer()
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider(
                color = Color.LightGray,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // ---- Secondary Bottom Items ----
            val bottomItems = listOf(
                DrawerMenuItem("Settings", Icons.Default.Settings),
                DrawerMenuItem("Help & feedback", Icons.Default.Info)
            )

            bottomItems.forEachIndexed { index, item ->
                AnimatedSidebarItem(
                    title = item.title,
                    icon = item.icon,
                    isSelected = false,
                    delay = (menuItems.size + index) * 80,
                    onClick = {
                        onCloseDrawer()
                        Toast.makeText(context, "${item.title} clicked!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun AnimatedSidebarItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    delay: Int,
    onClick: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "item_scale"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300, delayMillis = delay)) +
                slideInHorizontally(initialOffsetX = { -it / 2 }, animationSpec = tween(300, delayMillis = delay))
    ) {
        val backgroundColor by animateColorAsState(
            targetValue = if (isSelected) Color(0xFFD8F3E5) else Color.Transparent,
            animationSpec = tween(durationMillis = 300),
            label = "bg_color"
        )

        val iconColor by animateColorAsState(
            targetValue = if (isSelected) BrandGreen else Color.DarkGray,
            animationSpec = tween(durationMillis = 300),
            label = "icon_color"
        )

        val textColor by animateColorAsState(
            targetValue = if (isSelected) Color.Black else Color.DarkGray,
            animationSpec = tween(durationMillis = 300),
            label = "text_color"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isPressed = true
                    onClick()
                    isPressed = false
                }
                .background(backgroundColor, RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = title,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )

            Spacer(modifier = Modifier.weight(1f))

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(24.dp)
                        .background(BrandGreen, RoundedCornerShape(2.dp))
                )
            }
        }
    }
}