import SwiftUI

struct PizzaButton: View {
    let title: String
    let isEnabled: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text(title)
                .font(PizzaFonts.bold16)
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .frame(height: 50)
                .background(isEnabled ? PizzaColors.red600 : Color.gray.opacity(0.5))
                .cornerRadius(12)
        }
        .disabled(!isEnabled)
    }
}

struct PizzaTextField: View {
    let hint: String
    @Binding var text: String
    var isPassword: Bool = false
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(hint)
                .font(PizzaFonts.medium14)
                .foregroundColor(PizzaColors.red600)
            
            Group {
                if isPassword {
                    SecureField("", text: $text)
                } else {
                    TextField("", text: $text)
                }
            }
            .padding()
            .frame(height: 50)
            .background(Color.white)
            .cornerRadius(8)
            .overlay(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(PizzaColors.red600, lineWidth: 1)
            )
        }
    }
}

struct FilterChip: View {
    let text: String
    let isSelected: Bool
    let onClick: () -> Void
    
    var body: some View {
        Button(action: onClick) {
            Text(text)
                .font(PizzaFonts.bold12)
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .background(isSelected ? PizzaColors.red600 : Color.white)
                .foregroundColor(isSelected ? .white : PizzaColors.red600)
                .cornerRadius(12)
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .stroke(PizzaColors.red600, lineWidth: 1)
                )
        }
    }
}

struct PizzaToolbar: View {
    let title: String
    var showBackButton: Bool = true
    var showLogoutButton: Bool = false
    var onBack: (() -> Void)? = nil
    var onLogout: (() -> Void)? = nil
    
    var body: some View {
        HStack {
            if showBackButton {
                Button(action: { onBack?() }) {
                    Image(systemName: "chevron.left")
                        .foregroundColor(PizzaColors.red600)
                        .font(.system(size: 20, weight: .bold))
                }
            } else if showLogoutButton {
                Button(action: { onLogout?() }) {
                    Image(systemName: "rectangle.portrait.and.arrow.right")
                        .foregroundColor(PizzaColors.red600)
                        .font(.system(size: 20, weight: .bold))
                }
            }
            
            Spacer()
            
            Text(title)
                .font(PizzaFonts.bold20)
                .foregroundColor(PizzaColors.red600)
            
            Spacer()
            
            if showBackButton || showLogoutButton {
                Image(systemName: "chevron.left")
                    .opacity(0)
            }
        }
        .padding()
        .background(PizzaColors.red50)
    }
}
