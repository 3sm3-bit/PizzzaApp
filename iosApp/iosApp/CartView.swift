import SwiftUI
import Shared

struct CartView: View {
    @ObservedObject var cartManager = CartManager.shared
    @ObservedObject var viewModel: AppViewModel
    @State private var showAddressSelection = false
    @State private var showOrderSummary = false
    
    var isButtonEnabled: Bool {
        if cartManager.receptionMode == "RECOJO" {
            return !cartManager.cart.isEmpty
        } else {
            return !cartManager.cart.isEmpty && !cartManager.deliveryAddress.isEmpty && cartManager.deliveryAddress != "Selecciona dirección en el mapa"
        }
    }
    
    var body: some View {
        ZStack {
            VStack(spacing: 0) {
                PizzaToolbar(title: "Tu Carrito", showBackButton: false)
                
                if cartManager.cart.isEmpty {
                    Spacer()
                    VStack(spacing: 16) {
                        Image(systemName: "cart.badge.minus")
                            .font(.system(size: 64))
                            .foregroundColor(.gray)
                        Text("Tu carrito está vacío")
                            .font(PizzaFonts.bold18)
                            .foregroundColor(.gray)
                    }
                    Spacer()
                } else {
                    List {
                        Section {
                            VStack(alignment: .leading) {
                                Text("Selecciona modo de recojo")
                                    .font(PizzaFonts.bold16)
                                Spacer().frame(height: 8)
                                
                                HStack(spacing: 12) {
                                    ModeButton(label: "DOMICILIO", isSelected: cartManager.receptionMode == "DELIVERY") {
                                        cartManager.receptionMode = "DELIVERY"
                                    }
                                    ModeButton(label: "LOCAL", isSelected: cartManager.receptionMode == "RECOJO") {
                                        cartManager.receptionMode = "RECOJO"
                                    }
                                }
                                
                                if cartManager.receptionMode == "DELIVERY" {
                                    Spacer().frame(height: 16)
                                    Text("Cambiar dirección de entrega")
                                        .font(PizzaFonts.bold14)
                                    Spacer().frame(height: 4)
                                    Button(action: { showAddressSelection = true }) {
                                        HStack {
                                            Text(cartManager.deliveryAddress.isEmpty ? "Selecciona dirección en el mapa" : cartManager.deliveryAddress)
                                                .font(PizzaFonts.medium12)
                                                .foregroundColor(cartManager.deliveryAddress.isEmpty ? .gray : .black)
                                                .lineLimit(1)
                                            Spacer()
                                            Image(systemName: "location.fill")
                                                .foregroundColor(.black)
                                        }
                                        .padding()
                                        .background(Color.white)
                                        .cornerRadius(12)
                                        .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.black, lineWidth: 1))
                                    }
                                }
                            }
                            .padding(.vertical, 8)
                        }
                        
                        Section {
                            ForEach(cartManager.cart) { item in
                                CartItemCard(item: item) {
                                    if let index = cartManager.cart.firstIndex(where: { $0.id == item.id }) {
                                        cartManager.removeItem(at: IndexSet(integer: index))
                                    }
                                }
                            }
                        }
                        
                        Section { Spacer().frame(height: 80) }
                    }
                    .listStyle(PlainListStyle())
                }
            }
            
            // FAB Continue Button
            if !cartManager.cart.isEmpty {
                VStack {
                    Spacer()
                    HStack {
                        Spacer()
                        Button(action: { if isButtonEnabled { showOrderSummary = true } }) {
                            Image(systemName: "arrow.right")
                                .font(.system(size: 24, weight: .bold))
                                .foregroundColor(.white)
                                .frame(width: 60, height: 60)
                                .background(isButtonEnabled ? PizzaColors.green600 : Color.gray.opacity(0.5))
                                .cornerRadius(16)
                                .shadow(radius: 8)
                        }
                        .disabled(!isButtonEnabled)
                        .padding(16)
                    }
                }
            }
        }
        .background(PizzaColors.background)
        .onAppear {
            viewModel.getProductsList()
        }
        .sheet(isPresented: $showAddressSelection) {
            AddressSelectionView { address, lat, lng in
                cartManager.deliveryAddress = address
            }
        }
        .fullScreenCover(isPresented: $showOrderSummary) {
            OrderSummaryView {
                showOrderSummary = false
            }
        }
    }
}

struct ModeButton: View {
    let label: String
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text(label)
                .font(PizzaFonts.bold12)
                .frame(maxWidth: .infinity)
                .frame(height: 32)
                .background(isSelected ? PizzaColors.red600 : Color.white)
                .foregroundColor(isSelected ? .white : PizzaColors.red600)
                .cornerRadius(12)
                .overlay(isSelected ? nil : RoundedRectangle(cornerRadius: 12).stroke(PizzaColors.red600, lineWidth: 1))
        }
    }
}

struct CartItemCard: View {
    let item: OrderItemSwift
    let onRemove: () -> Void
    
    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: item.product.type == "1" ? "pizza" : "takeoutbag.and.cup.and.straw")
                .resizable()
                .scaledToFit()
                .frame(width: 50, height: 50)
                .padding(8)
                .background(PizzaColors.red50)
                .cornerRadius(12)
            
            VStack(alignment: .leading, spacing: 4) {
                Text(item.product.nameProduct)
                    .font(PizzaFonts.bold16)
                Text("Cantidad: \(item.quantity)")
                    .font(PizzaFonts.medium12)
                    .foregroundColor(.gray)
                if item.product.type == "1" {
                    Text("\(item.typeDough)\(item.cheeseFilledCrust ? " + Orilla Queso" : "")")
                        .font(PizzaFonts.medium10)
                        .foregroundColor(PizzaColors.red600)
                }
            }
            Spacer()
            
            VStack(alignment: .trailing) {
                let itemPrice = (Double(item.product.price) ?? 0.0) + (item.cheeseFilledCrust ? (Double(item.product.priceChosse) ?? 0.0) : 0.0)
                Text("\(item.product.currencySymbol)\(String(format: "%.2f", itemPrice * Double(item.quantity)))")
                    .font(PizzaFonts.bold16)
                
                Button(action: onRemove) {
                    Image(systemName: "trash")
                        .foregroundColor(.red)
                }
            }
        }
        .padding()
        .background(Color.white)
        .cornerRadius(12)
    }
}
