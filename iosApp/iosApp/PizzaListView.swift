import SwiftUI
import Shared

struct PizzaListView: View {
    @ObservedObject var viewModel: AppViewModel
    @State private var selectedSize = "GRANDE"
    var onLogout: () -> Void
    
    let sizes = ["GRANDE", "MEDIANO", "CHICO"]
    
    var filteredPizzas: [ProductModel] {
        viewModel.pizzaProducts.filter { product in
            product.tamanio.uppercased() == selectedSize ||
            (selectedSize == "CHICO" && product.tamanio.uppercased() == "CHICA") ||
            (selectedSize == "MEDIANO" && product.tamanio.uppercased() == "MEDIANA")
        }
    }
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                PizzaToolbar(title: "Pizzzeria 3 Z", showBackButton: false, showLogoutButton: true, onLogout: onLogout)
                
                // Filter Chips
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 12) {
                        ForEach(sizes, id: \.self) { size in
                            FilterChip(text: size, isSelected: selectedSize == size) {
                                selectedSize = size
                            }
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                }
                
                if viewModel.isLoading {
                    Spacer()
                    ProgressView().tint(PizzaColors.red600)
                    Spacer()
                } else {
                    ScrollView {
                        VStack(spacing: 16) {
                            HStack {
                                VStack(alignment: .leading) {
                                    Text("Bienvenido a la pizzeria")
                                        .font(PizzaFonts.medium14)
                                        .foregroundColor(PizzaColors.red600)
                                    Text("Has tu pedido ya!")
                                        .font(PizzaFonts.bold20)
                                }
                                Spacer()
                            }
                            .padding(.horizontal)
                            .padding(.top, 8)
                            
                            LazyVStack(spacing: 12) {
                                ForEach(filteredPizzas, id: \.uid) { product in
                                    PizzaProductCard(product: product)
                                        .onTapGesture {
                                            viewModel.selectProduct(product)
                                        }
                                }
                            }
                            .padding(.horizontal)
                        }
                    }
                }
            }
            .background(PizzaColors.background)
            .fullScreenCover(item: $viewModel.selectedProduct) { product in
                ProductDetailView(product: product)
            }
        }
        .onAppear {
            viewModel.getProductsList()
        }
    }
}

extension ProductModel: Identifiable {
    public var id: String { uid }
}

struct PizzaProductCard: View {
    let product: ProductModel
    
    var body: some View {
        HStack(spacing: 16) {
            // Placeholder for image
            Image(systemName: "pizza")
                .resizable()
                .scaledToFit()
                .frame(width: 80, height: 80)
                .foregroundColor(PizzaColors.red600)
                .padding(8)
                .background(PizzaColors.red50)
                .cornerRadius(12)
            
            VStack(alignment: .leading, spacing: 4) {
                Text(product.nameProduct)
                    .font(PizzaFonts.bold16)
                
                Text(product.description)
                    .font(PizzaFonts.medium10)
                    .foregroundColor(.gray)
                    .lineLimit(2)
                
                HStack {
                    Text("\(product.currencySymbol)\(product.price)")
                        .font(PizzaFonts.bold18)
                        .foregroundColor(PizzaColors.green600)
                    
                    Spacer()
                    
                    Image(systemName: "plus.circle.fill")
                        .font(.system(size: 24))
                        .foregroundColor(PizzaColors.red600)
                }
            }
        }
        .padding()
        .background(Color.white)
        .cornerRadius(16)
        .shadow(color: Color.black.opacity(0.05), radius: 5, x: 0, y: 2)
    }
}

// Add bold18 if missing
extension PizzaFonts {
    static let bold18 = Font.system(size: 18, weight: .bold)
}
