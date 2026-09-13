import SwiftUI
import TaySwitfUILibrary

struct RegisterView: View {

    @ObservedObject private var viewModel: AuthViewModel = Resolver.shared.resolve(AuthViewModel.self)
    @Environment(\.presentationMode) var presentationMode
    @State private var showAddressSelection = false
    @State var enableButton: Bool = false
    @Environment(\.dismiss) var dismiss

    var body: some View {
        BaseViewGeneral(viewModel: viewModel){
            VStack(spacing: 0) {
                
                UiTayCToolBar(uiTayText: "Registrate"){_ in
                    dismiss()
                }
            
              
                ScrollView {
                    VStack(spacing: 8) {
                        Spacer().frame(height: 8)
                        UiTayTextLayout(
                                       value: $viewModel.nameUser,
                                        hint: "Usuario",
                                        uiTayChangeText :{validButton() }
                                      
                        )
                        UiTayTextLayout(
                                       value: $viewModel.names,
                                        hint: "Nombres",
                                        uiTayChangeText :{validButton() }
                                      
                        )
                        UiTayTextLayout(
                                       value: $viewModel.lastName,
                                        hint: "Apellidos",
                                        uiTayChangeText :{validButton() }
                                      
                        )
                        UiTayTextLayout(
                                       value: $viewModel.email,
                                        hint: "Email",
                                        uiTayChangeText :{validButton() }
                                      
                        )
                     
                        UiTayTextLayout(
                                       value: $viewModel.phone,
                                        hint: "Celular",
                                        keyboardType : .numberPad,
                                        uiTayChangeText :{validButton() }
                                      
                        )
                        
                        UiTayTextLayout(
                                       value: $viewModel.pass,
                                        hint: "Contraseña",
                                        isPassword : true,
                                        uiTayChangeText :{ validButton()}
                        )
                     
                     
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Dirección")
                                .font(PizzaFonts.medium14)
                                .foregroundColor(Color.uiTayGreen600)
                            
                            HStack {
                                Text(viewModel.address.isEmpty ? "Seleccionar dirección" : viewModel.address)
                                    .font(PizzaFonts.medium14)
                                    .foregroundColor(viewModel.address.isEmpty ? .gray : .black)
                                Spacer()
                                Image(systemName: "mappin.and.ellipse")
                                    .foregroundColor(Color.uiTayGreen600)
                            }
                            .padding()
                            .frame(height: 50)
                            .background(Color.white)
                            .cornerRadius(24)
                            .overlay(
                                RoundedRectangle(cornerRadius: 24)
                                    .stroke(Color.uiTayGreen600, lineWidth: 1)
                            )
                        }
                        .onTapGesture {
                            showAddressSelection = true
                        }
                    
                        Spacer()
                        
                        UITayButton(text: "Registrarse") {
                            viewModel.register()
                        }.disabled(!enableButton)
                        
                    }
                    .padding(.horizontal, 24)
                }
            }
        }.sheet(isPresented: $showAddressSelection) {
            AddressSelectionView { address, lat, lng in
                viewModel.address = address
                viewModel.latitude = lat
                viewModel.longitude = lng
            }
        }.onChange(of: viewModel.isRegisterSuccessful) { oldValue, newValue in
            if newValue {
                dismiss()
            }
        }
    }
    
     func validButton(){
        enableButton = viewModel.nameUser.isNotEmpty() &&
        viewModel.names.isNotEmpty() &&
        viewModel.lastName.isNotEmpty() &&
        viewModel.lastName.uiTayValidEmail() &&
        viewModel.pass.isNotEmpty() &&
        viewModel.phone.uiTayValidNumberPhone() &&
        viewModel.address.isNotEmpty() &&
        viewModel.address != "Selecciona dirección en el mapa"
    }
}
