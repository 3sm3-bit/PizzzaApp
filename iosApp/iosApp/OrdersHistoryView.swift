import SwiftUI
import Shared

struct OrdersHistoryView: View {
    @ObservedObject var viewModel: AppViewModel
    @State private var selectedOrderForMonitoring: ParentOrderModel?
    
    var body: some View {
        VStack(spacing: 0) {
            PizzaToolbar(title: "Mis Pedidos", showBackButton: false)
            
            if viewModel.isLoading && viewModel.orders.isEmpty {
                Spacer()
                ProgressView().tint(PizzaColors.red600)
                Spacer()
            } else if viewModel.orders.isEmpty {
                Spacer()
                Text("Aún no tienes pedidos")
                    .font(PizzaFonts.medium14)
                    .foregroundColor(.gray)
                Spacer()
            } else {
                ScrollView {
                    LazyVStack(spacing: 12) {
                        ForEach(viewModel.orders, id: \.uid) { order in
                            OrderItemCard(order: order, viewModel: viewModel) {
                                selectedOrderForMonitoring = order
                            }
                        }
                    }
                    .padding()
                }
            }
        }
        .onAppear {
            viewModel.getGeneralOrderList(forceLoading: true)
        }
        .background(PizzaColors.background)
        .fullScreenCover(item: $selectedOrderForMonitoring) { order in
            MonitorView(order: order)
        }
    }
}

struct OrderItemCard: View {
    let order: ParentOrderModel
    @ObservedObject var viewModel: AppViewModel
    var onMonitor: () -> Void
    
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
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Text("Pedido #\(order.nameClient)")
                    .font(PizzaFonts.bold16)
                    .foregroundColor(.black)
                Spacer()
                Text(displayState)
                    .font(PizzaFonts.bold12)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(statusColors.bg)
                    .foregroundColor(statusColors.text)
                    .cornerRadius(8)
            }
            
            Text(order.description)
                .font(PizzaFonts.medium12)
                .foregroundColor(.gray)
            
            HStack {
                VStack(alignment: .leading) {
                    Text(order.date)
                        .font(PizzaFonts.medium12)
                        .foregroundColor(.gray)
                    Text("\(order.symbol)\(order.price)")
                        .font(PizzaFonts.bold14)
                        .foregroundColor(PizzaColors.red600)
                }
                Spacer()
                
                if order.state.uppercased() == "INICIADO" && order.reception.uppercased() == "DELIVERY" {
                    Button(action: onMonitor) {
                        Text("VER")
                            .font(PizzaFonts.bold12)
                            .foregroundColor(.white)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 8)
                            .background(PizzaColors.red600)
                            .cornerRadius(8)
                    }
                }
            }
        }
        .padding()
        .background(Color.white)
        .cornerRadius(16)
        .shadow(color: Color.black.opacity(0.05), radius: 2, x: 0, y: 1)
        .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color(hex: 0xF0F2F5), lineWidth: 1))
    }
}
