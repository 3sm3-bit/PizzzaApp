import Foundation
import Shared
import Combine

class StoreViewModel: ObservableObject {
    @Published var pizzaProducts: [ProductModel] = []
    @Published var extraProducts: [ProductModel] = []
    @Published var isLoading: Bool = false
    
    private let dataUseCase = KoinHelper.shared.getDataUseCase()
    
    func fetchProducts() {
        isLoading = true
        // Just fetch from local DB, as sync was already done in Splash
        dataUseCase.getProducts { [weak self] response, error in
            DispatchQueue.main.async {
                self?.isLoading = false
                if let products = response {
                    self?.pizzaProducts = products.filter { $0.type == "1" }
                    self?.extraProducts = products.filter { $0.type == "2" || $0.type == "3" }
                }
            }
        }
    }
}
