//
//  HomeViewModel.swift
//  iosApp
//
//  Created by Developer on 12/09/26.
//
import Shared

@MainActor
class HomeViewModel: BaseViewModel {
    
    private let dataUseCase = KoinHelper.shared.getDataUseCase()
    @Published var pizzaProducts: [ProductModel] = []
    @Published var orders: [ParentOrderModel] = []
    @Published var isLoadingOrders: Bool = false
    
    func getGeneralOrderList(forceLoading: Bool = false) {
        let isAlreadyLoaded = CartManager.shared.ordersLoaded
        
        if isAlreadyLoaded && !forceLoading { return }
        
        Task {
            await self.execute(loading: forceLoading) {
                let user = try await self.dataUseCase.getUserLocal()
                let response = try await self.dataUseCase.loadParentOrder(userId: user?.uid ?? "")
                
                // Ordenar por prioridad igual que en Android
                self.orders = response.sorted { o1, o2 in
                    func priority(_ s: String) -> Int {
                        switch s.trimmingCharacters(in: .whitespacesAndNewlines).uppercased() {
                        case "CONFIRMADO": return 1
                        case "LISTO": return 2
                        default: return 3
                        }
                    }
                    return priority(o1.state) < priority(o2.state)
                }
                CartManager.shared.ordersLoaded = true
            }
        }
    }
    
    func logout() {
        Task{
            await self.execute() {
                try await self.dataUseCase.logout()
            }
        }
    }
}
