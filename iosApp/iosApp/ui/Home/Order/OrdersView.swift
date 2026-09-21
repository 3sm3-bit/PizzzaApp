//
//  OrdersView.swift
//  iosApp
//
//  Created by Developer on 12/09/26.
//

import SwiftUI
import Shared

struct OrdersView: View {
    @ObservedObject var viewModel: HomeViewModel
    var onLogout: () -> Void
    @State private var selectedOrderForMonitoring: ParentOrderModel?
    
    var body: some View {
        VStack(spacing: 0) {            
            UiToolBarHome(typeFlow: true,visibleCart: false,typeRefresh: true){
                viewModel.getGeneralOrderList(forceLoading: true)
            }
            
            if viewModel.orders.isEmpty {
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
            // Ya no forzamos el loading (forceLoading: false) para evitar el bloqueo al navegar
            viewModel.getGeneralOrderList(forceLoading: false)
        }
        .background(PizzaColors.background)
        .fullScreenCover(item: $selectedOrderForMonitoring) { order in
            MonitorView(order: order)
        }
    }
}

struct OrderItemCard: View {
    let order: ParentOrderModel
    @ObservedObject var viewModel: HomeViewModel
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
            // Cabecera: ID Pedido y Estado
            HStack {
                Text("Pedido #\(order.nameClient)")
                    .font(Font.uiMontB18)
                    .foregroundColor(.black)
                Spacer()
                Text(displayState)
                    .font(PizzaFonts.bold12)
                    .padding(.horizontal, 10)
                    .padding(.vertical, 4)
                    .background(statusColors.bg)
                    .foregroundColor(statusColors.text)
                    .cornerRadius(12)
            }
            
            // Lista de Productos
            VStack(alignment: .leading, spacing: 6) {
                ForEach(order.orders, id: \.ui) { item in
                    HStack {
                        let sizeText = item.type == "1" ? "(\(item.tamanio)) - " : ""
                        let doughText = item.type == "1" ? "\(item.typeDough)" : ""
                        
                        Text("\(item.quantity)x \(item.nameProduct) \(sizeText)\(doughText)")
                            .font(PizzaFonts.medium12)
                            .foregroundColor(.black)
                        
                        Spacer()
                        
                        Text("\(item.symbol)\(item.priceTotal)")
                            .font(PizzaFonts.bold14)
                            .foregroundColor(.black)
                    }
                }
            }
            
            // Costo de Envío (Si existe)
            if let deliveryPrice = Double(order.orders.first?.priceDelivery ?? "0"), deliveryPrice > 0 {
                HStack {
                    Text("Costo de Envío")
                        .font(PizzaFonts.medium12)
                        .foregroundColor(Color.uiTayGreen600)
                    Spacer()
                    Text("\(order.symbol)\(Int(deliveryPrice))")
                        .font(PizzaFonts.bold14)
                        .foregroundColor(Color.uiTayGreen600)
                }
            }
            
            Divider()
            
            // Pie de tarjeta: Modo, Fecha y Total
            HStack(alignment: .bottom) {
                VStack(alignment: .leading, spacing: 2) {
                    Text(order.reception.uppercased())
                        .font(PizzaFonts.bold12)
                        .foregroundColor(Color.uiTayGreen600)
                    Text(order.date)
                        .font(PizzaFonts.medium10)
                        .foregroundColor(.gray)
                }
                
                Spacer()
                
                VStack(alignment: .trailing, spacing: 0) {
                    Text("TOTAL")
                        .font(PizzaFonts.medium10)
                        .foregroundColor(.gray)
                    Text("\(order.symbol)\(order.price)")
                        .font(PizzaFonts.bold20)
                        .foregroundColor(PizzaColors.red600)
                }
            }
            
            // Botón de Seguimiento si está en camino
            if order.state.uppercased() == "INICIADO" && order.reception.uppercased() == "DELIVERY" {
                Button(action: onMonitor) {
                    Text("VER UBICACIÓN EN VIVO")
                        .font(PizzaFonts.bold12)
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 36)
                        .background(PizzaColors.red600)
                        .cornerRadius(8)
                }
                .padding(.top, 4)
            }
        }
        .padding(16)
        .background(Color.white)
        .cornerRadius(20)
        .shadow(color: Color.black.opacity(0.08), radius: 6, x: 0, y: 3)
    }
}
