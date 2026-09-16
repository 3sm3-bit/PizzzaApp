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
    @State private var selectedSize = "GRANDE"
    @State private var product   : ProductModel? = nil
    let sizes = ["GRANDE", "MEDIANO", "CHICO"]
    @State   var destiny: ActionNav?
    var onLogout: () -> Void
    
    var filteredPizzas: [ProductModel] {
        CartManager.shared.pizzaProducts.filter { product in
            product.tamanio.uppercased() == selectedSize ||
            (selectedSize == "CHICO" && product.tamanio.uppercased() == "CHICA") ||
            (selectedSize == "MEDIANO" && product.tamanio.uppercased() == "MEDIANA")
        }
    }
    
    public var body: some View {
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
            .padding(.vertical, 8)
            
            ScrollView(.vertical, showsIndicators: false) {
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
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.uiTayGrey100.ignoresSafeArea())
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
}

