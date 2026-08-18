import Foundation
import Shared
import Combine

class OrderViewModel: ObservableObject {
    private let dataUseCase = KoinHelper.shared.getDataUseCase()
    
    @Published var orders: [ParentOrderModel] = []
    @Published var filteredOrders: [ParentOrderModel] = []
    @Published var selectedFilter: String = "TODOS"
    @Published var isLoading: Bool = false
    
    init() {
        getGeneralOrderList()
    }
    
    func getGeneralOrderList() {
        isLoading = true
        dataUseCase.loadParentOrder(forceRefresh: false) { response, error in
            DispatchQueue.main.async {
                self.isLoading = false
                if let orders = response {
                    self.orders = orders
                    self.applyFilter(filter: self.selectedFilter)
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
                    self.orders = orders
                    self.applyFilter(filter: self.selectedFilter)
                }
            }
        }
    }
    
    func updateFilter(filter: String) {
        self.selectedFilter = filter
        self.applyFilter(filter: filter)
    }
    
    private func applyFilter(filter: String) {
        let cleanFilter = filter.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
        if cleanFilter == "TODOS" {
            self.filteredOrders = self.orders
        } else {
            self.filteredOrders = self.orders.filter { 
                $0.state.trimmingCharacters(in: .whitespacesAndNewlines).uppercased() == cleanFilter
            }
        }
    }
}
