import SwiftUI
import TaySwitfUILibrary
import MapKit

struct AddressSelectionView: View {
    @Environment(\.presentationMode) var presentationMode
    var onConfirm: (String, String, String) -> Void
    
    @State private var region = MKCoordinateRegion(
        center: CLLocationCoordinate2D(latitude: 19.4326, longitude: -99.1332),
        span: MKCoordinateSpan(latitudeDelta: 0.05, longitudeDelta: 0.05)
    )
    
    @State private var address: String = "Cargando dirección..."
    @State private var isLocating = false
    
    var body: some View {
        ZStack {
            Map(coordinateRegion: $region, interactionModes: .all, showsUserLocation: true)
                .edgesIgnoringSafeArea(.all)
            
            Image(systemName: "mappin")
                .font(.system(size: 40))
                .foregroundColor(PizzaColors.red600)
                .offset(y: -20)
            
            VStack {
                UiTayCToolBar(uiTayText: "Seleccionar Dirección"){_ in
                    presentationMode.wrappedValue.dismiss()
                }
                Spacer()
                VStack(spacing: 16) {
                    HStack {
                        Image(systemName: "location.fill")
                            .foregroundColor(Color.uiTayRed600)
                        Text(address)
                            .font(PizzaFonts.medium14)
                            .lineLimit(2)
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

extension View {
    func uiTayCornerRadius(_ radius: CGFloat, corners: UIRectCorner) -> some View {
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
