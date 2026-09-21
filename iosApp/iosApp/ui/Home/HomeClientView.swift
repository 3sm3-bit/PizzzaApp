//
//  HomeClientView.swift
//  iosApp
//
//  Created by Developer on 12/09/26.
//

import SwiftUI
import TaySwitfUILibrary

struct HomeClientView: View {
    
    @ObservedObject private var viewModel: HomeViewModel = Resolver.shared.resolve(HomeViewModel.self)
    @ObservedObject var cartManager = CartManager.shared
    @State private var badgeInfo: (Int, Int) = (2, 0)
    @State var tabs : [UITayTabItem] = []
    @State private var showLogoutAlert = false
    @EnvironmentObject var managerAPP: PizzaManagerAPP

    
    var views: [AnyView] {[
        AnyView(PizzaView(viewModel: viewModel, onLogout: { showLogoutAlert = true })),
        AnyView(ExtraView(viewModel: viewModel, onLogout: { showLogoutAlert = true })),
        AnyView(CartView(viewModel: viewModel,onLogout: { showLogoutAlert = true })),
        AnyView(OrdersView(viewModel: viewModel,onLogout: { showLogoutAlert = true })),
        AnyView(EmptyView())
    ]
    }
    
    var body: some View {
        BaseViewGeneral(viewModel: viewModel){
            UITayNavigationBotton(selectedTab: $cartManager.selectedTab,
                               tabs: tabs,
                               tayView:views,
                               uiBadge: $badgeInfo,
                               uiTypeBadge: cartManager.cart.count > 0)
            .onChange(of: cartManager.cart.count) { oldValue, newValue in
                badgeInfo = (2, newValue)
            }


            .onAppear{
                self.tabs = [
                    UITayTabItem(id : 0,iconName: "ic_pizza",title: "Pizza"),
                    UITayTabItem(id : 1,iconName: "ic_extra", title: "Extra"),
                    UITayTabItem(id : 2,iconName: "ic_cart", title: "Cart"),
                    UITayTabItem(id : 3,iconName: "ic_orden", title: "Orden")
                ]
                
            }
            
        }.alert(isPresented: $showLogoutAlert) {
            Alert(
                title: Text("Cerrar Sesión"),
                message: Text("¿Estás seguro de que deseas cerrar sesión?"),
                primaryButton: .destructive(Text("Sí, salir"), action: {
                    viewModel.logout()
                    managerAPP.currentScreen = .auth
                }),
                secondaryButton: .cancel(Text("No"))
            )
        }
    }
        
   
}
