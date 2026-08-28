package com.example.energynest

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SideBar() {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf("Home") }
    var isSidebarOpen by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { isSidebarOpen = !isSidebarOpen },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        if (isSidebarOpen) Icons.Default.Close else Icons.Default.Menu,
                        contentDescription = if (isSidebarOpen) "Close Sidebar" else "Open Sidebar",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = "EnergyNest",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                IconButton(
                    onClick = {
                        Toast.makeText(context, "Notifications clicked!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.LightGray)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = isSidebarOpen,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(300))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isSidebarOpen = false
                        }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .width(300.dp)
                                .fillMaxHeight()
                                .background(Color.White)
                                .padding(horizontal = 24.dp, vertical = 32.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
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
                                        .size(180.dp)
                                        .scale(scale)
                                )

                                Text(
                                    text = "EnergyNest",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            Text(
                                text = "Spark Green Energy To Malaysia",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 32.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            OutlinedTextField(
                                value = searchText,
                                onValueChange = { searchText = it },
                                placeholder = {
                                    Text("Search CREAM", color = Color.Gray)
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 24.dp),
                                shape = RoundedCornerShape(50.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4CAF50),
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    cursorColor = Color(0xFF4CAF50),
                                ),
                                singleLine = true
                            )

                            val menuItems = listOf(
                                "Home" to Icons.Default.Home,
                                "View Payment History" to Icons.Default.History,
                                "View Electric Analysis" to Icons.Default.Analytics,
                                "Services" to Icons.Default.Build
                            )

                            menuItems.forEachIndexed { index, (title, icon) ->
                                AnimatedSidebarItem(
                                    title = title,
                                    icon = icon,
                                    isSelected = selectedItem == title,
                                    delay = index * 100,
                                    onClick = {
                                        selectedItem = title
                                        Toast.makeText(context, "$title clicked!", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Divider(
                                color = Color.LightGray,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )

                            val bottomItems = listOf(
                                "Settings" to Icons.Default.Settings,
                                "Help & feedback" to Icons.Default.Info
                            )

                            bottomItems.forEachIndexed { index, (title, icon) ->
                                AnimatedSidebarItem(
                                    title = title,
                                    icon = icon,
                                    isSelected = selectedItem == title,
                                    delay = (menuItems.size + index) * 100,
                                    onClick = {
                                        selectedItem = title
                                        Toast.makeText(context, "$title clicked!", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = if (isSidebarOpen) 300.dp else 0.dp)
            )
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
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 400,
                delayMillis = delay
            )
        ) + slideInHorizontally(
            initialOffsetX = { -it / 2 },
            animationSpec = tween(
                durationMillis = 400,
                delayMillis = delay
            )
        ),
        exit = fadeOut() + slideOutHorizontally()
    ) {
        val backgroundColor by animateColorAsState(
            targetValue = if (isSelected) Color(0xFFE8F5E9) else Color.Transparent,
            animationSpec = tween(durationMillis = 300),
            label = "bg_color"
        )

        val iconColor by animateColorAsState(
            targetValue = if (isSelected) Color(0xFF4CAF50) else Color.DarkGray,
            animationSpec = tween(durationMillis = 300),
            label = "icon_color"
        )

        val textColor by animateColorAsState(
            targetValue = if (isSelected) Color.Black else Color.DarkGray,
            animationSpec = tween(durationMillis = 300),
            label = "text_color"
        )

        val textWeight by animateFloatAsState(
            targetValue = if (isSelected) 500f else 400f,
            animationSpec = tween(durationMillis = 300),
            label = "text_weight"
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
                .background(
                    backgroundColor,
                    RoundedCornerShape(8.dp)
                )
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
                fontWeight = FontWeight(textWeight.toInt())
            )

            Spacer(modifier = Modifier.weight(1f))

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(24.dp)
                        .background(
                            Color(0xFF4CAF50),
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    }
}

@Preview(
    name = "Sidebar Preview",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun PreviewSideBar() {
    MaterialTheme {
        SideBar()
    }
}