import Foundation
import Shared
import Combine

class CartManager: ObservableObject {
    static let shared = CartManager()
    
    @Published var cart: [OrderItemSwift] = []
    @Published var deliveryAddress: String = ""
    @Published var receptionMode: String = "DELIVERY"
    
    private let dataUseCase = KoinHelper.shared.getDataUseCase()
    
    var totalPrice: Double {
        cart.reduce(0) { total, item in
            let basePrice = Double(item.product.price) ?? 0.0
            let crustPrice = item.cheeseFilledCrust ? (Double(item.product.priceChosse) ?? 0.0) : 0.0
            return total + (basePrice + crustPrice) * Double(item.quantity)
        }
    }
    
    func addToCart(
        product: ProductModel,
        quantity: Int = 1,
        typeDough: String = "TRADICIONAL",
        cheeseFilledCrust: Bool = false,
        note: String = ""
    ) {
        if product.type == "1" {
            // Pizzas are usually added as separate items if they have different configs
            cart.append(OrderItemSwift(
                product: product,
                quantity: quantity,
                typeDough: typeDough,
                cheeseFilledCrust: cheeseFilledCrust,
                note: note
            ))
        } else {
            // Extras are combined if same product
            if let index = cart.firstIndex(where: { $0.product.uid == product.uid }) {
                cart[index].quantity += quantity
            } else {
                cart.append(OrderItemSwift(product: product, quantity: quantity))
            }
        }
    }
    
    func removeItem(at indexSet: IndexSet) {
        cart.remove(atOffsets: indexSet)
    }
    
    func clearCart() {
        cart = []
    }
    
    func confirmOrder(onSuccess: @escaping () -> Void) {
        dataUseCase.getUserLocal { user, error in
            guard let user = user else { return }
            
            let orders = self.cart.map { item in
                let basePrice = Double(item.product.price) ?? 0.0
                let crustPrice = item.cheeseFilledCrust ? (Double(item.product.priceChosse) ?? 0.0) : 0.0
                let totalItemPrice = (basePrice + crustPrice) * Double(item.quantity)
                
                return OrderResponse(
                    uid: "",
                    nameClient: user.nameUser,
                    quantity: String(item.quantity),
                    type: item.product.type,
                    symbol: item.product.currencySymbol,
                    nameProduct: item.product.nameProduct,
                    tamanio: item.product.tamanio,
                    typeDough: item.typeDough,
                    cheeseFilledCrust: item.cheeseFilledCrust ? "SI" : "NO",
                    note: item.note,
                    phone: user.phone,
                    price: item.product.price,
                    priceTotal: String(format: "%.2f", totalItemPrice),
                    state: "CONFIRMADO",
                    date: "",
                    address: self.receptionMode == "DELIVERY" ? self.deliveryAddress : "",
                    reception: self.receptionMode,
                    priceDelivery: "0",
                    priceChosse: item.product.priceChosse,
                    idOrden: "",
                    branchId: "1",
                    stage: "1",
                    userId: user.uid,
                    driverId: "0",
                    latitude: user.latitude,
                    longitude: user.longitude,
                    currentLatitude: "0",
                    currentLongitude: "0",
                    statePay: "PENDIENTE"
                )
            }
            
            self.dataUseCase.createOrder(data: orders) { _, error in
                if error == nil {
                    DispatchQueue.main.async {
                        self.clearCart()
                        onSuccess()
                    }
                }
            }
        }
    }
}

struct OrderItemSwift: Identifiable {
    let id = UUID()
    let product: ProductModel
    var quantity: Int
    var typeDough: String = "TRADICIONAL"
    var cheeseFilledCrust: Bool = false
    var note: String = ""
}
