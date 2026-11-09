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
            MapViewRepresentable(
                region: region,
                markers: viewModel.markers,
                pathCoordinates: routeOverlay
            )
            .edgesIgnoringSafeArea(.all)
            
            // Overlays
            VStack {
                HStack {
                    Button(action: { presentationMode.wrappedValue.dismiss() }) {
                        Image(systemName: "arrow.left")
                            .font(.system(size: 20, weight: .bold))
                            .foregroundColor(Color.uiTayRed600)
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
                            .foregroundColor(Color.uiTayRed600)
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
        
        // Generate curved path matching Android
        routeOverlay = generateCurvedPath(start: driver, end: delivery)
        
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
    
    private func generateCurvedPath(start: CLLocationCoordinate2D, end: CLLocationCoordinate2D) -> [CLLocationCoordinate2D] {
        var points: [CLLocationCoordinate2D] = []
        let count = 50
        
        let midLat = (start.latitude + end.latitude) / 2
        let midLng = (start.longitude + end.longitude) / 2

        let distLat = end.latitude - start.latitude
        let distLng = end.longitude - start.longitude

        let offset = 0.2
        let controlPoint = CLLocationCoordinate2D(
            latitude: midLat + (distLng * offset),
            longitude: midLng - (distLat * offset)
        )

        for i in 0...count {
            let t = Double(i) / Double(count)
            let lat = ((1 - t) * (1 - t) * start.latitude) + (2 * (1 - t) * t * controlPoint.latitude) + (t * t * end.latitude)
            let lng = ((1 - t) * (1 - t) * start.longitude) + (2 * (1 - t) * t * controlPoint.longitude) + (t * t * end.longitude)
            points.append(CLLocationCoordinate2D(latitude: lat, longitude: lng))
        }
        
        return points
    }
}

struct MapViewRepresentable: UIViewRepresentable {
    let region: MKCoordinateRegion
    let markers: [MapMarker]
    let pathCoordinates: [CLLocationCoordinate2D]
    
    func makeUIView(context: Context) -> MKMapView {
        let mapView = MKMapView()
        mapView.delegate = context.coordinator
        return mapView
    }
    
    func updateUIView(_ uiView: MKMapView, context: Context) {
        uiView.setRegion(region, animated: true)
        
        uiView.removeAnnotations(uiView.annotations)
        for marker in markers {
            let annotation = CustomAnnotation(marker: marker)
            uiView.addAnnotation(annotation)
        }
        
        uiView.removeOverlays(uiView.overlays)
        if !pathCoordinates.isEmpty {
            let polyline = MKPolyline(coordinates: pathCoordinates, count: pathCoordinates.count)
            uiView.addOverlay(polyline)
        }
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator()
    }
    
    class Coordinator: NSObject, MKMapViewDelegate {
        func mapView(_ mapView: MKMapView, rendererFor overlay: MKOverlay) -> MKOverlayRenderer {
            if let polyline = overlay as? MKPolyline {
                let renderer = MKPolylineRenderer(polyline: polyline)
                renderer.strokeColor = UIColor(red: 22/255, green: 101/255, blue: 52/255, alpha: 1.0) // tay_green_800
                renderer.lineWidth = 4.0
                return renderer
            }
            return MKOverlayRenderer(overlay: overlay)
        }
        
        func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
            guard let customAnnotation = annotation as? CustomAnnotation else { return nil }
            
            let identifier = "CustomAnnotation"
            var annotationView = mapView.dequeueReusableAnnotationView(withIdentifier: identifier) as? MKMarkerAnnotationView
            
            if annotationView == nil {
                annotationView = MKMarkerAnnotationView(annotation: customAnnotation, reuseIdentifier: identifier)
                annotationView?.canShowCallout = true
            } else {
                annotationView?.annotation = customAnnotation
            }
            
            if customAnnotation.marker.type == .house {
                annotationView?.markerTintColor = .systemGreen
                annotationView?.glyphImage = UIImage(systemName: "house.fill")
            } else {
                annotationView?.markerTintColor = .systemRed
                annotationView?.glyphImage = UIImage(named: "ic_map_boy")
            }
            
            return annotationView
        }
    }
}

class CustomAnnotation: NSObject, MKAnnotation {
    let marker: MapMarker
    var coordinate: CLLocationCoordinate2D { marker.coordinate }
    var title: String? { marker.title }
    
    init(marker: MapMarker) {
        self.marker = marker
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
        
        // Polling every 20 seconds
        timer = Timer.scheduledTimer(withTimeInterval: 20, repeats: true) { _ in
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
