//
//  PizzaView.swift
//  iosApp
//
//  Created by Developer on 12/09/26.
//

import SwiftUI
import Shared
import TaySwitfUILibrary

struct PizzaView: View {
    
    @ObservedObject var viewModel: HomeViewModel
    @ObservedObject var cartManager = CartManager.shared
    @State private var selectedSize = "GRANDE"
    @State private var product   : ProductModel? = nil
    let sizes = ["GRANDE", "MEDIANO", "CHICO"]
    @State   var destiny: ActionNav?
    var onLogout: () -> Void
    
    var filteredPizzas: [ProductModel] {
        cartManager.pizzaProducts.filter { product in
            product.tamanio.uppercased() == selectedSize ||
            (selectedSize == "CHICO" && product.tamanio.uppercased() == "CHICA") ||
            (selectedSize == "MEDIANO" && product.tamanio.uppercased() == "MEDIANA")
        }
    }
    
    public var body: some View {
        VStack(spacing: 16) {
            UiToolBarHome(typeFlow: true){
                onLogout()
            }
            ScrollView(.vertical, showsIndicators: false) {
                VStack(spacing: 12) {
                    
                    PromotionsBanner(promotions: CartManager.shared.promotionsProducts) { promoProduct in
                        self.product = promoProduct
                        destiny = .uiNext
                    }
                    
                    HStack(spacing: 12) {
                        ForEach(sizes, id: \.self) { size in
                            UITaySelectedChip(
                                text: size,
                                isSelected: selectedSize == size
                            ) {
                                selectedSize = size
                            }
                        }
                    }
                    .padding(.horizontal, 24)

                    if !CartManager.shared.promotionsProducts.isEmpty {
                        LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 16) {
                            ForEach(filteredPizzas, id: \.uid) { product in
                                PizzaGridCard(product: product)
                                    .onTapGesture {
                                        self.product = product
                                        destiny = .uiNext
                                    }
                            }
                        }
                        .padding(.horizontal)
                    } else {
                        LazyVStack(spacing: 12) {
                            ForEach(filteredPizzas, id: \.uid) { product in
                                PizzaProductCard(product: product)
                                    .onTapGesture {
                                        self.product = product
                                        destiny = .uiNext
                                    }
                            }
                        }
                        .padding(.horizontal)
                    }
                }
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.uiTayGrey50.ignoresSafeArea())
        .uiTayHideToolbar()
        .uiTayNavigate(
            item: self.product,
            to: { safeProduct in ProductDetailView(product: safeProduct) },
            when: $destiny.cmToBool(.uiNext)
        )
    }

    struct PizzaProductCard: View {
        let product: ProductModel
        
        var body: some View {
            HStack(spacing: 12) {
                UiTayUrlImage(url: product.urlImg)
                    .frame(width: 150,height: 150)
                
                VStack(alignment: .leading, spacing: 10) {
                    
                    Text(product.nameProduct)
                        .font(Font.uiMontB18)
                        .padding(.trailing, 16)
                    
                    Text(product.description_)
                        .font(Font.uiMontR12)
                        .foregroundColor(.gray)
                        .lineLimit(3)
                        .padding(.trailing, 16)
                    
                    HStack {
                        Text("\(product.currencySymbol)\(product.price)")
                            .font(Font.uiMontB18)
                            .foregroundColor(Color.uiTayRed600)
                        
                        Spacer()
                        
                        Image(systemName: "plus.circle.fill")
                            .font(.system(size: 24))
                            .foregroundColor(Color.uiTayGreen600)
                    } .padding(.trailing, 16)
                    
                }
            }.uiTayBgShadowDark()
            
        }
    }

    struct PizzaGridCard: View {
        let product: ProductModel
        
        var body: some View {
            VStack(alignment: .leading, spacing: 0) {
                UiTayUrlImage(url: product.urlImg)
                    .frame(height: 110)
                    .clipped()
                
                HStack(alignment: .top, spacing: 8) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(product.nameProduct)
                            .font(Font.uiMontB12)
                            .foregroundColor(.black)
                            .lineLimit(2)
                        
                        Text(product.description_)
                            .font(Font.uiMontR8)
                            .foregroundColor(.gray)
                            .lineLimit(2)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    
                    VStack(alignment: .trailing, spacing: 4) {
                        Text("\(product.currencySymbol)\(product.price)")
                            .font(Font.uiMontB12)
                            .foregroundColor(Color.uiTayGreen600)
                        
                        Spacer()
                        
                        Image(systemName: "plus.app.fill")
                            .font(.system(size: 20))
                            .foregroundColor(Color.uiTayGreen600)
                    }
                }
                .padding(12)
            }
            .frame(height: 180)
            .background(Color.white)
            .cornerRadius(20)
            .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
        }
    }
}
