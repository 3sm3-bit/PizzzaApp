import Foundation
import Shared
import Combine

class AppViewModel: ObservableObject {
    @Published var pizzaProducts: [ProductModel] = []
    @Published var extraProducts: [ProductModel] = []
    @Published var deliveryProducts: [ProductModel] = []
    @Published var orders: [ParentOrderModel] = []
    @Published var selectedProduct: ProductModel? = nil
    @Published var selectedOrder: ParentOrderModel? = nil
    @Published var isLoading: Bool = false
    @Published var ordersLoaded: Bool = false
    
    private let dataUseCase = KoinHelper.shared.getDataUseCase()
    
    func syncProducts(onComplete: @escaping (Bool) -> Void) {
        isLoading = true
        dataUseCase.syncProducts { [weak self] _, error in
            if error != nil {
                DispatchQueue.main.async {
                    self?.isLoading = false
                    onComplete(false)
                }
                return
            }
            
            self?.getProductsList { success in
                onComplete(success)
            }
        }
    }
    
    func getProductsList(onComplete: ((Bool) -> Void)? = nil) {
        dataUseCase.getProducts { [weak self] response, error in
            DispatchQueue.main.async {
                self?.isLoading = false
                if let products = response {
                    self?.pizzaProducts = products.filter { $0.type == "1" }
                    self?.extraProducts = products.filter { $0.type == "2" || $0.type == "3" }
                    self?.deliveryProducts = products.filter { $0.type == "4" }
                    onComplete?(true)
                } else {
                    onComplete?(false)
                }
            }
        }
    }
    
    func getGeneralOrderList(forceLoading: Bool = false) {
        if ordersLoaded && !forceLoading { return }
        
        isLoading = forceLoading
        dataUseCase.getUserLocal { [weak self] user, error in
            guard let uid = user?.uid else {
                DispatchQueue.main.async {
                    self?.isLoading = false
                    self?.orders = []
                }
                return
            }
            
            self?.dataUseCase.loadParentOrder(userId: uid) { response, error in
                DispatchQueue.main.async {
                    self?.isLoading = false
                    if let orders = response {
                        self?.updateStateWithOrders(orders: orders)
                    }
                }
            }
        }
    }
    
    private func updateStateWithOrders(orders: [ParentOrderModel]) {
        self.orders = orders.sorted { o1, o2 in
            func priority(_ s: String) -> Int {
                switch s.trimmingCharacters(in: .whitespacesAndNewlines).uppercased() {
                case "CONFIRMADO": return 1
                case "LISTO": return 2
                default: return 3
                }
            }
            return priority(o1.state) < priority(o2.state)
        }
        self.ordersLoaded = true
    }
    
    func selectProduct(_ product: ProductModel?) {
        self.selectedProduct = product
    }
    
    func selectOrder(_ order: ParentOrderModel?) {
        self.selectedOrder = order
    }
}
