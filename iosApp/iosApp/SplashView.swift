import SwiftUI
import Shared

struct SplashView: View {
    @State private var isActive = false
    @State private var isLoggedIn = false
    private let dataUseCase = KoinHelper.shared.getDataUseCase()
    
    @State private var loadingText = "Cargando menú delicioso..."
    @State private var errorMessage: String? = nil
    
    var body: some View {
        ZStack {
            PizzaColors.red600.edgesIgnoringSafeArea(.all)
            
            VStack(spacing: 24) {
                Image("ic_pizzza")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 200, height: 200)
                
                Text("PIZZZA APP")
                    .font(.system(size: 42, weight: .bold))
                    .foregroundColor(.white)
                
                Spacer().frame(height: 60)
                
                if let error = errorMessage {
                    Text(error)
                        .foregroundColor(.white)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 40)
                    
                    Button(action: {
                        errorMessage = nil
                        checkLoginStatus()
                    }) {
                        Text("Reintentar")
                            .font(PizzaFonts.bold16)
                            .foregroundColor(.black)
                            .padding(.horizontal, 24)
                            .padding(.vertical, 12)
                            .background(Color.white)
                            .cornerRadius(12)
                    }
                } else {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        .scaleEffect(1.5)
                    
                    Text(loadingText)
                        .font(PizzaFonts.medium14)
                        .foregroundColor(.white.opacity(0.7))
                }
            }
        }
        .onAppear {
            checkLoginStatus()
        }
        .fullScreenCover(isPresented: $isActive) {
            if isLoggedIn {
                HomeTabView()
            } else {
                NavigationView {
                    LoginView(onNavigateToHome: {
                        isLoggedIn = true
                    }, onNavigateToRegister: {
                        // Managed via navigation
                    })
                }
            }
        }
    }
    
    private func checkLoginStatus() {
        // 1. Sincronizar productos (Igual que Android)
        dataUseCase.syncProducts { response, error in
            if error != nil {
                DispatchQueue.main.async {
                    self.errorMessage = "Error al conectar con el servidor. Revisa tu conexión."
                }
                return
            }
            
            // 2. Verificar usuario local
            dataUseCase.getUserLocal { user, error in
                DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                    if let u = user {
                        self.isLoggedIn = (u.rol == "CLIENTE")
                    }
                    self.isActive = true
                }
            }
        }
    }
}
