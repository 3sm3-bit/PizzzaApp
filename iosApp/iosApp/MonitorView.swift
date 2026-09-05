import SwiftUI
import MapKit
import Shared

struct MonitorView: View {
    let order: ParentOrderModel
    @Environment(\.presentationMode) var presentationMode
    @StateObject private var viewModel = MonitorViewModel()
    
    // Map state
    @State private var region = MKCoordinateRegion()
    @State private var routeOverlay: [CLLocationCoordinate2D] = []
    
    var body: some View {
        ZStack {
            Map(coordinateRegion: $region, annotationItems: viewModel.markers) { marker in
                MapAnnotation(coordinate: marker.coordinate) {
                    VStack {
                        Image(systemName: marker.type == .house ? "house.fill" : "bicycle")
                            .resizable()
                            .frame(width: 30, height: 30)
                            .foregroundColor(marker.type == .house ? PizzaColors.green600 : PizzaColors.red600)
                            .padding(8)
                            .background(Color.white)
                            .clipShape(Circle())
                            .shadow(radius: 4)
                        
                        Text(marker.title)
                            .font(PizzaFonts.medium10)
                            .padding(4)
                            .background(Color.white.opacity(0.8))
                            .cornerRadius(4)
                    }
                }
            }
            .edgesIgnoringSafeArea(.all)
            
            // Overlays
            VStack {
                HStack {
                    Button(action: { presentationMode.wrappedValue.dismiss() }) {
                        Image(systemName: "arrow.left")
                            .font(.system(size: 20, weight: .bold))
                            .foregroundColor(PizzaColors.red600)
                            .padding()
                            .background(Color.white)
                            .clipShape(Circle())
                            .shadow(radius: 4)
                    }
                    .padding()
                    Spacer()
                }
                
                Spacer()
                
                // Info Card
                VStack(alignment: .leading, spacing: 8) {
                    HStack {
                        Text("Estado: EN CAMINO")
                            .font(PizzaFonts.bold16)
                            .foregroundColor(PizzaColors.red600)
                        Spacer()
                    }
                    Text("Tu repartidor está acercándose a tu ubicación.")
                        .font(PizzaFonts.medium14)
                        .foregroundColor(.gray)
                }
                .padding()
                .background(Color.white)
                .cornerRadius(16)
                .shadow(radius: 8)
                .padding()
            }
        }
        .onAppear {
            viewModel.setup(order: order)
            updateMap(order: order)
        }
        .onReceive(viewModel.$currentOrder) { newOrder in
            if let o = newOrder {
                updateMap(order: o)
            }
        }
    }
    
    private func updateMap(order: ParentOrderModel) {
        let delivery = CLLocationCoordinate2D(
            latitude: Double(order.latitude) ?? 0.0,
            longitude: Double(order.longitude) ?? 0.0
        )
        let driver = CLLocationCoordinate2D(
            latitude: Double(order.currentLatitude) ?? 0.0,
            longitude: Double(order.currentLongitude) ?? 0.0
        )
        
        // Center map to show both
        let centerLat = (delivery.latitude + driver.latitude) / 2
        let centerLng = (delivery.longitude + driver.longitude) / 2
        let spanLat = abs(delivery.latitude - driver.latitude) * 2.5
        let spanLng = abs(delivery.longitude - driver.longitude) * 2.5
        
        region = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: centerLat, longitude: centerLng),
            span: MKCoordinateSpan(latitudeDelta: max(spanLat, 0.01), longitudeDelta: max(spanLng, 0.01))
        )
    }
}

class MonitorViewModel: ObservableObject {
    @Published var currentOrder: ParentOrderModel?
    @Published var markers: [MapMarker] = []
    private let dataUseCase = KoinHelper.shared.getDataUseCase()
    private var timer: Timer?
    
    func setup(order: ParentOrderModel) {
        self.currentOrder = order
        updateMarkers(order: order)
        
        // Polling every 1 minute as in Android
        timer = Timer.scheduledTimer(withTimeInterval: 60, repeats: true) { _ in
            self.refresh()
        }
    }
    
    func refresh() {
        guard let order = currentOrder else { return }
        dataUseCase.getOrderById(orderId: order.uid) { [weak self] updatedOrder, error in
            DispatchQueue.main.async {
                if let o = updatedOrder {
                    self?.currentOrder = o
                    self?.updateMarkers(order: o)
                }
            }
        }
    }
    
    private func updateMarkers(order: ParentOrderModel) {
        let delivery = MapMarker(
            title: "Entrega",
            coordinate: CLLocationCoordinate2D(latitude: Double(order.latitude) ?? 0.0, longitude: Double(order.longitude) ?? 0.0),
            type: .house
        )
        let driver = MapMarker(
            title: "Repartidor",
            coordinate: CLLocationCoordinate2D(latitude: Double(order.currentLatitude) ?? 0.0, longitude: Double(order.currentLongitude) ?? 0.0),
            type: .driver
        )
        markers = [delivery, driver]
    }
    
    deinit {
        timer?.invalidate()
    }
}

struct MapMarker: Identifiable {
    let id = UUID()
    let title: String
    let coordinate: CLLocationCoordinate2D
    let type: MarkerType
    
    enum MarkerType {
        case house, driver
    }
}
