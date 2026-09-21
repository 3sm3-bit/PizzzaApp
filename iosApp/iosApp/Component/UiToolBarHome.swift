//
//  UiToolBarHome.swift
//  iosApp
//
//  Created by Developer on 20/09/26.
//

import SwiftUI

struct UiToolBarHome: View {

    var typeFlow: Bool
    var visibleCart : Bool = true
    var typeRefresh : Bool = false
    var onClick: () -> Void = {}
    @ObservedObject var cartManager = CartManager.shared
    
    var body: some View {
        HStack{
            VStack(alignment: .leading) {
                Text("Bienvenido a la")
                    .font(PizzaFonts.medium14)
                    .foregroundColor(Color.uiTayRed600)
                Image(uiName: "ic_logo_pizzzeria")
                    .resizable()
                    .frame(width: 120, height: 40)
            }
            Spacer()
            if(visibleCart){
                ZStack(alignment: .topTrailing) {
                    Image(uiName: "ic_cart")
                        .renderingMode(.template)
                        .resizable()
                        .foregroundColor(Color.uiTayRed600)
                        .frame(width: 32, height: 32)
                    
                    if cartManager.cart.count > 0 {
                        Text("\(cartManager.cart.count)")
                            .font(.system(size: 10, weight: .bold))
                            .foregroundColor(.white)
                            .frame(width: 18, height: 18)
                            .background(Color.uiTayGreen600)
                            .clipShape(Circle())
                            .offset(x: 10, y: -10)
                    }
                }
            }
            Image(systemName: typeRefresh ? "arrow.clockwise" : "rectangle.portrait.and.arrow.right"  )
                .foregroundColor(Color.uiTayRed600)
                .font(.system(size: 20, weight: .bold))
                .onTapGesture {
                    onClick()
                }
        }
          .padding(.horizontal)
          .padding(.top, 8).padding(.trailing,12)
        }
}

