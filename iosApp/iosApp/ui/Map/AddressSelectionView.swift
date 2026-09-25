import SwiftUI
import TaySwitfUILibrary
import MapKit
import Contacts

struct AddressSelectionView: View {
    var initialLat: String? = nil
    var initialLng: String? = nil
    var initialAddress: String? = nil
    var onConfirm: (String, String, String) -> Void
    
    @State private var region: MKCoordinateRegion
    @State private var address: String
    @State private var isLocating = false
    @State private var debounceTask: Task<Void, Never>? = nil
    @State private var searchDebounceTask: Task<Void, Never>? = nil
    
    // Search states
    @State private var searchQuery: String = ""
    @State private var searchResults: [MKMapItem] = []
    @State private var isSelectingSearch = false
    
    @Environment(\.dismiss) var dismiss

    init(initialLat: String? = nil, initialLng: String? = nil, initialAddress: String? = nil, onConfirm: @escaping (String, String, String) -> Void) {
        self.initialLat = initialLat
        self.initialLng = initialLng
        self.initialAddress = initialAddress
        self.onConfirm = onConfirm
        
        let lat = Double(initialLat ?? "") ?? 19.4326
        let lng = Double(initialLng ?? "") ?? -99.1332
        
        _region = State(initialValue: MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: lat, longitude: lng),
            span: MKCoordinateSpan(latitudeDelta: 0.002, longitudeDelta: 0.002)
        ))
        _address = State(initialValue: initialAddress ?? "Cargando dirección...")
    }
    
    var body: some View {
        BaseViewGeneral {
            ZStack(alignment: .bottom) {
                // 1. Capa de Mapa e Interfaz Superior
                VStack(spacing: 0) {
                    UiTayCToolBar(uiTayText: "Seleccionar Ubicación",
                                  uiTayModifier: UiToolBarModel()) { _ in
                        dismiss()
                    }
                    
                    ZStack {
                        Map(coordinateRegion: $region, interactionModes: .all, showsUserLocation: true)
                            .ignoresSafeArea()
                        
                        // Pin fijo en el centro
                        Image(systemName: "mappin")
                            .font(.system(size: 40))
                            .foregroundColor(PizzaColors.red600)
                            .offset(y: -20)
                            .allowsHitTesting(false)
                    }
                    .overlay(alignment: .top) {
                        // Barra de búsqueda y sugerencias
                        VStack(spacing: 4) {
                            HStack(alignment: .top) {
                                Image(systemName: "magnifyingglass")
                                    .foregroundColor(PizzaColors.red600)
                                    .padding(.top, 4)
                                
                                ZStack(alignment: .topLeading) {
                                    if searchQuery.isEmpty {
                                        Text("Ej. Ciudad, Zona / Colonia, Calle o Av.")
                                            .font(PizzaFonts.medium14)
                                            .foregroundColor(.gray.opacity(0.7))
                                            .padding(.top, 2)
                                    }
                                    TextField("", text: $searchQuery, axis: .vertical)
                                        .font(PizzaFonts.medium14)
                                        .lineLimit(1...3)
                                        .onChange(of: searchQuery) { query in
                                            debounceSearchAddress(query: query)
                                        }
                                }
                                
                                if !searchQuery.isEmpty {
                                    Button(action: {
                                        searchDebounceTask?.cancel()
                                        searchQuery = ""
                                        searchResults = []
                                    }) {
                                        Image(systemName: "xmark.circle.fill")
                                            .foregroundColor(.gray)
                                            .padding(.top, 4)
                                    }
                                }
                            }
                            .padding(12)
                            .background(Color.white)
                            .cornerRadius(12)
                            .shadow(radius: 4)
                            .padding(.horizontal, 16)
                            .padding(.top, 8)
                            
                            if !searchResults.isEmpty {
                                ScrollView {
                                    VStack(alignment: .leading, spacing: 0) {
                                        ForEach(searchResults, id: \.self) { item in
                                            Button(action: {
                                                UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
                                                
                                                isSelectingSearch = true
                                                let coordinate = item.placemark.coordinate
                                                
                                                var nameParts: [String] = []
                                                if let name = item.name { nameParts.append(name) }
                                                if let title = item.placemark.title, !nameParts.contains(title) { nameParts.append(title) }
                                                
                                                self.address = nameParts.joined(separator: ", ")
                                                self.region = MKCoordinateRegion(
                                                    center: coordinate,
                                                    span: MKCoordinateSpan(latitudeDelta: 0.002, longitudeDelta: 0.002)
                                                )
                                                
                                                self.searchResults = []
                                                self.searchQuery = ""
                                                
                                                DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                                                    isSelectingSearch = false
                                                }
                                            }) {
                                                VStack(alignment: .leading, spacing: 4) {
                                                    Text(item.name ?? "Ubicación")
                                                        .font(PizzaFonts.medium14)
                                                        .foregroundColor(.black)
                                                        .lineLimit(2)
                                                    if let addressString = item.placemark.title {
                                                        Text(addressString)
                                                            .font(.system(size: 12))
                                                            .foregroundColor(.gray)
                                                            .lineLimit(2)
                                                    }
                                                }
                                                .padding(.horizontal, 16)
                                                .padding(.vertical, 10)
                                                .frame(maxWidth: .infinity, alignment: .leading)
                                            }
                                            Divider()
                                        }
                                    }
                                    .background(Color.white)
                                    .cornerRadius(12)
                                    .shadow(radius: 4)
                                    .padding(.horizontal, 16)
                                }
                                .frame(maxHeight: 220)
                            }
                        }
                    }
                }
                
                // 2. Tarjeta Inferior pegada completamente al borde
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
                        dismiss()
                    }
                }
                .padding(.horizontal, 20)
                .padding(.top, 20)
                .padding(.bottom, 28) // Padding interno respetando espacio para Home Indicator
                .background(Color.white)
                .cornerRadius(20, corners: [.topLeft, .topRight])
                .shadow(radius: 10)
                .ignoresSafeArea(edges: .bottom) // Rompe la restricción del Safe Area en la base
            }
            .ignoresSafeArea(.all, edges: .bottom)
            .onAppear {
                if initialLat == nil {
                    updateAddress()
                }
            }
            .onChange(of: region.center.latitude) { _ in
                if !isSelectingSearch {
                    debounceAddressUpdate()
                }
            }
            .onChange(of: region.center.longitude) { _ in
                if !isSelectingSearch {
                    debounceAddressUpdate()
                }
            }
        }
    }
    
    // MARK: - Métodos de Búsqueda y Dirección
    private func debounceSearchAddress(query: String) {
        searchDebounceTask?.cancel()
        
        let trimmedQuery = query.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmedQuery.isEmpty else {
            searchResults = []
            return
        }
        
        searchDebounceTask = Task {
            try? await Task.sleep(nanoseconds: 500_000_000)
            if Task.isCancelled { return }
            executeFlexibleSearch(query: trimmedQuery)
        }
    }
    
    private func executeFlexibleSearch(query: String) {
        let cleanedQuery = cleanSearchQuery(query)
        
        performLocalSearch(query: cleanedQuery) { items in
            if !items.isEmpty {
                self.searchResults = items
            } else {
                let reversedQuery = self.reorderQueryTokens(cleanedQuery)
                if reversedQuery != cleanedQuery {
                    self.performLocalSearch(query: reversedQuery) { fallbackItems in
                        self.searchResults = fallbackItems
                    }
                } else {
                    self.searchResults = []
                }
            }
        }
    }
    
    private func performLocalSearch(query: String, completion: @escaping ([MKMapItem]) -> Void) {
        let request = MKLocalSearch.Request()
        request.naturalLanguageQuery = query
        request.region = region
        
        let search = MKLocalSearch(request: request)
        search.start { response, error in
            DispatchQueue.main.async {
                guard let response = response else {
                    completion([])
                    return
                }
                completion(response.mapItems)
            }
        }
    }
    
    private func cleanSearchQuery(_ rawQuery: String) -> String {
        let stopWords = [" y ", " esquina ", " con ", " esq ", " entre "]
        var result = rawQuery.lowercased()
        for word in stopWords {
            result = result.replacingOccurrences(of: word, with: " ")
        }
        return result
    }
    
    private func reorderQueryTokens(_ query: String) -> String {
        let parts = query.components(separatedBy: CharacterSet(charactersIn: ", "))
            .filter { !$0.trimmingCharacters(in: .whitespaces).isEmpty }
        guard parts.count > 1 else { return query }
        return parts.reversed().joined(separator: " ")
    }
    
    private func debounceAddressUpdate() {
        debounceTask?.cancel()
        debounceTask = Task {
            try? await Task.sleep(nanoseconds: 800_000_000)
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
            
            if let postalAddress = placemark.postalAddress {
                let street = postalAddress.street
                let neighborhood = placemark.subLocality ?? ""
                let country = postalAddress.country ?? ""
                
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
