import SwiftUI
import TaySwitfUILibrary

struct LoginView: View {
    @StateObject private var viewModel = LoginViewModel()
    var onNavigateToHome: () -> Void
    var onNavigateToRegister: () -> Void
    
    var body: some View {
        ZStack {
            
            VStack {
                PizzaToolbar(title: "Pizzzeria 3 Z", showBackButton: false)
                
                ScrollView {
                    VStack(spacing: 24) {
                        Spacer().frame(height: 40)
                        
                        Image("ic_pizzza") // Make sure this asset exists or use a placeholder
                            .resizable()
                            .scaledToFit()
                            .frame(width: 150, height: 150)
                            .padding(.bottom, 32)
                        
                        UITayTextField(
                                        label: "Usuario o email",
                                        placeholder: "Usuario o email",
                                        text: $viewModel.user
                        )
                        
                   
                        UITayTextField(
                                        label: "Contraseña",
                                        placeholder: "Contraseña",
                                        text: $viewModel.pass,
                                        model: EditModel(typeSecret: true, stylePresent: .endIcon)
                        )
                        
                        
                        if let error = viewModel.errorMessage {
                            Text(error)
                                .foregroundColor(.red)
                                .font(PizzaFonts.medium12)
                        }
                        
                        Spacer().frame(height: 8)
                        
                        UITayButton(text: "Iniciar Sesión") {
                            viewModel.login {
                                onNavigateToHome()
                            }
                         }
                        
                        if viewModel.isLoading {
                            ProgressView()
                                .tint(PizzaColors.red600)
                        }
                        
                        NavigationLink(destination: RegisterView(onRegisterSuccess: {
                            // After register, user goes back to login usually
                        })) {
                            Text("registrate ahora")
                                .font(PizzaFonts.bold14)
                                .underline()
                                .foregroundColor(PizzaColors.green600)
                        }
                        .padding(.top, 24)
                        
                        Spacer()
                    }
                    .padding(.horizontal, 24)
                }
            }
        }
        .navigationBarHidden(true)
    }
}
