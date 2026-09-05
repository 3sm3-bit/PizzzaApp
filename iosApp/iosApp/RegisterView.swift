import SwiftUI

struct RegisterView: View {
    @StateObject private var viewModel = RegisterViewModel()
    @Environment(\.presentationMode) var presentationMode
    var onRegisterSuccess: () -> Void
    
    @State private var showAddressSelection = false
    
    var body: some View {
        ZStack {
            PizzaColors.background.edgesIgnoringSafeArea(.all)
            
            VStack(spacing: 0) {
                PizzaToolbar(title: "Registro", onBack: {
                    presentationMode.wrappedValue.dismiss()
                })
                
                ScrollView {
                    VStack(spacing: 16) {
                        Spacer().frame(height: 16)
                        
                        PizzaTextField(hint: "Usuario", text: $viewModel.nameUser)
                        PizzaTextField(hint: "Nombre", text: $viewModel.names)
                        PizzaTextField(hint: "Apellido", text: $viewModel.lastName)
                        PizzaTextField(hint: "Email", text: $viewModel.email)
                        PizzaTextField(hint: "Teléfono", text: $viewModel.phone)
                        PizzaTextField(hint: "Contraseña", text: $viewModel.pass, isPassword: true)
                        
                        // Address section
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Dirección")
                                .font(PizzaFonts.medium14)
                                .foregroundColor(PizzaColors.red600)
                            
                            HStack {
                                Text(viewModel.address.isEmpty ? "Seleccionar dirección" : viewModel.address)
                                    .font(PizzaFonts.medium14)
                                    .foregroundColor(viewModel.address.isEmpty ? .gray : .black)
                                Spacer()
                                Image(systemName: "mappin.and.ellipse")
                                    .foregroundColor(PizzaColors.red600)
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
                        .onTapGesture {
                            showAddressSelection = true
                        }
                        
                        if let error = viewModel.errorMessage {
                            Text(error)
                                .foregroundColor(.red)
                                .font(PizzaFonts.medium12)
                        }
                        
                        Spacer().frame(height: 16)
                        
                        PizzaButton(
                            title: "Registrarse",
                            isEnabled: viewModel.isButtonEnabled && !viewModel.isLoading
                        ) {
                            viewModel.register { _ in
                                onRegisterSuccess()
                                presentationMode.wrappedValue.dismiss()
                            }
                        }
                        
                        if viewModel.isLoading {
                            ProgressView()
                                .tint(PizzaColors.red600)
                        }
                        
                        Spacer().frame(height: 32)
                    }
                    .padding(.horizontal, 24)
                }
            }
        }
        .navigationBarHidden(true)
        .sheet(isPresented: $showAddressSelection) {
            AddressSelectionView { address, lat, lng in
                viewModel.address = address
                viewModel.latitude = lat
                viewModel.longitude = lng
            }
        }
    }
}
