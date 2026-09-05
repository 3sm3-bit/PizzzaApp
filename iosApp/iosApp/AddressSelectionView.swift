import SwiftUI
import MapKit

struct AddressSelectionView: View {
    @Environment(\.presentationMode) var presentationMode
    var onConfirm: (String, String, String) -> Void
    
    @State private var region = MKCoordinateRegion(
        center: CLLocationCoordinate2D(latitude: 19.4326, longitude: -99.1332), // Default CDMX or similar
        span: MKCoordinateSpan(latitudeDelta: 0.05, longitudeDelta: 0.05)
    )
    
    @State private var address: String = "Cargando dirección..."
    @State private var isLocating = false
    
    var body: some View {
        ZStack {
            Map(coordinateRegion: $region, interactionModes: .all, showsUserLocation: true)
                .edgesIgnoringSafeArea(.all)
            
            // Center Pin
            Image(systemName: "mappin")
                .font(.system(size: 40))
                .foregroundColor(PizzaColors.red600)
                .offset(y: -20)
            
            VStack {
                PizzaToolbar(title: "Seleccionar Dirección", onBack: {
                    presentationMode.wrappedValue.dismiss()
                })
                
                Spacer()
                
                // Bottom Card
                VStack(spacing: 16) {
                    HStack {
                        Image(systemName: "location.fill")
                            .foregroundColor(PizzaColors.red600)
                        Text(address)
                            .font(PizzaFonts.medium14)
                            .lineLimit(2)
                        Spacer()
                    }
                    .padding()
                    .background(PizzaColors.red50)
                    .cornerRadius(12)
                    
                    PizzaButton(title: "Confirmar Ubicación", isEnabled: !isLocating) {
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
        }
        .onAppear {
            updateAddress()
        }
        .onChange(of: region.center.latitude) { _ in
            debounceAddressUpdate()
        }
    }
    
    private func debounceAddressUpdate() {
        isLocating = true
        // In a real app, use a proper debounce
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
            updateAddress()
        }
    }
    
    private func updateAddress() {
        let location = CLLocation(latitude: region.center.latitude, longitude: region.center.longitude)
        CLGeocoder().reverseGeocodeLocation(location) { placemarks, error in
            isLocating = false
            if let placemark = placemarks?.first {
                let street = placemark.thoroughfare ?? ""
                let number = placemark.subThoroughfare ?? ""
                let city = placemark.locality ?? ""
                self.address = "\(street) \(number), \(city)".trimmingCharacters(in: .whitespacesAndNewlines)
                if self.address.isEmpty || self.address == "," {
                    self.address = "Ubicación desconocida"
                }
            }
        }
    }
}

// Helper for rounded corners
extension View {
    func cornerRadius(_ radius: CGFloat, corners: UIRectCorner) -> some View {
        clipShape( RoundedCorner(radius: radius, corners: corners) )
    }
}

struct RoundedCorner: Shape {
    var radius: CGFloat = .infinity
    var corners: UIRectCorner = .allCorners

    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(roundedRect: rect, byRoundingCorners: corners, cornerRadii: CGSize(width: radius, height: radius))
        return Path(path.cgPath)
    }
}
