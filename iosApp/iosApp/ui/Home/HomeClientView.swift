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
    @State var selectedTab = 0
    @State var tabs : [UITayTabItem] = []
    @State private var showLogoutAlert = false
    @EnvironmentObject var managerAPP: PizzaManagerAPP

    
    var views: [AnyView] {[
        AnyView(PizzaView(viewModel: viewModel, onLogout: { showLogoutAlert = true })),
        AnyView(ExtraView(viewModel: viewModel, onLogout: { showLogoutAlert = true })),
        AnyView(CartView(viewModel: viewModel,)),
        AnyView(OrdersView(viewModel: viewModel,)),
        AnyView(EmptyView())
    ]
    }
    
    var body: some View {
        BaseViewGeneral(viewModel: viewModel){
            UITayNavigationBotton(selectedTab:$selectedTab,
                               tabs: tabs,tayView:views)
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
