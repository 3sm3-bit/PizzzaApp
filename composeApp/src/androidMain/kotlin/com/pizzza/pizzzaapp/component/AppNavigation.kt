package com.pizzza.pizzzaapp.component

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pizzza.pizzzaapp.core.navigation.ScreenInitNav
import com.pizzza.pizzzaapp.core.navigation.navigateSafe
import com.pizzza.pizzzaapp.feature.auth.login.LoginScreen
import com.pizzza.pizzzaapp.feature.auth.register.RegisterScreen
import com.pizzza.pizzzaapp.feature.cart.AddressScreen
import com.pizzza.pizzzaapp.feature.cart.CartViewModel
import com.pizzza.pizzzaapp.feature.cart.ScreenOrderSummary
import com.pizzza.pizzzaapp.feature.cart.ScreenPaymentWebView
import com.pizzza.pizzzaapp.feature.home.ScreenClientHome
import com.pizzza.pizzzaapp.feature.monitoring.ScreenMonitor
import com.pizzza.pizzzaapp.feature.orders.ScreenDetailOrder
import com.pizzza.pizzzaapp.ui.splash.SplashScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()
    val onBack: () -> Unit = { navController.popBackStack() }

    NavHost(
        navController = navController,
        startDestination = ScreenInitNav.Splash,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable<ScreenInitNav.Splash> {
            SplashScreen(
                onNavigateTo = { idNav ->
                        navController.navigateSafe(idNav) {
                            popUpTo<ScreenInitNav.Splash> { inclusive = true }
                        }

                }
            )
        }

        composable<ScreenInitNav.Login> {
            LoginScreen(
                onNavigateTo = { idNav ->
                    navController.navigateSafe(idNav) {
                        if (idNav == ScreenInitNav.ClientHome){
                            popUpTo(ScreenInitNav.Login) { inclusive = true }
                        }
                    }
                }

            )
        }

        composable<ScreenInitNav.Register> {
            RegisterScreen(
                onNavigateTo = { idNav ->
                    navController.navigateSafe(idNav)
                }
            )
        }

        composable<ScreenInitNav.AddressSelection> {
            AddressScreen(onBack = onBack)
        }

        composable<ScreenInitNav.ClientHome> {
            ScreenClientHome(
                onNavigateTo = {  idNav ->
                    navController.navigateSafe(idNav) {
                        if (idNav == ScreenInitNav.Login){
                            popUpTo<ScreenInitNav.ClientHome> { inclusive = true }
                        }
                    }
                }
            )
        }

        composable<ScreenInitNav.OrderDetail> {
            ScreenDetailOrder(onBack = onBack)
        }

        composable<ScreenInitNav.Monitor> {
            ScreenMonitor(onBack = onBack)
        }

        composable<ScreenInitNav.OrderSummary> {
            ScreenOrderSummary(
                onConfirm = {
                    navController.navigateSafe(ScreenInitNav.ClientHome) {
                        popUpTo(ScreenInitNav.ClientHome) { inclusive = true }
                    }
                },
                onPaymentRedirect = { url ->
                    navController.navigateSafe(ScreenInitNav.PaymentWebView(url))
                },
                onBack = onBack
            )
        }

        composable<ScreenInitNav.PaymentWebView> { backStackEntry ->
            val route: ScreenInitNav.PaymentWebView = backStackEntry.toRoute()
            val cartViewModel: CartViewModel = koinViewModel()
            
            ScreenPaymentWebView(
                url = route.url,
                onSuccess = {
                    cartViewModel.confirmOrder(statePay = "PAGADO") {
                        navController.navigateSafe(ScreenInitNav.ClientHome) {
                            popUpTo(ScreenInitNav.ClientHome) { inclusive = true }
                        }
                    }
                },
                onBack = onBack
            )
        }
    }
}
