package com.pizzza.pizzzaapp.component

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pizzza.pizzzaapp.ui.AppViewModel
import com.pizzza.pizzzaapp.ui.CartViewModel
import com.pizzza.pizzzaapp.ui.StoreViewModel
import com.pizzza.pizzzaapp.ui.auth.AuthViewModel
import com.pizzza.pizzzaapp.ui.splash.SplashScreen
import com.pizzza.pizzzaapp.ui.client.ScreenClientHome
import com.pizzza.pizzzaapp.ui.client.ScreenOrderSummary
import com.pizzza.pizzzaapp.ui.client.ScreenDetailOrder
import com.pizzza.pizzzaapp.ui.client.AddressScreen
import com.pizzza.pizzzaapp.ui.auth.LoginScreen
import com.pizzza.pizzzaapp.ui.auth.RegisterScreen

@Composable
fun AppNavigation(
    viewModel: AppViewModel,
    cartViewModel: CartViewModel,
    storeViewModel: StoreViewModel,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Splash) {
        composable<Splash> {
            SplashScreen(
                viewModel = viewModel,
                authViewModel = authViewModel,
                onFinished = { isLoggedIn ->
                    if (isLoggedIn) {
                        navController.navigate(ClientHome) {
                            popUpTo<Splash> { inclusive = true }
                        }
                    } else {
                        navController.navigate(Login) {
                            popUpTo<Splash> { inclusive = true }
                        }
                    }
                }
            )
        }
        composable<Login> {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToClientHome = {
                    navController.navigate(ClientHome) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Register) 
                }
            )
        }
        composable<Register> {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToAddressSelection = {
                    navController.navigate(AddressSelection)
                },
                onRegisterSuccess = {
                    navController.navigate(Login) {
                        popUpTo(Register) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable<AddressSelection> {
            AddressScreen(
                onConfirm = { address, lat, lng ->
                    // Detect if we came from Register or Cart (ClientHome/Cart tab)
                    val previousRoute = navController.previousBackStackEntry?.destination?.route
                    if (previousRoute?.contains("Register") == true) {
                        authViewModel.onRegisterFieldChange(
                            address = address,
                            latitude = lat,
                            longitude = lng
                        )
                    } else {
                        cartViewModel.setAddressSelection(
                            address = address,
                            lat = lat,
                            lng = lng
                        )
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable<ClientHome> {
            ScreenClientHome(
                viewModel = viewModel,
                cartViewModel = cartViewModel,
                storeViewModel = storeViewModel,
                authViewModel = authViewModel,
                onNavigateToSummary = { navController.navigate(OrderSummary) },
                onNavigateToAddressSelection = { navController.navigate(AddressSelection) },
                onNavigateToDetail = { navController.navigate(OrderDetail) },
                onLogout = {
                    navController.navigate(Login) {
                        popUpTo<ClientHome> { inclusive = true }
                    }
                }
            )
        }
        composable<OrderDetail> {
            ScreenDetailOrder(
                viewModel = viewModel,
                cartViewModel = cartViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable<OrderSummary> {
            ScreenOrderSummary(
                cartViewModel = cartViewModel,
                onConfirm = { tabIndex ->
                    cartViewModel.setInitialTab(tabIndex)
                    cartViewModel.clearCart()
                    viewModel.getGeneralOrderList(forceLoading = true)
                    navController.navigate(ClientHome) {
                        popUpTo(ClientHome) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
