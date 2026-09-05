import SwiftUI
import Shared

struct HomeTabView: View {
    @State private var selectedTab = 0
    @StateObject private var appViewModel = AppViewModel()
    @ObservedObject var cartManager = CartManager.shared
    @State private var showLogoutAlert = false
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        TabView(selection: $selectedTab) {
            PizzaListView(viewModel: appViewModel, onLogout: { showLogoutAlert = true })
                .tabItem {
                    Label("Pizza", systemImage: "pizzaslice")
                }
                .tag(0)
            
            ExtraListView(viewModel: appViewModel, onLogout: { showLogoutAlert = true })
                .tabItem {
                    Label("Extra", systemImage: "takeoutbag.and.cup.and.straw")
                }
                .tag(1)
            
            CartView(viewModel: appViewModel)
                .tabItem {
                    Label("Cart", systemImage: "cart")
                }
                .badge(cartManager.cart.count > 0 ? String(cartManager.cart.count) : nil)
                .tag(2)
            
            OrdersHistoryView(viewModel: appViewModel)
                .tabItem {
                    Label("Orden", systemImage: "doc.text.magnifyingglass")
                }
                .tag(3)
        }
        .accentColor(PizzaColors.red600)
        .alert(isPresented: $showLogoutAlert) {
            Alert(
                title: Text("Cerrar Sesión"),
                message: Text("¿Estás seguro de que deseas cerrar sesión?"),
                primaryButton: .destructive(Text("Sí, salir"), action: {
                    KoinHelper.shared.getDataUseCase().logout(completionHandler: { error in
                        DispatchQueue.main.async {
                            presentationMode.wrappedValue.dismiss()
                        }
                    })
                }),
                secondaryButton: .cancel(Text("No"))
            )
        }
    }
}
