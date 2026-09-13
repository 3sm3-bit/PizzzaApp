package com.pizzza.pizzzaapp.component

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pizzza.pizzzaapp.core.navigation.*
import com.pizzza.pizzzaapp.core.ui.*
import com.pizzza.pizzzaapp.feature.auth.AuthViewModel
import com.pizzza.pizzzaapp.feature.auth.login.LoginScreen
import com.pizzza.pizzzaapp.feature.auth.register.RegisterScreen
import com.pizzza.pizzzaapp.feature.cart.AddressScreen
import com.pizzza.pizzzaapp.feature.cart.CartViewModel
import com.pizzza.pizzzaapp.feature.cart.ScreenOrderSummary
import com.pizzza.pizzzaapp.feature.cart.ScreenPaymentWebView
import com.pizzza.pizzzaapp.feature.home.HomeViewModel
import com.pizzza.pizzzaapp.feature.home.ScreenClientHome
import com.pizzza.pizzzaapp.feature.monitoring.ScreenMonitor
import com.pizzza.pizzzaapp.feature.orders.OrdersViewModel
import com.pizzza.pizzzaapp.feature.orders.ScreenDetailOrder
import com.pizzza.pizzzaapp.ui.AppViewModel
import com.pizzza.pizzzaapp.ui.splash.SplashScreen
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()
    val onBack: () -> Unit = { navController.popBackStack() }

    NavHost(
        navController = navController,
        startDestination = Splash,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable<Splash> {
            SplashScreen(
                onFinished = { isLoggedIn ->
                    if (isLoggedIn) {
                        navController.navigateSafe(ClientHome) {
                            popUpTo<Splash> { inclusive = true }
                        }
                    } else {
                        navController.navigateSafe(Login) {
                            popUpTo<Splash> { inclusive = true }
                        }
                    }
                }
            )
        }

        composable<Login> {
            LoginScreen(
                onNavigateToClientHome = {
                    navController.navigateSafe(ClientHome) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigateSafe(Register)
                }
            )
        }

        composable<Register> {
            RegisterScreen(
                onNavigateToAddressSelection = {
                    navController.navigateSafe(AddressSelection)
                },
                onRegisterSuccess = {
                    navController.navigateSafe(Login) {
                        popUpTo(Register) { inclusive = true }
                    }
                }
            )
        }

        composable<AddressSelection> {
            AddressScreen(onBack = onBack)
        }

        composable<ClientHome> {
            ScreenClientHome(
                onNavigateToSummary = { navController.navigateSafe(OrderSummary) },
                onNavigateToAddressSelection = { navController.navigateSafe(AddressSelection) },
                onNavigateToDetail = {
                    navController.navigateSafe(OrderDetail)
                },
                onLogout = {
                    navController.navigateSafe(Login) {
                        popUpTo<ClientHome> { inclusive = true }
                    }
                },
                onNavigateToMonitor = { navController.navigateSafe(Monitor) }
            )
        }

        composable<OrderDetail> {
            ScreenDetailOrder(onBack = onBack)
        }

        composable<Monitor> {
            ScreenMonitor(onBack = onBack)
        }

        composable<OrderSummary> {
            ScreenOrderSummary(
                onConfirm = {
                    navController.navigateSafe(ClientHome) {
                        popUpTo(ClientHome) { inclusive = true }
                    }
                },
                onPaymentRedirect = { url ->
                    navController.navigateSafe(PaymentWebView(url))
                },
                onBack = onBack
            )
        }

        composable<PaymentWebView> { backStackEntry ->
            val route: PaymentWebView = backStackEntry.toRoute()
            val cartViewModel: CartViewModel = koinViewModel()
            
            ScreenPaymentWebView(
                url = route.url,
                onSuccess = {
                    cartViewModel.confirmOrder(statePay = "PAGADO") {
                        navController.navigateSafe(ClientHome) {
                            popUpTo(ClientHome) { inclusive = true }
                        }
                    }
                },
                onBack = onBack
            )
        }
    }
}
