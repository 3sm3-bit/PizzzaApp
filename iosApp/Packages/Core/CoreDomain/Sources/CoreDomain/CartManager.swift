import Foundation
import Shared
import Combine

public class CartManager: ObservableObject {
    
    public static let shared = CartManager()
    
    @Published public var cart: [OrderItemSwift] = []
    @Published public var selectedTab: Int = 0
    @Published public var ordersLoaded: Bool = false
    @Published public var deliveryAddress: String = ""
    @Published public var latitude: String = ""
    @Published public var longitude: String = ""
    @Published public var receptionMode: String = "DELIVERY"
    @Published public var pizzaProducts: [ProductModel] = []
    @Published public var extraProducts: [ProductModel] = []
    @Published public var promotionsProducts: [ProductModel] = []
    @Published public var deliveryProducts: [ProductModel] = []
    @Published public var branches: [BranchModel] = []
    @Published public var branchId: String = "1"
    @Published public var selectedProduct: ProductModel? = nil
    @Published public var pendingOrderUid: String = ""
    @Published public var userName: String = ""
    @Published public var serverCalculatedDeliveryFee: Double? = nil
    
    private let dataUseCase = KoinHelper.shared.getDataUseCase()
    
    public var totalPrice: Double {
        cart.reduce(0) { total, item in
            let basePrice = Double(item.product.price) ?? 0.0
            let isCheese = (item.typeDough != "CRUJIENTE" && item.cheeseFilledCrust)
            let crustPrice = isCheese ? (Double(item.product.priceChosse) ?? 0.0) : 0.0
            return total + (basePrice + crustPrice) * Double(item.quantity)
        }
    }

    public var deliveryFee: Double {
        guard receptionMode == "DELIVERY" else { return 0.0 }
        if let serverFee = serverCalculatedDeliveryFee { return serverFee }
        return Double(deliveryProducts.first?.price ?? "") ?? 40.0
    }
    
    public var finalTotal: Double {
        return totalPrice + deliveryFee
    }
    
