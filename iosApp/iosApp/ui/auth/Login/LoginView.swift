import SwiftUI
import TaySwitfUILibrary

struct LoginView: View {
   
    @ObservedObject private var viewModel: AuthViewModel = Resolver.shared.resolve(AuthViewModel.self)
    @EnvironmentObject var managerAPP: PizzaManagerAPP
    @State var enableButton: Bool = false
    @State var destiny: ActionNav?
    @State private var showInfo: Bool = false

    var body: some View {
        BaseViewGeneral(viewModel: viewModel){
            ZStack(alignment: .bottom) {
                VStack {
                    Spacer()
                    Image("ic_logo_pizzzeria")
                        .resizable()
                        .frame(width: 250, height: 100)
                    
                    UiTayTextLayout(
                        value: $viewModel.userLolin,
                        hint: "Usuario o email",
                        uiTayChangeText: { validButton() }
                    )
                    Spacer().frame(height: 24)
               
                    UiTayTextLayout(
                        value: $viewModel.passLogin,
                        hint: "Contraseña",
                        isPassword: true,
                        uiTayChangeText: { validButton() }
                    )
                    
                    Spacer().frame(height: 48)
                    
                    UITayButton(text: "Iniciar Sesión") {
                        viewModel.login()
                    }.disabled(!enableButton)
                    
                    Text("registrate ahora")
                        .font(PizzaFonts.bold14)
                        .underline()
                        .foregroundColor(PizzaColors.green600)
                        .padding(.top, 24)
                        .onTapGesture {
                            destiny = .uiNext
                        }
                    Spacer()
                }
                .padding(.horizontal, 24)
                
                Text("Información y Ayuda")
                    .font(PizzaFonts.bold14)
                    .underline()
                    .foregroundColor(Color.uiTayRed600)
                    .padding(.bottom, 24)
                    .onTapGesture {
                        showInfo = true
                    }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .ignoresSafeArea(.all)
            .background(Color.white)
        }.onChange(of: viewModel.isLoginSuccessful) { oldValue, newValue in
            if newValue {
                managerAPP.currentScreen = .home
            }
        }
        .uiTayNavigate(to: { RegisterView() }, when: $destiny.cmToBool(.uiNext))
        .uiTayNavigate(to: { InfoMainView() }, when: $showInfo)
    }
    
    private func validButton(){
        enableButton = viewModel.userLolin.count > 2 && viewModel.passLogin.count > 2
    }
}
