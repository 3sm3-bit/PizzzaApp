import SwiftUI
import Shared

struct ContentView: View {
    @StateObject private var viewModel = OrderViewModel()
    
    var body: some View {
        NavigationView {
            ZStack {
                Color(hex: 0xF0F2F5).edgesIgnoringSafeArea(.all)
                
                VStack(spacing: 0) {
                    // Cabecera Minimalista
                    HStack {
                        Text("Gestión de Pedidos")
                            .font(.system(size: 18, weight: .bold))
                            .foregroundColor(Color(hex: 0x1C1E21))
                        
                        Spacer()
                        
                        Button(action: { viewModel.refresh() }) {
                            Image(systemName: "arrow.clockwise")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(.blue)
                                .frame(width: 36, height: 36)
                                .background(Color.white)
                                .cornerRadius(10)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 10)
                                        .stroke(Color(hex: 0xDDDFE2), lineWidth: 1)
                                )
                        }
                    }
                    .padding(.horizontal)
                    .padding(.vertical, 12)
                    
                    // Indicadores CONFIRMADO / LISTO (50% cada uno)
                    HStack(spacing: 8) {
                        StatusIndicator(text: "CONFIRMADO", count: viewModel.countConfirmado, color: Color(hex: 0x3B82F6))
                        StatusIndicator(text: "LISTO", count: viewModel.countListo, color: Color(hex: 0x10B981))
                    }
                    .padding(.horizontal)
                    .padding(.bottom, 16)
                    
                    if viewModel.isLoading {
                        ProgressView()
                            .padding(.bottom, 8)
                    }
                    
                    // Lista de Pedidos
                    ScrollView {
                        if viewModel.orders.isEmpty && !viewModel.isLoading {
                            VStack(spacing: 16) {
                                Spacer().frame(height: 100)
                                Image(systemName: "info.circle")
                                    .font(.system(size: 64))
                                    .foregroundColor(.gray)
                                Text("No se encontraron pedidos")
                                    .font(.system(size: 16, weight: .medium))
                                    .foregroundColor(.gray)
                                Button("Intentar de nuevo") {
                                    viewModel.refresh()
                                }
                                .padding(.horizontal, 20)
                                .padding(.vertical, 10)
                                .background(Color.blue)
                                .foregroundColor(.white)
                                .cornerRadius(8)
                            }
                        } else {
                            LazyVStack(spacing: 8) {
                                ForEach(viewModel.orders, id: \.uid) { order in
                                    OrderCardView(
                                        order: order,
                                        onDetail: { viewModel.selectedOrder = order },
                                        onAvanzar: { viewModel.avanzarEstado(order: order) }
                                    )
                                }
                            }
                            .padding(.horizontal)
                            .padding(.bottom, 12)
                        }
                    }
                }
                
                // Botón Flotante para Productos (Navegación)
                VStack {
                    Spacer()
                    HStack {
                        Spacer()
                        NavigationLink(destination: ProductListView(viewModel: viewModel)
                                        .onAppear { viewModel.getProductsList() }) {
                            Image(systemName: "list.bullet.rectangle")
                                .font(.system(size: 20, weight: .bold))
                                .foregroundColor(.white)
                                .frame(width: 48, height: 48)
                                .background(Color(hex: 0x007BFF))
                                .clipShape(Circle())
                                .shadow(radius: 4)
                        }
                        .padding(.trailing, 20)
                        .padding(.bottom, 20)
                    }
                }
            }
            .navigationBarHidden(true)
            .sheet(item: $viewModel.selectedOrder) { order in
                OrderDetailView(order: order)
                    .presentationDetents([.medium, .large])
                    .presentationDragIndicator(.visible)
            }
        }
    }
}

// Identificable para el .sheet
extension ParentOrderModel: Identifiable {
    public var id: String { uid }
}

struct StatusIndicator: View {
    let text: String
    let count: Int
    let color: Color
    
    var body: some View {
        HStack {
            Text(text)
                .font(.system(size: 10, weight: .bold))
                .foregroundColor(.white.opacity(0.9))
            Spacer()
            Text("\(count)")
                .font(.system(size: 12, weight: .bold))
                .foregroundColor(.white)
                .padding(.horizontal, 8)
                .padding(.vertical, 2)
                .background(Color.white.opacity(0.2))
                .cornerRadius(6)
        }
        .padding(.horizontal, 8)
        .frame(height: 40)
        .frame(maxWidth: .infinity)
        .background(color)
        .cornerRadius(12)
    }
}

struct OrderCardView: View {
    let order: ParentOrderModel
    let onDetail: () -> Void
    let onAvanzar: () -> Void
    
