//
//  AuthViewModel.swift
//  iosApp
//
//  Created by Developer on 12/09/26.
//

internal import Combine
import Foundation
import Shared


@MainActor
class AuthViewModel: BaseViewModel {
    
    private let dataUseCase = KoinHelper.shared.getDataUseCase()
    
    @Published var nameUser: String = "" {
        didSet {
            let filtered = nameUser.replacingOccurrences(of: " ", with: "")
            if filtered != nameUser {
                nameUser = filtered
            }
        }
    }
    @Published var names: String = ""
    @Published var lastName: String = ""
    @Published var email: String = ""
    @Published var phone: String = ""
    @Published var pass: String = ""
    @Published var address: String = ""
    @Published var latitude: String = ""
    @Published var longitude: String = ""
    @Published var userLolin: String = ""
    @Published var passLogin: String = ""
    @Published var isLoginSuccessful: Bool = false
    @Published var isRegisterSuccessful: Bool = false

    func login() {
        Task{
            await self.execute(){
                try? await self.dataUseCase.logout()
                let request = LoginRequest(
                    nameUser: self.userLolin.trimmingCharacters(in: .whitespacesAndNewlines),
                    password: self.passLogin.trimmingCharacters(in: .whitespacesAndNewlines)
                )
                let response = try await self.dataUseCase.login(data: request)
                let userRole = response.finalUser.rol?.uppercased() ?? ""
                
                if userRole != "CLIENTE" && userRole != "ADMIN" {
                    // Simular error de autorización si no es cliente ni admin
                    throw NSError(domain: "Auth", code: 401, userInfo: [NSLocalizedDescriptionKey: "Usuario no autorizado para esta aplicación"])
                }

                let entity = response.toUserModel()
                try await self.dataUseCase.saveUserLocal(user: entity)
                self.isLoginSuccessful = true
            }
        }
    }
    
    func register() {
        Task{
            await self.execute(){
                try? await self.dataUseCase.logout()
                let cleanPhone = self.phone.trimmingCharacters(in: .whitespacesAndNewlines)
                let request = UserResponse(
                    nameUser: self.nameUser.trimmingCharacters(in: .whitespacesAndNewlines),
                    names: self.names.trimmingCharacters(in: .whitespacesAndNewlines),
                    lastName: self.lastName.trimmingCharacters(in: .whitespacesAndNewlines),
                    document: "11111111",
                    email: self.email.trimmingCharacters(in: .whitespacesAndNewlines),
                    password: self.pass.trimmingCharacters(in: .whitespacesAndNewlines),
                    phone: "+52\(cleanPhone)",
                    address: self.address.trimmingCharacters(in: .whitespacesAndNewlines),
                    rol: "CLIENTE",
                    area: "1",
                    longitude: self.longitude.trimmingCharacters(in: .whitespacesAndNewlines),
                    latitude: self.latitude.trimmingCharacters(in: .whitespacesAndNewlines),
                    uid: ""
                )
                
                let response = try await self.dataUseCase.registerUser(data: request)
                self.isRegisterSuccessful = !response.isEmpty
            }
        }
        
    }
}
