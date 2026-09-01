package com.example.energynest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.energynest.ui.components.AppBottomNavBar
import com.example.energynest.ui.theme.EnergyNestTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnergyNestTheme {
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.SmartSell.route,
                    Screen.Cream.route,
                    Screen.Services.route,
                    Screen.ElectricAnalysis.route
                )

                val isAuthScreen = currentRoute in listOf(
                    Screen.Login.route,
                    Screen.Register.route,
                    Screen.ForgotPassword.route
                )

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = !isAuthScreen,
                    drawerContent = {
                        SidebarDrawerContent(
                            currentRoute = currentRoute,
                            onCloseDrawer = { scope.launch { drawerState.close() } },
                            onNavigate = { route: String ->
                                scope.launch { drawerState.close() }
                                if (currentRoute != route) {
                                    navController.navigate(route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                ) {
                    Scaffold(
                        bottomBar = {
                            if (showBottomBar) {
                                AppBottomNavBar(
                                    currentRoute = currentRoute ?: Screen.Home.route,
                                    onNavigateTo = { route: String ->
                                        if (currentRoute != route) {
                                            navController.navigate(route) {
                                                popUpTo(Screen.Home.route) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Login.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            // ---- Auth Screens ----
                            composable(Screen.Login.route) {
                                LoginPage(
                                    onLoginSuccess = {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                        }
                                    },
                                    onNavigateToRegister = {
                                        navController.navigate(Screen.Register.route)
                                    },
                                    onNavigateToForgotPassword = {
                                        navController.navigate(Screen.ForgotPassword.route)
                                    }
                                )
                            }

                            composable(Screen.Register.route) {
                                RegisterPage(
                                    onRegisterSuccess = {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                        }
                                    },
                                    onBackToLogin = {
                                        navController.popBackStack()
                                    }
                                )
                            }

                            composable(Screen.ForgotPassword.route) {
                                ForgotPasswordPage(
                                    onBackToLogin = {
                                        navController.popBackStack()
                                    }
                                )
                            }

                            // ---- Main App Screens ----
                            composable(Screen.Home.route) {
                                HomeScreen(
                                    onOpenDrawer = { scope.launch { drawerState.open() } },
                                    onProfileClick = { navController.navigate(Screen.Profile.route) }
                                )
                            }

                            composable(Screen.SmartSell.route) {
                                SmartSellScreen(
                                    onOpenDrawer = { scope.launch { drawerState.open() } },
                                    onProfileClick = { navController.navigate(Screen.Profile.route) }
                                )
                            }

                            composable(Screen.Cream.route) {
                                CreamScreen(
                                    onOpenDrawer = { scope.launch { drawerState.open() } },
                                    onCheckEligibility = {
                                        navController.navigate(Screen.LegaEligibility.route)
                                    }
                                )
                            }

                            composable(Screen.Services.route) {
                                ServicesScreen(
                                    onOpenDrawer = { scope.launch { drawerState.open() } },
                                    onProfileClick = { navController.navigate(Screen.Profile.route) }
                                )
                            }

                            composable(Screen.ElectricAnalysis.route) {
                                ElectricAnalysisScreen(
                                    onOpenDrawer = { scope.launch { drawerState.open() } }
                                )
                            }

                            composable(Screen.LegaEligibility.route) {
                                LegaEligibilityScreen(
                                    onBack = { navController.popBackStack() },
                                    onCompleteAssessment = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.PaymentHistory.route) {
                                PaymentHistoryScreen(
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Profile.route) {
                                ProfileScreenWrapper(
                                    onBackToHome = { navController.popBackStack() },
                                    onChangePasswordClick = { navController.navigate(Screen.ResetPassword.route) },
                                    onPaymentHistoryClick = { navController.navigate(Screen.PaymentHistory.route) },
                                    onLogoutConfirm = {
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(Screen.ResetPassword.route) {
                                ResetPasswordScreen(
                                    onBack = { navController.popBackStack() },
                                    onResetSuccess = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Settings.route) {
                                SettingPage(
                                    onBackClick = { navController.popBackStack() },
                                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                                    onNavigateToTerms = {
                                        navController.navigate(Screen.TermsAndConditions.route)
                                    },
                                    onLogoutConfirmed = {
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(Screen.TermsAndConditions.route) {
                                TermsConditionPage(
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}