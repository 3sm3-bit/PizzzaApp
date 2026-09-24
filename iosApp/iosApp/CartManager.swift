import Foundation
import Shared
internal import Combine

class CartManager: ObservableObject {
    
    static let shared = CartManager()
    
    @Published var cart: [OrderItemSwift] = []
    @Published var selectedTab: Int = 0
    @Published var ordersLoaded: Bool = false
    @Published var deliveryAddress: String = ""
    @Published var latitude: String = ""
    @Published var longitude: String = ""
    @Published var receptionMode: String = "DELIVERY"
    @Published var pizzaProducts: [ProductModel] = []
    @Published var extraProducts: [ProductModel] = []
    @Published var promotionsProducts: [ProductModel] = []
    @Published var deliveryProducts: [ProductModel] = []
    @Published var branches: [BranchModel] = []
    @Published var branchId: String = "1"
    @Published var selectedProduct: ProductModel? = nil
    
    private let dataUseCase = KoinHelper.shared.getDataUseCase()
    
    var totalPrice: Double {
        cart.reduce(0) { total, item in
            let basePrice = Double(item.product.price) ?? 0.0
            let crustPrice = item.cheeseFilledCrust ? (Double(item.product.priceChosse) ?? 0.0) : 0.0
            return total + (basePrice + crustPrice) * Double(item.quantity)
        }
    }

    var deliveryFee: Double {
        return receptionMode == "DELIVERY" ? round(totalPrice * 0.20) : 0.0
    }
    
    var finalTotal: Double {
        return totalPrice + deliveryFee
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

    func loadUserAddress() {
        dataUseCase.getUserLocal { user, error in
            if let localUser = user {
                DispatchQueue.main.async {
                    if self.deliveryAddress.isEmpty || self.deliveryAddress == "Selecciona dirección en el mapa" {
                        self.deliveryAddress = localUser.address
                        self.latitude = localUser.latitude
                        self.longitude = localUser.longitude
                    }
                }
            }
        }
    }
    
    func startPayment(onUrlReady: @escaping (String) -> Void) {
        let total = self.finalTotal
        
        self.dataUseCase.getUserLocal { [weak self] user, error in
            guard let self = self, let user = user else { return }
            let orderId = String(UUID().uuidString.prefix(12))
            self.dataUseCase.createPaymentSession(amount: self.finalTotal, email: user.email, orderId: orderId) { url, error in
                if let paymentUrl = url, !paymentUrl.isEmpty {
                    DispatchQueue.main.async {
                        onUrlReady(paymentUrl)
                    }
                }
            }
        }
    }

    private var isConfirmingOrder = false
    
    func confirmOrder(statePay: String = "PENDIENTE", onSuccess: @escaping () -> Void) {
        guard !isConfirmingOrder else { return }
        isConfirmingOrder = true
        
        dataUseCase.getUserLocal { user, error in
            defer { self.isConfirmingOrder = false }
            guard let user = user else { return }
            
            let idOrder = UUID().uuidString
            let cartTotal = self.totalPrice
            let deliveryPrice = self.receptionMode == "DELIVERY" ? String(format: "%.0f", round(cartTotal * 0.20)) : "0"
            
            let orders = self.cart.map { item in
                let basePrice = Double(item.product.price) ?? 0.0
                let crustPrice = item.cheeseFilledCrust ? (Double(item.product.priceChosse) ?? 0.0) : 0.0
                let totalItemPrice = (basePrice + crustPrice) * Double(item.quantity)
                
                let deliveryPrice = self.receptionMode == "DELIVERY" ? String(format: "%.0f", round(cartTotal * 0.20)) : "0"
                
                return OrderResponse(
                    uid: UUID().uuidString,
                    nameClient: user.names,
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
                    priceDelivery: deliveryPrice,
                    priceChosse: item.product.priceChosse,
                    idOrden: idOrder,
                    branchId: self.branchId,
                    stage: "1",
                    userId: user.uid,
                    driverId: "0",
                    latitude: self.receptionMode == "DELIVERY" ? (self.latitude.isEmpty ? user.latitude : self.latitude) : "0",
                    longitude: self.receptionMode == "DELIVERY" ? (self.longitude.isEmpty ? user.longitude : self.longitude) : "0",
                    currentLatitude: "0",
                    currentLongitude: "0",
                    statePay: statePay
                )
            }
            
            self.dataUseCase.createOrder(data: orders) { _, error in
                if error == nil {
                    DispatchQueue.main.async {
                        self.clearCart()
                        self.ordersLoaded = false
                        self.selectedTab = 3
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
