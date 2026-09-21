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
    
    @Published var nameUser: String = ""
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
                let request = LoginRequest(nameUser: self.userLolin, password: self.passLogin)
                let response = try await self.dataUseCase.login(data: request)
                let userRole = response.userValid.rol?.uppercased() ?? ""
                
                if userRole != "CLIENTE" && userRole != "ADMIN" {
                    // Simular error de autorización si no es cliente ni admin
                    throw NSError(domain: "Auth", code: 401, userInfo: [NSLocalizedDescriptionKey: "Usuario no autorizado para esta aplicación"])
                }

                let entity = UserModel(
                        uid: response.userValid.uid ?? "",
                        nameUser: response.userValid.nameUser ?? "",
                        names: response.userValid.names ?? "",
                        lastName: response.userValid.lastName ?? "",
                        document: response.userValid.document ?? "",
                        email: response.userValid.email ?? "",
                        phone: response.userValid.phone ?? "",
                        address: response.userValid.address ?? "",
                        rol: response.userValid.rol ?? "CLIENTE",
                        area: response.userValid.area ?? "1",
                        longitude: response.userValid.longitude ?? "",
                        latitude: response.userValid.latitude ?? "",
                        token: response.token
                    )
                    try await self.dataUseCase.saveUserLocal(user: entity)
                    self.isLoginSuccessful = true
               
            }
        }
    }
    
    func register() {
        Task{
            await self.execute(){
                let request = UserResponse(
                    nameUser: self.nameUser,
                    names: self.names,
                    lastName: self.lastName,
                    document: "11111111",
                    email: self.email,
                    password: self.pass,
                    phone: "+52\(self.phone)",
                    address: self.address,
                    rol: "CLIENTE",
                    area: "1",
                    longitude: self.longitude,
                    latitude: self.latitude,
                    uid: ""
                )
                
                let response = try await self.dataUseCase.registerUser(data: request)
                self.isRegisterSuccessful = !response.isEmpty
            }
        }
        
    }
}
