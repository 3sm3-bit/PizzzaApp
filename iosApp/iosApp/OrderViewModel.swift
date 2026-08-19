import Foundation
import Shared
import Combine

class OrderViewModel: ObservableObject {
    private let dataUseCase = KoinHelper.shared.getDataUseCase()
    
    @Published var orders: [ParentOrderModel] = []
    @Published var countConfirmado: Int = 0
    @Published var countListo: Int = 0
    @Published var isLoading: Bool = false
    @Published var selectedOrder: ParentOrderModel? = nil
    @Published var products: [ProductModel] = []
    
    init() {
        getGeneralOrderList()
    }
    
    func getProductsList() {
        isLoading = true
        dataUseCase.getProducts { response, error in
            DispatchQueue.main.async {
                self.isLoading = false
                if let products = response {
                    self.products = products
                }
            }
        }
    }
    
    func getGeneralOrderList() {
        isLoading = true
        dataUseCase.loadParentOrder(forceRefresh: false) { response, error in
            DispatchQueue.main.async {
                self.isLoading = false
                if let orders = response {
                    self.updateStateWithOrders(orders: orders)
                }
            }
        }
    }
    
    func refresh() {
        isLoading = true
        dataUseCase.loadParentOrder(forceRefresh: true) { response, error in
            DispatchQueue.main.async {
                self.isLoading = false
                if let orders = response {
                    self.updateStateWithOrders(orders: orders)
                }
            }
        }
    }
    
    private func updateStateWithOrders(orders: [ParentOrderModel]) {
        let sortedOrders = orders.sorted { o1, o2 in
            let s1 = o1.state.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
            let s2 = o2.state.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
            
            func priority(_ s: String) -> Int {
                if s == "CONFIRMADO" { return 1 }
                if s == "LISTO" { return 2 }
                return 3
            }
            
            return priority(s1) < priority(s2)
        }
        
        self.orders = sortedOrders
        self.countConfirmado = orders.filter { $0.state.trimmingCharacters(in: .whitespacesAndNewlines).uppercased() == "CONFIRMADO" }.count
        self.countListo = orders.filter { $0.state.trimmingCharacters(in: .whitespacesAndNewlines).uppercased() == "LISTO" }.count
    }
    
    func updateOrderState(order: ParentOrderModel, newState: String) {
        if order.state.trimmingCharacters(in: .whitespacesAndNewlines).uppercased() == newState.uppercased() { return }
        
        // 1. Rollback state
        let previousOrders = self.orders
        let previousConfirmado = self.countConfirmado
        let previousListo = self.countListo
        
        // 2. Optimistic Update
        let updatedOrders = self.orders.map { 
            if $0.uid == order.uid {
                return $0.doCopy(
                    uid: $0.uid,
                    nameClient: $0.nameClient,
                    description: $0.description,
                    price: $0.price,
                    phone: $0.phone,
                    date: $0.date,
                    state: newState,
                    address: $0.address,
                    reception: $0.reception,
                    orders: $0.orders
                )
            }
            return $0
        }
        
        self.updateStateWithOrders(orders: updatedOrders)
        
        // 3. Sync in background
        dataUseCase.updateOrder(data: order.doCopy(
            uid: order.uid,
            nameClient: order.nameClient,
            description: order.description,
            price: order.price,
            phone: order.phone,
            date: order.date,
            state: newState,
            address: order.address,
            reception: order.reception,
            orders: order.orders
        )) { response, error in
            if error != nil {
                DispatchQueue.main.async {
                    self.orders = previousOrders
                    self.countConfirmado = previousConfirmado
                    self.countListo = previousListo
                    // En una app real mostraríamos un Toast o Alert aquí
                }
            } else {
                // Éxito: recargamos para asegurar sincronización total
                self.getGeneralOrderList()
            }
        }
    }
    
    func avanzarEstado(order: ParentOrderModel) {
        let currentState = order.state.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
        var nextState = "CONFIRMADO"
        if currentState == "CONFIRMADO" {
            nextState = "LISTO"
        } else if currentState == "LISTO" {
            nextState = "ENTREGADO"
        }
        updateOrderState(order: order, newState: nextState)
    }
}

// Extensión para facilitar la copia del modelo (KMP models don't have direct copy in Swift usually)
extension ParentOrderModel {
    func doCopy(uid: String, nameClient: String, description: String, price: String, phone: String, date: String, state: String, address: String, reception: String, orders: [OrderModel]) -> ParentOrderModel {
        return ParentOrderModel(uid: uid, nameClient: nameClient, description: description, price: price, phone: phone, date: date, state: state, address: address, reception: reception, orders: orders)
    }
}
