import SwiftUI
import Shared
import TaySwitfUILibrary

struct PromotionsBanner: View {
    let promotions: [ProductModel]
    let onProductClick: (ProductModel) -> Void
    
    var body: some View {
        if promotions.isEmpty {
            EmptyView()
        } else {
            let isMultiple = promotions.count > 1
            let cardWidth = isMultiple ? UIScreen.main.bounds.width - 64 : UIScreen.main.bounds.width - 32
            
            VStack(alignment: .leading, spacing: 8) {
                ScrollView(.horizontal, showsIndicators: false) {
                    LazyHStack(spacing: isMultiple ? -18 : 16) {
                        ForEach(promotions, id: \.uid) { product in
                            Button(action: { onProductClick(product) }) {
                                CardView(product: product, cardWidth: cardWidth)
                            }
                            .buttonStyle(PlainButtonStyle())
                        }
                    }
                    .padding(.horizontal, 16)
                    .scrollTargetLayout()
                }
                .scrollTargetBehavior(.viewAligned)
                .frame(height: 150)
            }
            .padding(.vertical, 8)
        }
    }
    
    struct CardView: View {
        let product: ProductModel
        let cardWidth: CGFloat
        
        var body: some View {
            GeometryReader { geometry in
                let midX = geometry.frame(in: .global).midX
                let screenMidX = UIScreen.main.bounds.width / 2
                let distance = abs(screenMidX - midX)
                let scale = max(0.85, 1.0 - (distance / UIScreen.main.bounds.width) * 0.15)
                
                UiTayUrlImage(url: product.urlImg)
                    .frame(width: cardWidth, height: 150)
                    .cornerRadius(16)
                    .scaleEffect(scale)
                    .opacity(scale >= 0.9 ? 1.0 : 0.7)
            }
            .frame(width: cardWidth, height: 150)
        }
    }
}
