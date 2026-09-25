import SwiftUI
import Shared
import TaySwitfUILibrary

struct CartView: View {
    @ObservedObject var cartManager: CartManager = .shared
    @ObservedObject var viewModel: HomeViewModel
    var onLogout: () -> Void
    @State private var showAddressSelection = false
    @State private var showOrderSummary = false
    @State var destiny: ActionNav?
    
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
                UiToolBarHome(typeFlow: true, visibleCart: false) {
                    onLogout()
                }
                
                if cartManager.cart.isEmpty {
                    Spacer()
                    VStack(spacing: 16) {
                        Image(systemName: "cart.badge.minus")
                            .font(.system(size: 64))
                            .foregroundColor(.gray)
                        Text("Tu carrito está vacío")
                            .font(Font.uiMontB8)
                            .foregroundColor(.gray)
                    }
                    Spacer()
                } else {
                    ScrollView {
                        VStack(alignment: .leading, spacing: 16) {
                            Text("Selecciona modo de recojo")
                                .font(PizzaFonts.bold16)
                            
                            HStack(spacing: 12) {
                                ModeButton(label: "DOMICILIO", isSelected: cartManager.receptionMode == "DELIVERY") {
                                    cartManager.receptionMode = "DELIVERY"
                                }
                                ModeButton(label: "LOCAL", isSelected: cartManager.receptionMode == "RECOJO") {
                                    cartManager.receptionMode = "RECOJO"
                                }
                            }
                            
                            if cartManager.branches.count >= 2 {
                                Text("Elije sucursal")
                                    .font(PizzaFonts.bold16)
                                BranchSelectorView(cartManager: cartManager)
                            }
                           
                            if cartManager.receptionMode == "DELIVERY" {
                                Text("Cambiar dirección de entrega")
                                    .font(PizzaFonts.bold14)
                                Button(action: {
                                    destiny = .uiNextAlter
                                    //showAddressSelection = true
                                }) {
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
                                .buttonStyle(PlainButtonStyle())
                            }
                            
                            ForEach(cartManager.cart) { item in
                                CartItemCard(item: item) {
                                    cartManager.cart.removeAll(where: { $0.id == item.id })
                                }
                            }
                            
                            Spacer().frame(height: 100)
                        }
                        .padding(.horizontal, 24)
                        .padding(.vertical, 16)
                    }
                }
            }
             
            if !cartManager.cart.isEmpty {
                VStack {
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
                .padding(.bottom, 40)
                .onTapGesture {
                    destiny = .uiNext
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .bottomTrailing)
            }
        }
        .background(Color.uiTayGrey50.ignoresSafeArea())
        .navigationBarHidden(true)
        .onAppear {
            cartManager.loadUserAddress()
        }.uiTayNavigate(to: {
            AddressSelectionView(
                initialLat: cartManager.latitude,
                initialLng: cartManager.longitude,
                initialAddress: cartManager.deliveryAddress
            ) { address, lat, lng in
                cartManager.deliveryAddress = address
                cartManager.latitude = lat
                cartManager.longitude = lng
            }
        }, when: $destiny.cmToBool(.uiNextAlter))
        .uiTayNavigate(to: {
            OrderSummaryView(onConfirm: {
                DispatchQueue.main.async {
                    cartManager.selectedTab = 3
                }
            })
        }, when: $destiny.cmToBool(.uiNext))
    }
}

struct BranchSelectorView: View {
    @ObservedObject var cartManager: CartManager
    
    var body: some View {
        let branches: [BranchModel] = Array(cartManager.branches)
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 10) {
                ForEach(branches, id: \.identifier) { (branch: BranchModel) in
                    let isSelected = cartManager.branchId == branch.identifier
                    Button(action: { cartManager.branchId = branch.identifier }) {
                        Text(branch.nameBranch)
                            .font(Font.uiMontB10)
                            .padding(.horizontal, 12)
                            .frame(minWidth: 100)
                            .frame(height: 32)
                            .background(isSelected ? PizzaColors.green600 : Color.white)
                            .foregroundColor(isSelected ? .white : PizzaColors.green600)
                            .cornerRadius(10)
                            .overlay(
                                RoundedRectangle(cornerRadius: 10)
                                    .stroke(isSelected ? Color.clear : PizzaColors.green600, lineWidth: 1)
                            )
                    }
                }
            }
            .padding(.vertical, 4)
        }
    }
}

struct ModeButton: View {
    let label: String
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Text(label)
            .font(PizzaFonts.bold12)
            .frame(maxWidth: .infinity)
            .frame(height: 32)
            .background(isSelected ? Color.uiTayRed600 : Color.white)
            .foregroundColor(isSelected ? .white : Color.uiTayRed600)
            .cornerRadius(12)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(Color.uiTayRed600, lineWidth: 1)
            )
            .contentShape(Rectangle())
            .onTapGesture {
                action()
            }
    }
}

struct CartItemCard: View {
    let item: OrderItemSwift
    let onRemove: () -> Void
    
    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            HStack(alignment: .center) {
                HStack(alignment: .center, spacing: 12) {
                    Text("\(item.quantity)x")
                        .font(Font.uiMontB12)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(Color.uiTayRed600)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                    
                    Text(item.product.nameProduct.uppercased())
                        .font(Font.uiMontB14)
                        .foregroundColor(.black)
                }
                
                Spacer()
                
                Button(action: onRemove) {
                    Image(systemName: "trash")
                        .font(.system(size: 18))
                        .foregroundColor(Color.uiTayRed600)
                }
                .buttonStyle(PlainButtonStyle())
            }
      
            if item.product.type == "1" || !item.note.isEmpty {
                VStack(alignment: .leading, spacing: 2) {
                    if item.product.type == "1" {
                        HStack(spacing: 0) {
                            Text("Masa: \(item.typeDough), ")
                            if item.cheeseFilledCrust {
                                Text("Con Orilla de Queso")
                                    .foregroundColor(Color.uiTayGreen600)
                            }
                        }
                        .font(Font.uiMontM12)
                        .foregroundColor(.gray)
                    }
                    
                    if !item.note.isEmpty {
                        Text("Nota: \(item.note)")
                            .font(Font.uiMontM12)
                            .foregroundColor(.gray)
                            .italic()
                    }
                }
            }
      
            HStack {
                Spacer()
                Text("Total ")
                    .font(Font.uiMontM12)
                    .foregroundColor(.gray)
                
                let unitPrice = (Double(item.product.price) ?? 0.0) + (item.cheeseFilledCrust ? (Double(item.product.priceChosse) ?? 0.0) : 0.0)
                Text("$\(String(format: "%.2f", unitPrice * Double(item.quantity)))")
                    .font(Font.uiMontB16)
                    .foregroundColor(Color.uiTayRed600)
            }
        }.padding(14)
        .uiTayBgShadowDark()
    }
}
