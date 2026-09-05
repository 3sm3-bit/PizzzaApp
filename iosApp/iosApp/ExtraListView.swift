import SwiftUI
import Shared

struct ExtraListView: View {
    @ObservedObject var viewModel: AppViewModel
    @State private var selectedCategory = "TODOS"
    var onLogout: () -> Void
    
    let categories = ["TODOS", "EXTRAS", "BEBIDAS"]
    
    var filteredExtras: [ProductModel] {
        viewModel.extraProducts.filter { product in
            switch selectedCategory {
            case "EXTRAS": return product.type == "2"
            case "BEBIDAS": return product.type == "3"
            default: return true
            }
        }
    }
    
    let columns = [
        GridItem(.flexible(), spacing: 16),
        GridItem(.flexible(), spacing: 16)
    ]
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                PizzaToolbar(title: "Complementos", showBackButton: false, showLogoutButton: true, onLogout: onLogout)
                
                // Filter Chips
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 12) {
                        ForEach(categories, id: \.self) { cat in
                            FilterChip(text: cat, isSelected: selectedCategory == cat) {
                                selectedCategory = cat
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
                                    Text("Acompaña tu pizza")
                                        .font(PizzaFonts.medium14)
                                        .foregroundColor(PizzaColors.red600)
                                    Text("Algo más?")
                                        .font(PizzaFonts.bold20)
                                }
                                Spacer()
                            }
                            .padding(.horizontal)
                            .padding(.top, 8)
                            
                            LazyVGrid(columns: columns, spacing: 16) {
                                ForEach(filteredExtras, id: \.uid) { product in
                                    ExtraProductCard(product: product)
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

struct ExtraProductCard: View {
    let product: ProductModel
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Image(systemName: "takeoutbag.and.cup.and.straw")
                .resizable()
                .scaledToFit()
                .frame(width: 60, height: 60)
                .foregroundColor(PizzaColors.red600)
                .padding(12)
                .background(PizzaColors.red50)
                .cornerRadius(12)
                .frame(maxWidth: .infinity)
            
            VStack(alignment: .leading, spacing: 4) {
                Text(product.nameProduct)
                    .font(PizzaFonts.bold14)
                    .lineLimit(1)
                
                HStack {
                    Text("\(product.currencySymbol)\(product.price)")
                        .font(PizzaFonts.bold16)
                        .foregroundColor(PizzaColors.green600)
                    
                    Spacer()
                    
                    Image(systemName: "plus.circle.fill")
                        .font(.system(size: 20))
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
