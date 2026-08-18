import SwiftUI
import Shared

struct ContentView: View {
    @StateObject private var viewModel = OrderViewModel()
    
    var body: some View {
        NavigationView {
            ZStack {
                Color(hex: 0xF0F2F5).edgesIgnoringSafeArea(.all)
                
                VStack(spacing: 16) {
                    // Cabecera con Filtro y Refresh
                    HStack(spacing: 8) {
                        FilterHeader(
                            selectedFilter: viewModel.selectedFilter,
                            onFilterSelected: { viewModel.updateFilter(filter: $0) }
                        )
                        .frame(maxWidth: .infinity)
                        
                        Button(action: { viewModel.refresh() }) {
                            Image(systemName: "arrow.clockwise")
                                .font(.system(size: 20, weight: .bold))
                                .foregroundColor(.blue)
                                .frame(width: 48, height: 48)
                                .background(Color.white)
                                .cornerRadius(12)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 12)
                                        .stroke(Color(hex: 0xDDDFE2), lineWidth: 1)
                                )
                        }
                    }
                    .padding(.horizontal)
                    .padding(.top, 8)
                    
                    if viewModel.isLoading {
                        ProgressView()
                            .progressViewStyle(LinearProgressViewStyle(tint: .blue))
                            .padding(.horizontal)
                    }
                    
                    // Lista de Pedidos
                    ScrollView {
                        LazyVStack(spacing: 8) {
                            ForEach(viewModel.filteredOrders, id: \.uid) { order in
                                OrderCardView(order: order)
                            }
                        }
                        .padding(.horizontal)
                        .padding(.bottom, 12)
                    }
                }
                
                // Botón Flotante de Prueba de Voz (Solo para desarrollo)
                VStack {
                    Spacer()
                    HStack {
                        Spacer()
                        Button(action: {
                            VoiceManager.shared.speak(text: "Llegó un pedido de DELIVERY para Manuel")
                        }) {
                            Image(systemName: "megaphone.fill")
                                .font(.system(size: 24))
                                .foregroundColor(.white)
                                .frame(width: 60, height: 60)
                                .background(Color.orange)
                                .clipShape(Circle())
                                .shadow(radius: 4)
                        }
                        .padding(.trailing, 20)
                        .padding(.bottom, 20)
                    }
                }
            }
            .navigationTitle("Pedidos Pizzza")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

struct FilterHeader: View {
    let selectedFilter: String
    let onFilterSelected: (String) -> Void
    
    let filters = ["TODOS", "PENDIENTE", "CONFIRMADO", "PREPARANDO"]
    
    var body: some View {
        Menu {
            ForEach(filters, id: \.self) { filter in
                Button(filter) {
                    onFilterSelected(filter)
                }
            }
        } label: {
            HStack {
                Text("Estado: \(selectedFilter)")
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(Color(hex: 0x1C1E21))
                Spacer()
                Image(systemName: "chevron.down")
                    .foregroundColor(Color(hex: 0x65676B))
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            .background(Color.white)
            .cornerRadius(12)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(Color(hex: 0xDDDFE2), lineWidth: 1)
            )
        }
    }
}

struct OrderCardView: View {
    let order: ParentOrderModel
    
    var statusColor: Color {
        switch order.state.uppercased() {
        case "PENDIENTE": return Color(hex: 0xF59E0B)
        case "CONFIRMADO": return Color(hex: 0x3B82F6)
        case "PREPARANDO": return Color(hex: 0x10B981)
        default: return Color(hex: 0x6B7280)
        }
    }
    
    var body: some View {
        HStack(spacing: 0) {
            // Indicador lateral minimalista
            Rectangle()
                .fill(statusColor)
                .frame(width: 3)
            
            VStack(alignment: .leading, spacing: 3) {
                // Fila 1: ID, Cliente y Estado
                HStack(spacing: 4) {
                    Text("#\(String(order.uid.suffix(4)))")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(Color(hex: 0x65676B))
                    
                    Text(order.nameClient)
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(Color(hex: 0x1C1E21))
                        .lineLimit(1)
                    
                    Spacer()
                    
                    Text(order.state.uppercased())
                        .font(.system(size: 9, weight: .bold))
                        .padding(.horizontal, 5)
                        .padding(.vertical, 1)
                        .background(statusColor.opacity(0.1))
                        .foregroundColor(statusColor)
                        .cornerRadius(3)
                }
                
                // Fila 2: Nota y Precio
                HStack(spacing: 4) {
                    Image(systemName: "pencil")
                        .font(.system(size: 10))
                        .foregroundColor(Color(hex: 0x8A8D91))
                    
                    Text(order.description)
                        .font(.system(size: 11))
                        .foregroundColor(Color(hex: 0x65676B))
                        .lineLimit(3)
                    
                    Spacer()
                    
                    Text("$\(order.price)")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(Color(hex: 0x10B981))
                }
                
                // Fila 3: Botones Compactos
                HStack(spacing: 6) {
                    Button(action: {}) {
                        Text("Detalles")
                            .font(.system(size: 10, weight: .semibold))
                            .foregroundColor(Color(hex: 0x1C1E21))
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 2)
                            .background(Color(hex: 0xF0F2F5))
                            .cornerRadius(4)
                    }
                    
                    Button(action: {}) {
                        Text("Avanzar")
                            .font(.system(size: 10, weight: .semibold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 2)
                            .background(Color(hex: 0x007BFF))
                            .cornerRadius(4)
                    }
                }
            }
            .padding(.vertical, 5)
            .padding(.horizontal, 8)
        }
        .background(Color.white)
        .cornerRadius(8)
        .shadow(color: Color.black.opacity(0.04), radius: 2, x: 0, y: 1)
    }
}

// Extensión para colores Hex en SwiftUI
extension Color {
    init(hex: UInt, alpha: Double = 1) {
        self.init(
            .sRGB,
            red: Double((hex >> 16) & 0xff) / 255,
            green: Double((hex >> 8) & 0xff) / 255,
            blue: Double(hex & 0xff) / 255,
            opacity: alpha
        )
    }
}
