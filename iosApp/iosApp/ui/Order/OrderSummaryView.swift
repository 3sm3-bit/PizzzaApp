

import SwiftUI
import Shared
import TaySwitfUILibrary

struct OrderSummaryView: View {
    @ObservedObject var cartManager: CartManager = .shared
    @State private var showPaymentWebView = false
    @State private var paymentUrl: URL? = nil
    @State private var isProcessing = false
    @State var destiny: ActionNav?
    @Environment(\.dismiss) var dismiss

    var onConfirm: () -> Void

    var body: some View {
        BaseViewGeneral {
            VStack(spacing: 0) {
                UiTayCToolBar(uiTayText: "Resumen de Pedido") { _ in
                    dismiss()
                }
                
                ScrollView {
                    orderContentCard
                        .padding(16)
                }
                
                confirmButton
            }
            .background(Color.uiTayGrey50)
            .uiTayNavigate(
                item: self.paymentUrl,
                to: { url in
                    PaymentWebView(
                        url: url,
                        onSuccess: {
                            showPaymentWebView = false
                            cartManager.confirmOrder(statePay: "PAGADO") {
                                dismiss()
                                onConfirm()
                            }
                        },
                        onCancel: {
                            dismiss()
                        }
                    )
                },
                when: $destiny.cmToBool(.uiNext)
            )
        }
    }


    private var orderContentCard: some View {
        let cartItems: [OrderItemSwift] = Array(cartManager.cart)
        
        return VStack(alignment: .leading, spacing: 20) {
            Text("Detalles de tu Orden")
                .font(Font.uiMontB14)
                .padding(.bottom, 8)
            
            ForEach(cartItems) { item in
                OrderItemRow(item: item)
            }
            
            if cartManager.receptionMode == "DELIVERY" {
                deliveryFeeRow
            }
            
            receptionInfoBox
            
            totalRow
        }
        .padding(20)
        .background(Color.white)
        .cornerRadius(24)
        .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)
    }

    private var deliveryFeeRow: some View {
        VStack(spacing: 0) {
            HStack {
                Text("Envío")
                    .font(Font.uiMontM12)
                    .foregroundColor(.gray)
                Spacer()
                Text("$\(String(format: "%.2f", cartManager.deliveryFee))")
                    .font(Font.uiMontB14)
                    .foregroundColor(Color.uiTayGreen600)
            }
            Divider().background(Color(hex: 0xF0F2F5))
        }
    }

    private var receptionInfoBox: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(cartManager.receptionMode == "RECOJO" ? "RECOJO EN LOCAL" : "ENTREGA A DOMICILIO")
                .font(PizzaFonts.bold12)
                .foregroundColor(Color.uiTayRed600)
            
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
    }

    private var totalRow: some View {
        HStack {
            Text("Total a Pagar")
                .font(PizzaFonts.bold16)
            Spacer()
            Text("$\(String(format: "%.2f", cartManager.finalTotal))")
                .font(PizzaFonts.bold20)
                .foregroundColor(Color.uiTayRed600)
        }
    }

    private var confirmButton: some View {
        UITayButton(text: "Confirmar y Enviar Pedido") {
            cartManager.startPayment { url in
                if let paymentUrl = URL(string: url) {
                    self.paymentUrl = paymentUrl
                    destiny = .uiNext
                }
            }
        }
        .padding(.horizontal, 20)
        .padding(.bottom, 24)
    }
}


private struct OrderItemRow: View {
    let item: OrderItemSwift

    private var itemPrice: Double {
        let basePrice = Double(item.product.price) ?? 0.0
        let cheesePrice = item.cheeseFilledCrust ? (Double(item.product.priceChosse) ?? 0.0) : 0.0
        return (basePrice + cheesePrice) * Double(item.quantity)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack(alignment: .top) {
                VStack(alignment: .leading) {
                    Text("\(item.quantity)x \(item.product.nameProduct.uppercased())")
                        .font(Font.uiMontB14)
                    
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
                
                Text("$\(String(format: "%.2f", itemPrice))")
                    .font(Font.uiMontB14)
            }
            
            Divider().background(Color(hex: 0xF0F2F5))
        }
    }
}
