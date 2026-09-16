import Foundation
import Shared
internal import Combine

class StoreViewModel: ObservableObject {
    @Published var pizzaProducts: [ProductModel] = []
    @Published var extraProducts: [ProductModel] = []
    @Published var isLoading: Bool = false
    
    private let dataUseCase = KoinHelper.shared.getDataUseCase()
    
    func fetchProducts() {
        
    }
}
