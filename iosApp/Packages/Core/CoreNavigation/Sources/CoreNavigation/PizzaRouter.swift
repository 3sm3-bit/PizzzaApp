//
//  PizzaRouter.swift
//  CoreNavigation
//
//  Navegación entre features sin que una feature importe a otra.
//
//  Cada feature pide la pantalla de otra feature al router que está en el
//  Environment (`@Environment(\.pizzaRouter)`). La app (que sí conoce todas
//  las features) construye el router real y lo inyecta en `AppContentView`.
//

import SwiftUI
import Shared

public struct PizzaRouter {

    /// Pantalla de "Información y Ayuda" (FeatureInfo).
    public var info: () -> AnyView

    /// Selector de dirección en mapa (FeatureAddress).
    /// `onConfirm` recibe (dirección, latitud, longitud).
    public var addressSelection: (
        _ initialLat: String?,
        _ initialLng: String?,
        _ initialAddress: String?,
        _ onConfirm: @escaping (String, String, String) -> Void
    ) -> AnyView

    /// Tab de carrito (FeatureCart).
    public var cartTab: (_ onLogout: @escaping () -> Void, _ onOpenDrawer: @escaping () -> Void) -> AnyView

    /// Tab de pedidos (FeatureOrders).
    public var ordersTab: (OrdersTabInput) -> AnyView

    /// Seguimiento en vivo de un pedido (FeatureMonitoring).
    public var monitor: (ParentOrderModel) -> AnyView

    public init(
        info: @escaping () -> AnyView,
        addressSelection: @escaping (String?, String?, String?, @escaping (String, String, String) -> Void) -> AnyView,
        cartTab: @escaping (@escaping () -> Void, @escaping () -> Void) -> AnyView,
        ordersTab: @escaping (OrdersTabInput) -> AnyView,
        monitor: @escaping (ParentOrderModel) -> AnyView
    ) {
        self.info = info
        self.addressSelection = addressSelection
        self.cartTab = cartTab
        self.ordersTab = ordersTab
        self.monitor = monitor
    }
}

/// Datos que el Home le pasa al tab de pedidos.
public struct OrdersTabInput {
    public let orders: [ParentOrderModel]
    public let onRefresh: (_ forceLoading: Bool) -> Void
    public let onLogout: () -> Void
    public let onOpenDrawer: () -> Void

    public init(
        orders: [ParentOrderModel],
        onRefresh: @escaping (Bool) -> Void,
        onLogout: @escaping () -> Void,
        onOpenDrawer: @escaping () -> Void
    ) {
        self.orders = orders
        self.onRefresh = onRefresh
        self.onLogout = onLogout
        self.onOpenDrawer = onOpenDrawer
    }
}

public extension PizzaRouter {
    /// Router vacío: útil para Previews o tests de una feature aislada.
    static let empty = PizzaRouter(
        info: { AnyView(EmptyView()) },
        addressSelection: { _, _, _, _ in AnyView(EmptyView()) },
        cartTab: { _, _ in AnyView(EmptyView()) },
        ordersTab: { _ in AnyView(EmptyView()) },
        monitor: { _ in AnyView(EmptyView()) }
    )
}

private struct PizzaRouterKey: EnvironmentKey {
    static let defaultValue: PizzaRouter = .empty
}

public extension EnvironmentValues {
    var pizzaRouter: PizzaRouter {
        get { self[PizzaRouterKey.self] }
        set { self[PizzaRouterKey.self] = newValue }
    }
}
