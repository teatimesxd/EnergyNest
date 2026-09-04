package com.example.energynest.routes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.energynest.backend_models.SupabaseClient
import com.example.energynest.backend_models.UserSession
import com.example.energynest.login_signup.LoginPage
import com.example.energynest.login_signup.RegisterPage
import com.example.energynest.login_signup.ForgotPasswordPage
import com.example.energynest.login_signup.ChangePasswordScreen
import com.example.energynest.dashboard_analysis.HomeScreen
import com.example.energynest.dashboard_analysis.ElectricAnalysisScreen
import com.example.energynest.sell_payment.SmartSellScreen
import com.example.energynest.sell_payment.CreamScreen
import com.example.energynest.sell_payment.LegaEligibilityScreen
import com.example.energynest.sell_payment.PaymentHistoryScreen
import com.example.energynest.maintenance_support.ServicesScreen
import com.example.energynest.maintenance_support.HelpNSupportPage
import com.example.energynest.maintenance_support.FeedbackPage
import com.example.energynest.userinfo_settings.ProfileScreenWrapper
import com.example.energynest.userinfo_settings.SettingPage
import com.example.energynest.policies.PrivacyPolicyPage
import com.example.energynest.policies.TermsConditionPage
import com.example.energynest.shared_ui.AppBottomNavBar
import com.example.energynest.shared_ui.SidebarDrawerContent
import com.example.energynest.ui.theme.EnergyNestTheme
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

                val userProfile = UserSession.user
                val userIc = UserSession.icNumber

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
                                        popUpTo(Screen.Home.route)
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            userProfile = userProfile
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
                                    },
                                    onNavigateToTerms = {
                                        navController.navigate(Screen.TermsAndConditions.route){
                                            popUpTo(Screen.TermsAndConditions.route) { inclusive = true }
                                        }
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
                                    userIc = userIc,
                                    onOpenDrawer = { scope.launch { drawerState.open() } },
                                    onProfileClick = { navController.navigate(Screen.Profile.route) }
                                )
                            }

                            composable(Screen.SmartSell.route) {
                                SmartSellScreen(
                                    userIc = userIc,
                                    onOpenDrawer = { scope.launch { drawerState.open() } },
                                    onProfileClick = { navController.navigate(Screen.Profile.route) }
                                )
                            }

                            composable(Screen.Cream.route) {
                                CreamScreen(
                                    onOpenDrawer = { scope.launch { drawerState.open() } },
                                    onCheckEligibility = {
                                        navController.navigate(Screen.LegaEligibility.route)
                                    },
                                    onProfileClick = { navController.navigate(Screen.Profile.route) }
                                )
                            }

                            composable(Screen.Services.route) {
                                ServicesScreen(
                                    userIc = userIc,
                                    onOpenDrawer = { scope.launch { drawerState.open() } },
                                    onProfileClick = { navController.navigate(Screen.Profile.route) }
                                )
                            }

                            composable(Screen.ElectricAnalysis.route) {
                                ElectricAnalysisScreen(
                                    userIc = userIc,
                                    onOpenDrawer = { scope.launch { drawerState.open() } },
                                    onProfileClick = { navController.navigate(Screen.Profile.route) }
                                )
                            }

                            composable(Screen.LegaEligibility.route) {
                                LegaEligibilityScreen(
                                    userIc = userIc,
                                    onBack = { navController.popBackStack() },
                                    onCompleteAssessment = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.PaymentHistory.route) {
                                PaymentHistoryScreen(
                                    userIc = userIc,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Profile.route) {
                                ProfileScreenWrapper(
                                    userIc = userIc,
                                    onBackToHome = { navController.popBackStack() },
                                    onChangePasswordClick = { navController.navigate(Screen.ResetPassword.route) },
                                    onPaymentHistoryClick = { navController.navigate(Screen.PaymentHistory.route) },
                                    onLogoutConfirm = {
                                        UserSession.logout()
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(Screen.ResetPassword.route) {
                                ChangePasswordScreen(
                                    onBack = { navController.popBackStack() },
                                    onResetSuccess = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Settings.route) {
                                SettingPage(
                                    onBackClick = { navController.popBackStack() },
                                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                                    onNavigateToChangePassword = { navController.navigate(Screen.ResetPassword.route) },
                                    onNavigateToPrivacyPolicy = { navController.navigate(Screen.PrivacyPolicy.route) },
                                    onNavigateToTerms = {
                                        navController.navigate(Screen.TermsAndConditions.route)
                                    },
                                    onNavigateToFeedback = { navController.navigate(Screen.Feedback.route) },
                                    onLogoutConfirmed = {
                                        UserSession.logout()
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    },
                                    onDeleteAccountConfirmed = {
                                        UserSession.logout()
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(Screen.PrivacyPolicy.route) {
                                PrivacyPolicyPage(
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Feedback.route) {
                                FeedbackPage(
                                    userIc = userIc,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.HelpSupport.route) {
                                HelpNSupportPage(
                                    onBack = { navController.popBackStack() }
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