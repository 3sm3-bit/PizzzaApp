//
//  AppContentView.swift
//  iosApp
//
//  Created by Developer on 12/09/26.
//
//  Raíz de la app: decide qué pantalla mostrar según `PizzaManagerAPP`
//  y conecta las features entre sí a través de `PizzaRouter`.
//  Es el único lugar que importa todas las features.
//

import SwiftUI
import CoreNavigation
import FeatureAuth
import FeatureAddress
import FeatureCart
import FeatureHome
import FeatureInfo
import FeatureMonitoring
import FeatureOrders

struct AppContentView: View {
    @EnvironmentObject var flowManager: PizzaManagerAPP

    var body: some View {
        Group {
            switch flowManager.currentScreen {
            case .splash:
                SplashView()
            case .auth:
                NavigationStack {
                    LoginView()
                }
            case .home:
                NavigationStack {
                    HomeClientView()
                }
            case .none:
                EmptyView()
            }
        }
        .environment(\.pizzaRouter, .live)
        .environmentObject(flowManager)
    }
}

extension PizzaRouter {
    /// Router real de la app: aquí se decide qué feature resuelve cada ruta.
    static let live = PizzaRouter(
        info: {
            AnyView(InfoMainView())
        },
        addressSelection: { lat, lng, address, onConfirm in
            AnyView(AddressSelectionView(
                initialLat: lat,
                initialLng: lng,
                initialAddress: address,
                onConfirm: onConfirm
            ))
        },
        cartTab: { onLogout, onOpenDrawer in
            AnyView(CartView(onLogout: onLogout, onOpenDrawer: onOpenDrawer))
        },
        ordersTab: { input in
            AnyView(OrdersView(input: input))
        },
        monitor: { order in
            AnyView(MonitorView(order: order))
        }
    )
}
