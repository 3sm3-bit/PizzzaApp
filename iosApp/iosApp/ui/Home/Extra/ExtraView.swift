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
    @State private var selectedCategory = "TODOS"
    var onLogout: () -> Void
    @State private var product   : ProductModel? = nil
    @State   var destiny: ActionNav?
    let categories = ["TODOS", "EXTRAS", "BEBIDAS"]
    
    var filteredExtras: [ProductModel] {
        CartManager.shared.extraProducts.filter { product in
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
            HStack(spacing: 12) {
                VStack(alignment: .leading) {
                    Text("Bienvenido a la pizzeria")
                        .font(PizzaFonts.medium14)
                        .foregroundColor(Color.uiTayRed600)
                    Text("Has tu pedido ya!")
                        .font(PizzaFonts.bold20)
                }
                Spacer()
                Image(uiName: "ic_cart")
                    .renderingMode(.template)
                    .resizable()
                    .foregroundColor(Color.uiTayRed600)
                    .frame(width: 32, height: 32)
                    .badge(CartManager.shared.cart.count > 0 ? String(CartManager.shared.cart.count) : nil)
                Image(systemName: "rectangle.portrait.and.arrow.right")
                    .foregroundColor(Color.uiTayRed600)
                    .font(.system(size: 20, weight: .bold))
                    .onTapGesture {
                        onLogout()
                    }
            }
            .padding(.horizontal)
            .padding(.top, 8)
            
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
            .padding(.vertical, 8)
            
            ScrollView {
                LazyVGrid(columns: columns, spacing: 16) {
                    ForEach(filteredExtras, id: \.uid) { product in
                        ExtraProductCard(product: product)
                            .onTapGesture {
                                self.product = product
                                destiny = .uiNext
                        }
                    }
               
            } .padding(.horizontal)
        }
       }.frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.uiTayGrey100.ignoresSafeArea())
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
        VStack(alignment: .leading, spacing: 12) {
            UiTayUrlImage(url: product.urlImg)
                .frame(height: 120)
           
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
                        .foregroundColor(Color.uiTayGreen600)
                }
            }.padding(8)
        }.uiTayBgShadowDark()
    }
}
