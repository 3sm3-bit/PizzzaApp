import SwiftUI
import Shared

struct ProductDetailView: View {
    let product: ProductModel
    @Environment(\.presentationMode) var presentationMode
    
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
        VStack(spacing: 0) {
            // Header Image with Back Button
            ZStack(alignment: .topLeading) {
                // Placeholder for URL Image (AsyncImage would be ideal)
                Rectangle()
                    .fill(PizzaColors.red50)
                    .frame(height: 250)
                    .overlay(
                        Image(systemName: "pizza")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 150)
                            .foregroundColor(PizzaColors.red600)
                    )
                
                Button(action: { presentationMode.wrappedValue.dismiss() }) {
                    Image(systemName: "arrow.left")
                        .font(.system(size: 20, weight: .bold))
                        .foregroundColor(.white)
                        .padding(10)
                        .background(PizzaColors.red600)
                        .clipShape(Circle())
                }
                .padding(.top, 44)
                .padding(.leading, 16)
            }
            
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    // Name and Price
                    HStack(alignment: .bottom) {
                        VStack(alignment: .leading, spacing: 4) {
                            Text(product.nameProduct.uppercased())
                                .font(PizzaFonts.bold20)
                            Text("TAMAÑO: \(product.tamanio)")
                                .font(PizzaFonts.medium12)
                        }
                        Spacer()
                        Text("\(product.currencySymbol)\(product.price)")
                            .font(PizzaFonts.bold20)
                    }
                    
                    // Quantity and Subtotal
                    HStack {
                        HStack(spacing: 20) {
                            Button(action: { if quantity > 1 { quantity -= 1 } }) {
                                Image(systemName: "minus")
                                    .foregroundColor(PizzaColors.green600)
                                    .frame(width: 32, height: 32)
                            }
                            Text("\(quantity)")
                                .font(PizzaFonts.bold18)
                            Button(action: { quantity += 1 }) {
                                Image(systemName: "plus")
                                    .foregroundColor(PizzaColors.green600)
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
                                .foregroundColor(PizzaColors.green600)
                        }
                    }
                    
                    Divider()
                    
                    HStack(alignment: .top, spacing: 16) {
                        // Left Side: Description and Notes
                        VStack(alignment: .leading, spacing: 16) {
                            Text("Descripción")
                                .font(PizzaFonts.bold16)
                                .foregroundColor(PizzaColors.red600)
                            Text(product.description)
                                .font(PizzaFonts.medium12)
                                .foregroundColor(.gray)
                            
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Nota")
                                    .font(PizzaFonts.bold14)
                                    .foregroundColor(PizzaColors.red600)
                                TextEditor(text: $note)
                                    .frame(height: 100)
                                    .padding(4)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 12)
                                            .stroke(PizzaColors.red600, lineWidth: 1)
                                    )
                            }
                        }
                        .frame(maxWidth: .infinity)
                        
                        // Right Side: Dough and Crust (Only for Pizzas)
                        if product.type == "1" {
                            VStack(alignment: .leading, spacing: 16) {
                                Text("Masa")
                                    .font(PizzaFonts.bold14)
                                    .foregroundColor(PizzaColors.red600)
                                
                                ForEach(["TRADICIONAL", "CRUJIENTE"], id: \.self) { dough in
                                    HStack {
                                        Text(dough)
                                            .font(PizzaFonts.medium12)
                                        Spacer()
                                        Image(systemName: typeDough == dough ? "largecircle.fill.circle" : "circle")
                                            .foregroundColor(typeDough == dough ? PizzaColors.red600 : .gray)
                                    }
                                    .onTapGesture { typeDough = dough }
                                }
                                
                                Divider()
                                
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("Orilla / Queso")
                                        .font(PizzaFonts.bold14)
                                        .foregroundColor(PizzaColors.red600)
                                    Text("+ \(product.currencySymbol)\(product.priceChosse)")
                                        .font(PizzaFonts.medium10)
                                        .foregroundColor(PizzaColors.green600)
                                    
                                    Toggle("", isOn: $cheeseFilledCrust)
                                        .toggleStyle(SwitchToggleStyle(tint: PizzaColors.red600))
                                        .labelsHidden()
                                        .scaleEffect(0.8)
                                }
                            }
                            .frame(width: 120)
                        }
                    }
                    
                    Spacer(minLength: 100)
                }
                .padding(20)
            }
            
            // Add to Cart Button
            PizzaButton(title: "Agregar al Carrito", isEnabled: true) {
                CartManager.shared.addToCart(
                    product: product,
                    quantity: quantity,
                    typeDough: typeDough,
                    cheeseFilledCrust: cheeseFilledCrust,
                    note: note
                )
                presentationMode.wrappedValue.dismiss()
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 32)
        }
        .navigationBarHidden(true)
        .background(Color.white)
    }
}