    var statusColor: Color {
        order.state.uppercased().contains("CONFIRMADO") ? Color(hex: 0x3B82F6) : Color(hex: 0x10B981)
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(spacing: 0) {
                Rectangle()
                    .fill(statusColor)
                    .frame(width: 4)
                
                VStack(alignment: .leading, spacing: 0) {
                    // Fila 1: Cliente y Estado
                    HStack {
                        Text(order.nameClient)
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(Color(hex: 0x1C1E21))
                        Spacer()
                        Text(order.state.uppercased())
                            .font(.system(size: 10, weight: .bold))
                            .padding(.horizontal, 6)
                            .padding(.vertical, 2)
                            .background(statusColor.opacity(0.1))
                            .foregroundColor(statusColor)
                            .cornerRadius(4)
                    }
                    .padding(.top, 12)
                    
                    Divider().padding(.vertical, 8)
                    
                    // Desglose de Productos
                    VStack(alignment: .leading, spacing: 2) {
                        ForEach(order.orders, id: \.ui) { item in
                            VStack(alignment: .leading, spacing: 0) {
                                HStack {
                                    Text("\(item.quantity) \(item.nameProduct) \(item.typeDough)")
                                        .font(.system(size: 14, weight: .medium))
                                        .foregroundColor(Color(hex: 0x1C1E21))
                                    Spacer()
                                    let subtotal = (Double(item.quantity) ?? 0) * (Double(item.price) ?? 0)
                                    Text("$\(Int(subtotal))")
                                        .font(.system(size: 14, weight: .bold))
                                }
                                
                                if item.cheeseFilledCrust.uppercased() == "SI" {
                                    HStack {
                                        Text("con orilla de queso")
                                            .font(.system(size: 12))
                                            .foregroundColor(Color(hex: 0x65676B))
                                        Spacer()
                                        Text("$\(item.priceChosse)")
                                            .font(.system(size: 12))
                                            .foregroundColor(Color(hex: 0x65676B))
                                    }
                                    .padding(.leading, 12)
                                }
                                
                                if !item.note.isEmpty {
                                    Text("Nota: \(item.note)")
                                        .font(.system(size: 10))
                                        .foregroundColor(Color(hex: 0x8A8D91))
                                        .padding(.leading, 12)
                                        .padding(.top, 2)
                                }
                            }
                        }
                    }
                    
                    Divider().padding(.top, 2).padding(.bottom, 8)
                    
                    // Logística y Total
                    HStack(alignment: .bottom) {
                        VStack(alignment: .leading, spacing: 2) {
                            let isDelivery = order.reception.uppercased().contains("DELIVERY")
                            Text(isDelivery ? "🏠 DELIVERY" : "🛍️ RECOJO EN LOCAL")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(isDelivery ? Color(hex: 0xE91E63) : Color(hex: 0x007BFF))
                            
                            if isDelivery && !order.address.isEmpty && order.address.lowercased() != "null" {
                                Text(order.address)
                                    .font(.system(size: 10))
                                    .foregroundColor(Color(hex: 0x65676B))
                                    .lineLimit(2)
                            }
                        }
                        
                        Spacer()
                        
                        VStack(alignment: .trailing, spacing: 0) {
                            Text("TOTAL").font(.system(size: 10, weight: .bold)).foregroundColor(Color(hex: 0x8A8D91))
                            Text("$\(order.price)")
                                .font(.system(size: 20, weight: .bold))
                                .foregroundColor(Color(hex: 0x10B981))
                        }
                    }
                    
                    Spacer().frame(height: 12)
                    
                    // Botones
                    HStack(spacing: 8) {
                        Button(action: onDetail) {
                            Text("DETALLE")
                                .font(.system(size: 12, weight: .bold))
                                .foregroundColor(Color(hex: 0x1C1E21))
                                .frame(maxWidth: .infinity)
                                .frame(height: 32)
                                .background(Color(hex: 0xF0F2F5))
                                .cornerRadius(6)
                        }
                        
                        if order.state.uppercased().trimmingCharacters(in: .whitespacesAndNewlines) != "LISTO" {
                            Button(action: onAvanzar) {
                                Text("AVANZAR")
                                    .font(.system(size: 12, weight: .bold))
                                    .foregroundColor(.white)
                                    .frame(maxWidth: .infinity)
                                    .frame(height: 32)
                                    .background(statusColor)
                                    .cornerRadius(6)
                            }
                        }
                    }
                    .padding(.bottom, 12)
                }
                .padding(.horizontal, 12)
            }
        }
        .background(Color.white)
        .cornerRadius(10)
        .shadow(color: Color.black.opacity(0.05), radius: 2, x: 0, y: 1)
    }
}

