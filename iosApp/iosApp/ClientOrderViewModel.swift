import Foundation
import Shared
import Combine

class ClientOrderViewModel: ObservableObject {
    @Published var orders: [ParentOrderModel] = []
    @Published var isLoading: Bool = false
    
    private let dataUseCase = KoinHelper.shared.getDataUseCase()
    
    func fetchOrders() {
        isLoading = true
        dataUseCase.getUserLocal { [weak self] user, error in
            guard let user = user else {
                DispatchQueue.main.async {
                    self?.isLoading = false
                    self?.orders = []
                }
                return
            }
            
            self?.dataUseCase.loadParentOrder(userId: user.uid) { response, error in
                DispatchQueue.main.async {
                    self?.isLoading = false
                    if let orders = response {
                        // Sort by date or state
                        self?.orders = orders.sorted(by: { $0.date > $1.date })
                    }
                }
            }
        }
    }
}
