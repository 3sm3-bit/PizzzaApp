import SwiftUI
import Shared
import TaySwitfUILibrary

struct OrderHistoryView: View {
    @StateObject private var viewModel = ClientOrderViewModel()
    @Environment(\.dismiss) var dismiss

    var body: some View {
        BaseViewGeneral {
            VStack(spacing: 0) {
                UiTayCToolBar(
                    uiTayText: "Historial de Pedidos",
                    uiTayModifier: UiToolBarModel()
                        .backgroundColor(.white)
                        .textColor(Color.uiTayRed600)
                        .iconColor(Color.uiTayRed600)
                ) { _ in
                    dismiss()
                }

                if viewModel.isLoading {
                    Spacer()
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: Color.uiTayRed600))
                    Spacer()
                } else if viewModel.orders.isEmpty {
                    Spacer()
                    VStack(spacing: 16) {
                        Image(systemName: "clock")
                            .font(.system(size: 64))
                            .foregroundColor(.gray)
                        Text("No tienes pedidos en tu historial")
                            .font(PizzaFonts.medium14)
                            .foregroundColor(.gray)
                    }
                    Spacer()
                } else {
                    ScrollView {
                        LazyVStack(spacing: 12) {
                            ForEach(viewModel.orders, id: \.uid) { order in
                                OrderHistoryCardView(order: order)
                            }
                        }
                        .padding()
                    }
                }
            }
        }
        .onAppear {
            viewModel.fetchOrders()
        }
        .background(PizzaColors.background)
    }
}

struct OrderHistoryCardView: View {
    let order: ParentOrderModel

    var displayState: String {
        switch order.state.uppercased() {
        case "CONFIRMADO": return "PREPARANDO"
        case "LISTO", "ENVIADO": return "LISTO"
        case "INICIADO": return "EN CAMINO"
        case "ENTREGADO": return "ENTREGADO"
        default: return order.state.uppercased()
        }
    }

    var statusColors: (bg: Color, text: Color) {
        switch order.state.uppercased() {
        case "CONFIRMADO": return (Color(hex: 0xFFF3E0), Color(hex: 0xE65100))
        case "LISTO", "ENVIADO": return (Color(hex: 0xE8F5E9), Color(hex: 0x2E7D32))
        case "INICIADO": return (Color(hex: 0xE3F2FD), Color(hex: 0x1565C0))
        case "ENTREGADO": return (Color(hex: 0xE8F5E9), Color(hex: 0x2E7D32))
        default: return (Color(hex: 0xF5F5F5), .gray)
        }
    }

    var descriptionText: String {
        if !order.description_.isEmpty {
            return order.description_
        } else {
            return order.orders.map { "\($0.quantity)x \($0.nameProduct)" }.joined(separator: ", ")
        }
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            // Estado y Fecha
            HStack {
                Text(displayState)
                    .font(PizzaFonts.bold12)
                    .padding(.horizontal, 10)
                    .padding(.vertical, 4)
                    .background(statusColors.bg)
                    .foregroundColor(statusColors.text)
                    .cornerRadius(8)

                Spacer()

                Text(order.date)
                    .font(PizzaFonts.medium12)
                    .foregroundColor(.gray)
            }

            // Descripción del pedido
            if !descriptionText.isEmpty {
                Text(descriptionText)
                    .font(PizzaFonts.medium12)
                    .foregroundColor(.black)
            }

            // Total / Precio
            HStack {
                Text("Total:")
                    .font(PizzaFonts.medium12)
                    .foregroundColor(.gray)
                Spacer()
                Text("\(order.symbol)\(order.price)")
                    .font(PizzaFonts.bold18)
                    .foregroundColor(PizzaColors.red600)
            }
        }
        .padding(16)
        .background(Color.white)
        .cornerRadius(16)
        .shadow(color: Color.black.opacity(0.08), radius: 4, x: 0, y: 2)
    }
}