    public func addToCart(
        product: ProductModel,
        quantity: Int = 1,
        typeDough: String = "TRADICIONAL",
        cheeseFilledCrust: Bool = false,
        note: String = ""
    ) {
        let finalCheeseCrust = (typeDough == "CRUJIENTE") ? false : cheeseFilledCrust
        if product.type == "1" {
            // Pizzas are usually added as separate items if they have different configs
            cart.append(OrderItemSwift(
                product: product,
                quantity: quantity,
                typeDough: typeDough,
                cheeseFilledCrust: finalCheeseCrust,
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
    
    public func removeItem(at indexSet: IndexSet) {
        cart.remove(atOffsets: indexSet)
    }
    
    public func clearCart() {
        cart = []
        userName = ""
        deliveryAddress = ""
        latitude = ""
        longitude = ""
        serverCalculatedDeliveryFee = nil
        ordersLoaded = false
        selectedTab = 0
    }

    public func updateDeliveryAddress(address: String, lat: String, lng: String) {
        self.deliveryAddress = address
        self.latitude = lat
        self.longitude = lng
        self.serverCalculatedDeliveryFee = nil
    }

    public func fetchServerDeliveryPrice(completion: @escaping () -> Void) {
        guard receptionMode == "DELIVERY", !branchId.isEmpty, !latitude.isEmpty, !longitude.isEmpty else {
            completion()
            return
        }
        dataUseCase.calculatePriceDelivery(branchId: branchId, latitude: latitude, longitude: longitude) { [weak self] result, error in
            if let priceStr = result, let price = Double(priceStr) {
                DispatchQueue.main.async {
                    self?.serverCalculatedDeliveryFee = price
                }
            }
            DispatchQueue.main.async {
                completion()
            }
        }
    }

    public func updateWithHomeData(_ homeData: HomeDataModel) {
        self.pizzaProducts = homeData.pizzaProducts
        self.extraProducts = homeData.extraProducts
        self.promotionsProducts = homeData.promotionsProducts
        self.deliveryProducts = homeData.deliveryProducts
        if !homeData.branches.isEmpty {
            self.branches = homeData.branches
            self.branchId = homeData.defaultBranchId
        }
    }

    public func loadUserAddress() {
        dataUseCase.getUserLocal { user, error in
            if let localUser = user {
                DispatchQueue.main.async {
                    self.userName = localUser.names
                    if self.deliveryAddress.isEmpty || self.deliveryAddress == "Selecciona dirección en el mapa" {
                        self.deliveryAddress = localUser.address
                        self.latitude = localUser.latitude
                        self.longitude = localUser.longitude
                    }
                }
            }
        }
    }
    
    public func startPayment(onUrlReady: @escaping (String) -> Void) {
        guard !cart.isEmpty else { return }
        
        self.dataUseCase.getUserLocal { [weak self] user, error in
            guard let self = self, let user = user else { return }
            let idOrder = UUID().uuidString
            let isDelivery = self.receptionMode == "DELIVERY"
            let deliveryFeeDouble = self.deliveryFee
            let deliveryPriceStr = isDelivery ? String(format: "%.0f", deliveryFeeDouble) : "0"
            
            let orders = self.cart.enumerated().map { index, item in
                let basePrice = Double(item.product.price) ?? 0.0
                let isCheese = (item.typeDough != "CRUJIENTE" && item.cheeseFilledCrust)
                let crustPrice = isCheese ? (Double(item.product.priceChosse) ?? 0.0) : 0.0
                let itemSubtotal = (basePrice + crustPrice) * Double(item.quantity)
                let totalItemPrice = (isDelivery && index == 0) ? (itemSubtotal + deliveryFeeDouble) : itemSubtotal
                
                return OrderResponse(
                    uid: UUID().uuidString,
                    nameClient: user.names,
                    quantity: String(item.quantity),
                    type: item.product.type,
                    symbol: item.product.currencySymbol,
                    nameProduct: item.product.nameProduct,
                    tamanio: item.product.tamanio,
                    typeDough: item.typeDough,
                    cheeseFilledCrust: isCheese ? "SI" : "NO",
                    note: item.note,
                    phone: user.phone,
                    price: item.product.price,
                    priceTotal: String(format: "%.0f", totalItemPrice),
                    state: "PENDIENTE",
                    date: "",
                    address: self.receptionMode == "DELIVERY" ? self.deliveryAddress : "",
                    reception: self.receptionMode,
                    priceDelivery: deliveryPriceStr,
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
                    statePay: "PENDIENTE",
                    canal: "I7K4M3",
                    idProductPoster: item.product.idProductPoster
                )
            }
            
            // 1. POST /pizzzeria/order/mobile -> crea la orden
            self.dataUseCase.createOrderMobile(data: orders) { mobileResponse, error in
                let generalUid = mobileResponse?.ordenGeneral?.uid ?? ""
                if !generalUid.isEmpty {
                    DispatchQueue.main.async {
                        self.pendingOrderUid = generalUid
                    }
                }
                
                let orderId = !generalUid.isEmpty ? generalUid : String(UUID().uuidString.prefix(12))
                
                // 2. POST /services/payment/create -> devuelve Stripe checkout URL
                self.dataUseCase.createPaymentSession(amount: self.finalTotal, email: user.email, orderId: orderId) { url, error in
                    if let paymentUrl = url, !paymentUrl.isEmpty {
                        DispatchQueue.main.async {
                            onUrlReady(paymentUrl)
                        }
                    }
                }
            }
        }
    }

    private var isConfirmingOrder = false
    
    public func confirmOrder(statePay: String = "PENDIENTE", onSuccess: @escaping () -> Void) {
        guard !isConfirmingOrder else { return }
        isConfirmingOrder = true
        
        defer { self.isConfirmingOrder = false }
        
        let pendingUid = self.pendingOrderUid
        
        if !pendingUid.isEmpty {
            // 3. PUT /pizzzeria/order/mobile/:id/confirm -> marca la orden como pagada/confirmada
            self.dataUseCase.confirmOrderMobile(ordenGeneralUid: pendingUid, request: ConfirmOrderRequest(state: "CONFIRMADO", statePay: statePay)) { _, _ in
                DispatchQueue.main.async {
                    self.clearCart()
                    self.pendingOrderUid = ""
                    self.ordersLoaded = false
                    self.selectedTab = 3
                    onSuccess()
                }
            }
        } else {
            // Fallback si no había pendingOrderUid
            dataUseCase.getUserLocal { user, error in
                guard let user = user else { return }
                
                let idOrder = UUID().uuidString
                let isDelivery = self.receptionMode == "DELIVERY"
                let deliveryFeeDouble = self.deliveryFee
                let deliveryPriceStr = isDelivery ? String(format: "%.0f", deliveryFeeDouble) : "0"
                
                let orders = self.cart.enumerated().map { index, item in
                    let basePrice = Double(item.product.price) ?? 0.0
                    let isCheese = (item.typeDough != "CRUJIENTE" && item.cheeseFilledCrust)
                    let crustPrice = isCheese ? (Double(item.product.priceChosse) ?? 0.0) : 0.0
                    let itemSubtotal = (basePrice + crustPrice) * Double(item.quantity)
                    let totalItemPrice = (isDelivery && index == 0) ? (itemSubtotal + deliveryFeeDouble) : itemSubtotal
                    
                    return OrderResponse(
                        uid: UUID().uuidString,
                        nameClient: user.names,
                        quantity: String(item.quantity),
                        type: item.product.type,
                        symbol: item.product.currencySymbol,
                        nameProduct: item.product.nameProduct,
                        tamanio: item.product.tamanio,
                        typeDough: item.typeDough,
                        cheeseFilledCrust: isCheese ? "SI" : "NO",
                        note: item.note,
                        phone: user.phone,
                        price: item.product.price,
                        priceTotal: String(format: "%.0f", totalItemPrice),
                        state: "CONFIRMADO",
                        date: "",
                        address: self.receptionMode == "DELIVERY" ? self.deliveryAddress : "",
                        reception: self.receptionMode,
                        priceDelivery: deliveryPriceStr,
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
                        statePay: statePay,
                        canal: "A1P9X2",
                        idProductPoster: item.product.idProductPoster
                    )
                }
                
                self.dataUseCase.createOrderMobile(data: orders) { mobileResponse, error in
                    if let generalUid = mobileResponse?.ordenGeneral?.uid, !generalUid.isEmpty {
                        self.dataUseCase.confirmOrderMobile(ordenGeneralUid: generalUid, request: ConfirmOrderRequest(state: "CONFIRMADO", statePay: statePay)) { _, _ in
                            DispatchQueue.main.async {
                                self.clearCart()
                                self.pendingOrderUid = ""
                                self.ordersLoaded = false
                                self.selectedTab = 3
                                onSuccess()
                            }
                        }
                    } else {
                        DispatchQueue.main.async {
                            self.clearCart()
                            self.pendingOrderUid = ""
                            self.ordersLoaded = false
                            self.selectedTab = 3
                            onSuccess()
                        }
                    }
                }
            }
        }
    }

    public func cancelPendingOrder(onComplete: (() -> Void)? = nil) {
        let pendingUid = self.pendingOrderUid

        DispatchQueue.main.async {
            self.clearCart()
            self.pendingOrderUid = ""
        }

        onComplete?()

        if !pendingUid.isEmpty {
            self.dataUseCase.deleteGeneralOrder(orderId: pendingUid) { _, error in
                if let error = error {
                    print("⚠️ Error deleting pending order on iOS: \(error.localizedDescription)")
                }
            }
        }
    }
}

public struct OrderItemSwift: Identifiable {
    public let id = UUID()
    public let product: ProductModel
    public var quantity: Int
    public var typeDough: String = "TRADICIONAL"
    public var cheeseFilledCrust: Bool = false
    public var note: String = ""

    public init(product: ProductModel, quantity: Int, typeDough: String = "TRADICIONAL", cheeseFilledCrust: Bool = false, note: String = "") {
        self.product = product
        self.quantity = quantity
        self.typeDough = typeDough
        self.cheeseFilledCrust = cheeseFilledCrust
        self.note = note
    }
}
