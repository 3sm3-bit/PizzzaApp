package com.tayler.pizzzaapp.component

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tayler.pizzzaapp.ui.AppViewModel
import com.tayler.pizzzaapp.ui.CartViewModel
import com.tayler.pizzzaapp.ui.StoreViewModel
import com.tayler.pizzzaapp.ui.orders.OrderScreen
import com.tayler.pizzzaapp.ui.products.ProductScreen
import com.tayler.pizzzaapp.ui.products.EditPizzaScreen
import com.tayler.pizzzaapp.ui.products.EditOtherProductScreen
import com.tayler.pizzzaapp.ui.menu.MenuOptionsScreen
import com.tayler.pizzzaapp.ui.branches.BranchScreen
import com.tayler.pizzzaapp.ui.branches.EditBranchScreen
import com.tayler.pizzzaapp.ui.roles.RoleSelectionScreen
import com.tayler.pizzzaapp.ui.splash.SplashScreen
import com.tayler.pizzzaapp.ui.client.ScreenClientHome
import com.tayler.pizzzaapp.ui.client.ScreenCartDetail
import com.tayler.pizzzaapp.ui.client.ScreenOrderSummary
import com.tayler.pizzzaapp.ui.client.ScreenDetailOrder
import com.tayler.pizzzaapp.ui.auth.LoginScreen
import com.tayler.pizzzaapp.ui.auth.RegisterScreen
import com.tayler.pizzzaapp.ui.auth.AuthViewModel
import com.tayler.pizzzaapp.ui.driver.ScreenDriverHome

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
                onFinished = {
                    navController.navigate(RoleSelection) {
                        popUpTo<Splash> { inclusive = true }
                    }
                }
            )
        }
        composable<RoleSelection> {
            RoleSelectionScreen(
                authViewModel = authViewModel,
                onNavigateToClient = { navController.navigate(Login) },
                onNavigateToClientHome = {
                    navController.navigate(ClientHome) {
                        popUpTo<RoleSelection> { inclusive = true }
                    }
                },
                onNavigateToStore = { navController.navigate(Orders) },
                onNavigateToDriver = { navController.navigate(DriverHome) }
            )
        }
        composable<Login> {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToClientHome = {
                    navController.navigate(ClientHome) {
                        popUpTo<Login> { inclusive = true }
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
                onRegisterSuccess = {
                    navController.navigate(Login) {
                        popUpTo<Register> { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable<ClientHome> {
            ScreenClientHome(
                viewModel = viewModel,
                cartViewModel = cartViewModel,
                authViewModel = authViewModel,
                onNavigateToCart = { navController.navigate(CartDetail) },
                onNavigateToDetail = { navController.navigate(OrderDetail) },
                onLogout = {
                    navController.navigate(Login) {
                        popUpTo<ClientHome> { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable<OrderDetail> {
            ScreenDetailOrder(
                viewModel = viewModel,
                cartViewModel = cartViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable<CartDetail> {
            ScreenCartDetail(
                cartViewModel = cartViewModel,
                storeViewModel = storeViewModel,
                onNavigateToSummary = { navController.navigate(OrderSummary) },
                onBack = { navController.popBackStack() }
            )
        }
        composable<OrderSummary> {
            ScreenOrderSummary(
                cartViewModel = cartViewModel,
                onConfirm = {
                    // Lógica para finalizar pedido (enviar al servidor y limpiar carrito)
                    cartViewModel.clearCart()
                    navController.navigate(ClientHome) {
                        popUpTo(ClientHome) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable<DriverHome> {
            ScreenDriverHome(onBack = { navController.popBackStack() })
        }
        composable<Orders> {
            OrderScreen(viewModel, onNavigateToMenuOptions = {
                navController.navigate(MenuOptions)
            })
        }
        composable<MenuOptions> {
            MenuOptionsScreen(
                onNavigateToProducts = {
                    storeViewModel.getProductsList()
                    navController.navigate(Products)
                },
                onNavigateToBranches = {
                    navController.navigate(Branches)
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable<Products> {
            ProductScreen(
                viewModel = storeViewModel,
                onNavigateToEditPizza = {
                    navController.navigate(EditPizza)
                },
                onNavigateToEditOther = {
                    navController.navigate(EditOtherProduct)
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable<EditPizza> {
            EditPizzaScreen(
                viewModel = storeViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable<EditOtherProduct> {
            EditOtherProductScreen(
                viewModel = storeViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable<Branches> {
            BranchScreen(
                viewModel = storeViewModel,
                onNavigateToEdit = {
                    navController.navigate(EditBranch)
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable<EditBranch> {
            EditBranchScreen(
                viewModel = storeViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