struct OrderDetailView: View {
    let order: ParentOrderModel
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        ZStack {
            Color(hex: 0xF0F2F5).edgesIgnoringSafeArea(.all)
            
            VStack(alignment: .leading, spacing: 16) {
                // Header
                HStack {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Detalle del Pedido")
                            .font(.system(size: 18, weight: .bold))
                        Text("Cliente: \(order.nameClient)")
                            .font(.system(size: 14, weight: .medium))
                            .foregroundColor(.blue)
                    }
                    Spacer()
                    Button(action: { presentationMode.wrappedValue.dismiss() }) {
                        Image(systemName: "xmark.circle.fill")
                            .font(.system(size: 24))
                            .foregroundColor(.gray)
                    }
                }
                .padding(.top, 20)
                
                let isDelivery = order.reception.uppercased().contains("DELIVERY")
                HStack {
                    Image(systemName: isDelivery ? "house.fill" : "cart.fill")
                        .font(.system(size: 16))
                        .foregroundColor(isDelivery ? Color(hex: 0xE91E63) : .blue)
                    Text(isDelivery ? "Envío a: \(order.address)" : "Recojo en local")
                        .font(.system(size: 14))
                        .foregroundColor(Color(hex: 0x65676B))
                }
                
                ScrollView {
                    VStack(spacing: 10) {
                        ForEach(order.orders, id: \.ui) { item in
                            HStack(spacing: 16) {
                                Text("x\(item.quantity)")
                                    .font(.system(size: 16, weight: .bold))
                                    .frame(width: 40, height: 40)
                                    .background(Color(hex: 0xF0F2F5))
                                    .cornerRadius(8)
                                
                                VStack(alignment: .leading, spacing: 2) {
                                    Text(item.nameProduct)
                                        .font(.system(size: 16, weight: .bold))
                                    Text("\(item.tamanio) • \(item.typeDough)")
                                        .font(.system(size: 12))
                                        .foregroundColor(Color(hex: 0x65676B))
                                    if item.cheeseFilledCrust.uppercased() == "SI" {
                                        Text("🧀 Con orilla de queso")
                                            .font(.system(size: 10, weight: .bold))
                                            .foregroundColor(Color(hex: 0x10B981))
                                    }
                                }
                                Spacer()
                                Text("$\(Int(Double(item.price) ?? 0))")
                                    .font(.system(size: 16, weight: .bold))
                            }
                            .padding(16)
                            .background(Color.white)
                            .cornerRadius(12)
                        }
                    }
                }
                
                // Total
                HStack {
                    Text("Total del Pedido")
                        .font(.system(size: 16, weight: .bold))
                    Spacer()
                    Text("$\(order.price)")
                        .font(.system(size: 20, weight: .bold))
                        .foregroundColor(Color(hex: 0x10B981))
                }
                .padding(16)
                .background(Color.white)
                .cornerRadius(12)
                .padding(.bottom, 32)
            }
            .padding(.horizontal, 16)
        }
    }
}

struct ProductListView: View {
    @ObservedObject var viewModel: OrderViewModel
    
    var body: some View {
        ZStack {
            Color(hex: 0xF0F2F5).edgesIgnoringSafeArea(.all)
            
            VStack(alignment: .leading, spacing: 16) {
                ScrollView {
                    VStack(spacing: 10) {
                        ForEach(viewModel.products, id: \.uid) { product in
                            VStack(alignment: .leading, spacing: 8) {
                                HStack {
                                    Text(product.nameProduct)
                                        .font(.system(size: 16, weight: .bold))
                                        .foregroundColor(Color(hex: 0x1C1E21))
                                    Spacer()
                                    Text("\(product.currencySymbol)\(product.price)")
                                        .font(.system(size: 18, weight: .bold))
                                        .foregroundColor(Color(hex: 0x10B981))
                                }
                                
                                Text(product.description)
                                    .font(.system(size: 12))
                                    .foregroundColor(Color(hex: 0x65676B))
                                
                                if product.type == "1" {
                                    HStack(spacing: 16) {
                                        Text(product.tamanio)
                                            .font(.system(size: 10, weight: .bold))
                                            .padding(.horizontal, 8)
                                            .padding(.vertical, 4)
                                            .background(Color(hex: 0xF0F2F5))
                                            .cornerRadius(8)
                                            .foregroundColor(Color(hex: 0x65676B))
                                        
                                        if !product.priceChosse.isEmpty {
                                            Text("🧀 Orilla: \(product.currencySymbol)\(product.priceChosse)")
                                                .font(.system(size: 10, weight: .bold))
                                                .foregroundColor(Color(hex: 0x007BFF))
                                        }
                                    }
                                }
                            }
                            .padding(16)
                            .background(Color.white)
                            .cornerRadius(12)
                        }
                    }
                    .padding(.top, 16)
                }
            }
            .padding(.horizontal, 16)
        }
        .navigationTitle("Nuestros Productos")
        .navigationBarTitleDisplayMode(.large)
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
