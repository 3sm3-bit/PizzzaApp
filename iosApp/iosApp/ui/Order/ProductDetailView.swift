import SwiftUI
import Shared
import  TaySwitfUILibrary

struct ProductDetailView: View {
    
    let product: ProductModel
    @Environment(\.dismiss) var dismiss
    
    @State private var quantity: Int = 1
    @State private var typeDough: String = "TRADICIONAL"
    @State private var cheeseFilledCrust: Bool = false
    @State private var note: String = ""
    
    var totalPrice: Double {
        let basePrice = Double(product.price) ?? 0.0
        let crustPrice = cheeseFilledCrust ? (Double(product.priceChosse) ?? 0.0) : 0.0
        return (basePrice + crustPrice) * Double(quantity)
    }
    
    var body: some View {
        BaseViewGeneral{
            VStack(spacing: 0) {
                ZStack(alignment: .topLeading) {
                    UiTayUrlImage(url: product.urlImg)
                        .frame(height: 180)
                        .frame(maxWidth: .infinity)
                    Image(systemName: "chevron.left")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(Color.uiTayRed600)
                        .frame(width: 32, height: 32)
                        .background(Color.white.opacity(0.85))
                        .clipShape(Circle())
                        .padding(.top, 17)
                        .padding(.leading, 8)
                        .onTapGesture {
                            dismiss()
                        }
                }.offset(y: -5)
                ScrollView {
                    VStack(alignment: .leading, spacing: 20) {
                        HStack(alignment: .bottom) {
                            VStack(alignment: .leading, spacing: 4) {
                                Text(product.nameProduct.uppercased())
                                    .font(PizzaFonts.bold20)
                                if product.type == "1" {
                                    Text("TAMAÑO: \(product.tamanio)")
                                        .font(PizzaFonts.medium12)
                                }
                            }
                            Spacer()
                            Text("\(product.currencySymbol)\(product.price)")
                                .font(PizzaFonts.bold20)
                        }
                        .padding(.top, 4)
                        HStack {
                            HStack(spacing: 20) {
                                Button(action: { if quantity > 1 { quantity -= 1 } }) {
                                    Image(systemName: "minus")
                                        .foregroundColor(Color.uiTayGreen600)
                                        .frame(width: 32, height: 32)
                                }
                                Text("\(quantity)")
                                    .font(Font.uiMontB12)
                                Button(action: { quantity += 1 }) {
                                    Image(systemName: "plus")
                                        .foregroundColor(Color.uiTayGreen600)
                                        .frame(width: 32, height: 32)
                                }
                            }
                            .padding(.horizontal, 8)
                            .padding(.vertical, 4)
                            .background(Color(hex: 0xF0F2F5))
                            .cornerRadius(12)
                            
                            Spacer()
                            
                            VStack(alignment: .trailing) {
                                Text("Total a pagar")
                                    .font(PizzaFonts.medium12)
                                    .foregroundColor(.gray)
                                Text("\(product.currencySymbol)\(String(format: "%.2f", totalPrice))")
                                    .font(PizzaFonts.bold20)
                                    .foregroundColor(Color.uiTayGreen600)
                            }
                        }
                        
                        Divider()
                        
                        HStack(alignment: .top, spacing: 16) {
                            VStack(alignment: .leading, spacing: 16) {
                                Text("Descripción")
                                    .font(PizzaFonts.bold16)
                                    .foregroundColor(.uiTayRed600)
                                Text(String(product.description_))
                                    .font(PizzaFonts.medium12)
                                    .foregroundColor(.gray)
                                
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("Nota")
                                        .font(PizzaFonts.bold14)
                                        .foregroundColor(.uiTayRed600)
                                    TextEditor(text: $note)
                                        .frame(height: 80)
                                        .padding(4)
                                        .overlay(
                                            RoundedRectangle(cornerRadius: 12)
                                                .stroke(Color.uiTayRed600, lineWidth: 1)
                                        )
                                }
                            }
                            .frame(maxWidth: .infinity)
                            
                            if product.type == "1" {
                                VStack(alignment: .leading, spacing: 16) {
                                    Text("Masa")
                                        .font(PizzaFonts.bold14)
                                        .foregroundColor(.uiTayRed600)
                                    
                                    ForEach(["TRADICIONAL", "CRUJIENTE"], id: \.self) { dough in
                                        HStack {
                                            Text(dough)
                                                .font(PizzaFonts.medium12)
                                            Spacer()
                                            Image(systemName: typeDough == dough ? "largecircle.fill.circle" : "circle")
                                                .foregroundColor(typeDough == dough ? .uiTayRed600 : .gray)
                                        }
                                        .onTapGesture { typeDough = dough }
                                    }
                                    
                                    if typeDough != "CRUJIENTE" {
                                        Divider()
                                        VStack(alignment: .leading, spacing: 4) {
                                            Text("Orilla / Queso")
                                                .font(PizzaFonts.bold14)
                                                .foregroundColor(.uiTayRed600)
                                            Text("+ \(product.currencySymbol)\(product.priceChosse)")
                                                .font(PizzaFonts.medium10)
                                                .foregroundColor(.uiTayGreen600)
                                            
                                            Toggle("", isOn: $cheeseFilledCrust)
                                                .toggleStyle(SwitchToggleStyle(tint: .uiTayRed600))
                                                .labelsHidden()
                                                .scaleEffect(0.8)
                                        }
                                    }
                                }
                                .frame(width: 120)
                            }
                        }
                        
                        Spacer(minLength: 100)
                    }
                    .padding(20)
                }
                
                UITayButton(text: "Agregar al Carrito") {
                    CartManager.shared.addToCart(
                        product: product,
                        quantity: quantity,
                        typeDough: typeDough,
                        cheeseFilledCrust: cheeseFilledCrust,
                        note: note
                    )
                    dismiss()
                }
                .padding(.horizontal, 20)
                .padding(.bottom, 12)
            }.background(Color.uiTayGrey50)
        }
       
    }
}
