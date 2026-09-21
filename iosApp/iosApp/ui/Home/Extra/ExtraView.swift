//
//  ExtraView.swift
//  iosApp
//
//  Created by Developer on 12/09/26.
//

import SwiftUI
import Shared
import TaySwitfUILibrary

struct ExtraView: View {
    @ObservedObject var viewModel: HomeViewModel
    @ObservedObject var cartManager = CartManager.shared
    @State private var selectedCategory = "TODOS"
    var onLogout: () -> Void
    @State private var product   : ProductModel? = nil
    @State   var destiny: ActionNav?
    let categories = ["TODOS", "EXTRAS", "BEBIDAS"]
    
    var filteredExtras: [ProductModel] {
        cartManager.extraProducts.filter { product in
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
        VStack(spacing: 16) {
            UiToolBarHome(typeFlow: true){
                onLogout()
            }
            ScrollView {
                VStack(spacing: 12) {
                    PromotionsBanner(promotions: CartManager.shared.promotionsProducts) { promoProduct in
                        self.product = promoProduct
                        destiny = .uiNext
                    }
                    
                    HStack(spacing: 12) {
                        ForEach(categories, id: \.self) { size in
                            UITaySelectedChip(
                                text: size,
                                isSelected: selectedCategory == size
                            ) {
                                selectedCategory = size
                            }
                        }
                    }
                    .padding(.horizontal, 24)

                    LazyVGrid(columns: columns, spacing: 16) {
                        ForEach(filteredExtras, id: \.uid) { product in
                            ExtraProductCard(product: product)
                                .onTapGesture {
                                    self.product = product
                                    destiny = .uiNext
                                }
                        }
                    }
                    .padding(.horizontal)
                }
            }
       }.frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.uiTayGrey50.ignoresSafeArea())
        .uiTayHideToolbar()
        .uiTayNavigate(
            item: self.product,
            to: { safeProduct in ProductDetailView(product: safeProduct) },
            when: $destiny.cmToBool(.uiNext)
        )
    
    
   }
}

struct ExtraProductCard: View {
    let product: ProductModel
    
    var body: some View {
        VStack(spacing: 0){
            UiTayUrlImage(url: product.urlImg)
                    .frame(height: 120)
                    .clipped()
           
            HStack(alignment: .top, spacing: 12) {
                Text(product.nameProduct)
                    .font(PizzaFonts.bold12)
                    .foregroundColor(.black)
                    .lineLimit(2)
                    .multilineTextAlignment(.leading)
                    .frame(maxWidth: .infinity, alignment: .topLeading)
                
                Text("\(product.currencySymbol)\(product.price)")
                    .font(PizzaFonts.bold12)
                    .foregroundColor(PizzaColors.green600)
            }
            .padding(10)
        }.uiTayBgShadowDark()
        .frame(height: 175)
    }
}
