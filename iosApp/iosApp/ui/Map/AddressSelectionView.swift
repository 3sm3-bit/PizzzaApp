import SwiftUI
import TaySwitfUILibrary
import MapKit
import Contacts

struct AddressSelectionView: View {
    @Environment(\.presentationMode) var presentationMode
    var initialLat: String? = nil
    var initialLng: String? = nil
    var initialAddress: String? = nil
    var onConfirm: (String, String, String) -> Void
    
    @State private var region: MKCoordinateRegion
    @State private var address: String
    @State private var isLocating = false
    @State private var debounceTask: Task<Void, Never>? = nil

    init(initialLat: String? = nil, initialLng: String? = nil, initialAddress: String? = nil, onConfirm: @escaping (String, String, String) -> Void) {
        self.initialLat = initialLat
        self.initialLng = initialLng
        self.initialAddress = initialAddress
        self.onConfirm = onConfirm
        
        let lat = Double(initialLat ?? "") ?? 19.4326
        let lng = Double(initialLng ?? "") ?? -99.1332
        
        _region = State(initialValue: MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: lat, longitude: lng),
            span: MKCoordinateSpan(latitudeDelta: 0.002, longitudeDelta: 0.002) // Zoom muy alto para ver calles
        ))
        _address = State(initialValue: initialAddress ?? "Cargando dirección...")
    }
    
    var body: some View {
        ZStack {
            // Mapa nativo de Apple (Gratis y sin claves)
            Map(coordinateRegion: $region, interactionModes: .all, showsUserLocation: true)
                .edgesIgnoringSafeArea(.all)
            
            // Pin fijo en el centro
            Image(systemName: "mappin")
                .font(.system(size: 40))
                .foregroundColor(PizzaColors.red600)
                .offset(y: -20)
                .allowsHitTesting(false)
        }
        .overlay(alignment: .top) {
            UiTayCToolBar(uiTayText: "Seleccionar Ubicación",
                          uiTayModifier: UiToolBarModel().showStartIcon(false) ) { _ in
                presentationMode.wrappedValue.dismiss()
            }
            .background(Color.white.opacity(0.9))
        }
        .overlay(alignment: .bottom) {
            VStack(spacing: 16) {
                HStack {
                    Image(systemName: "location.fill")
                        .foregroundColor(Color.uiTayRed600)
                    Text(address)
                        .font(PizzaFonts.medium14)
                        .lineLimit(2)
                        .minimumScaleFactor(0.8)
                    Spacer()
                }
                .padding()
                .background(Color.uiTayRed50)
                .cornerRadius(12)
                
                UITayButton(text: "Confirmar Ubicación") {
                    onConfirm(
                        address,
                        String(region.center.latitude),
                        String(region.center.longitude)
                    )
                    presentationMode.wrappedValue.dismiss()
                }
            }
            .padding()
            .background(Color.white)
            .cornerRadius(20, corners: [.topLeft, .topRight])
            .shadow(radius: 10)
        }
        .onAppear {
            if initialLat == nil {
                updateAddress()
            }
        }
        .onChange(of: region.center.latitude) { _ in
            debounceAddressUpdate()
        }
        .onChange(of: region.center.longitude) { _ in
            debounceAddressUpdate()
        }
    }
    
    private func debounceAddressUpdate() {
        debounceTask?.cancel()
        debounceTask = Task {
            try? await Task.sleep(nanoseconds: 800_000_000) // 0.8 seg de espera
            if Task.isCancelled { return }
            updateAddress()
        }
    }
    
    private func updateAddress() {
        let location = CLLocation(latitude: region.center.latitude, longitude: region.center.longitude)
        CLGeocoder().reverseGeocodeLocation(location) { placemarks, error in
            guard let placemark = placemarks?.first else {
                self.address = "Ubicación seleccionada"
                return
            }
            
            // Usamos el formateador postal que es el que mejor junta "Calle + Número"
            if let postalAddress = placemark.postalAddress {
                let street = postalAddress.street // Trae "Progreso 102A"
                let neighborhood = placemark.subLocality ?? "" // Colonia
                let country = postalAddress.country // País
                
                var parts: [String] = []
                if !street.isEmpty { parts.append(street) }
                if !neighborhood.isEmpty { parts.append(neighborhood) }
                if !country.isEmpty { parts.append(country) }
                
                self.address = parts.joined(separator: ", ")
            } else {
                self.address = placemark.name ?? "Ubicación seleccionada"
            }
        }
    }
}
