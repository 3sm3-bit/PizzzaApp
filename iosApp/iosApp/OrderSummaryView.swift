import SwiftUI
import Shared

struct OrderSummaryView: View {
    @ObservedObject var cartManager = CartManager.shared
    @Environment(\.presentationMode) var presentationMode
    var onConfirm: () -> Void
    
    var body: some View {
        VStack(spacing: 0) {
            PizzaToolbar(title: "Resumen de Pedido", onBack: {
                presentationMode.wrappedValue.dismiss()
            })
            
            ScrollView {
                VStack(spacing: 16) {
                    VStack(alignment: .leading, spacing: 20) {
                        Text("Detalles de tu Orden")
                            .font(PizzaFonts.bold18)
                            .padding(.bottom, 8)
                        
                        ForEach(cartManager.cart) { item in
                            VStack(alignment: .leading, spacing: 4) {
                                HStack(alignment: .top) {
                                    VStack(alignment: .leading) {
                                        Text("\(item.quantity)x \(item.product.nameProduct.uppercased())")
                                            .font(PizzaFonts.bold14)
                                        if item.product.type == "1" {
                                            Text("Masa: \(item.typeDough)\(item.cheeseFilledCrust ? " + Orilla Queso" : "")")
                                                .font(PizzaFonts.medium12)
                                                .foregroundColor(.gray)
                                        }
                                        if !item.note.isEmpty {
                                            Text("Nota: \(item.note)")
                                                .font(PizzaFonts.medium12)
                                                .foregroundColor(.gray)
                                                .italic()
                                        }
                                    }
                                    Spacer()
                                    
                                    let itemPrice = (Double(item.product.price) ?? 0.0) + (item.cheeseFilledCrust ? (Double(item.product.priceChosse) ?? 0.0) : 0.0)
                                    Text("$\(String(format: "%.2f", itemPrice * Double(item.quantity)))")
                                        .font(PizzaFonts.bold14)
                                }
                                Divider().background(Color(hex: 0xF0F2F5))
                            }
                        }
                        
                        // TODO: Add delivery fee if applicable
                        
                        // Delivery Info
                        VStack(alignment: .leading, spacing: 8) {
                            Text(cartManager.receptionMode == "RECOJO" ? "RECOJO EN LOCAL" : "ENTREGA A DOMICILIO")
                                .font(PizzaFonts.bold12)
                                .foregroundColor(PizzaColors.red600)
                            
                            if cartManager.receptionMode == "DELIVERY" {
                                Text(cartManager.deliveryAddress)
                                    .font(PizzaFonts.medium12)
                                    .foregroundColor(.gray)
                                    .lineLimit(2)
                            }
                        }
                        .padding(12)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color(hex: 0xF0F2F5))
                        .cornerRadius(12)
                        
                        // Total
                        HStack {
                            Text("Total a Pagar")
                                .font(PizzaFonts.bold16)
                            Spacer()
                            Text("$\(String(format: "%.2f", cartManager.totalPrice))")
                                .font(PizzaFonts.bold20)
                                .foregroundColor(PizzaColors.red600)
                        }
                    }
                    .padding(20)
                    .background(Color.white)
                    .cornerRadius(24)
                    .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)
                }
                .padding(16)
            }
            
            PizzaButton(title: "Confirmar y Enviar Pedido", isEnabled: true) {
                cartManager.confirmOrder {
                    onConfirm()
                }
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 32)
        }
        .background(PizzaColors.background)
        .navigationBarHidden(true)
    }
}
