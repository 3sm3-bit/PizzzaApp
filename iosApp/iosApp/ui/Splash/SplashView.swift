import SwiftUI
import Shared
import TaySwitfUILibrary

struct SplashView: View {

    @ObservedObject private var viewModel: AppViewModel = Resolver.shared.resolve(AppViewModel.self)
    @State private var loadingText = "Cargando menú delicioso..."
    @State private var errorMessage: String? = nil
    
    @EnvironmentObject var managerAPP: PizzaManagerAPP

    var body: some View {
        
        BaseViewGeneral(viewModel: viewModel){
            VStack(spacing: 24) {
                Image("ic_logo_m_pizzzeria")
                    .resizable()
                    .frame(width: 200, height: 150)
                Text("PIZZZERIA")
                    .font(.system(size: 42, weight: .bold))
                    .foregroundColor(.uiTayRed600)
                ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .uiTayRed600))
                        .scaleEffect(1.5)
                    
                Text(loadingText)
                        .font(PizzaFonts.medium14)
                        .foregroundColor(.uiTayGrey600)
            }.frame(maxWidth: .infinity, maxHeight: .infinity)
            .ignoresSafeArea(.all)
            .background(Color.white)
            .onAppear(){
                viewModel.loadValidData()
            }
           }.onChange(of: viewModel.successLogin) { oldValue, newValue in
               if let isSuccess = newValue {
                   managerAPP.currentScreen = isSuccess ? .home : .auth
               }
           }
    }
        
}
