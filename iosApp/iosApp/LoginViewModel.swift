import Foundation
import Shared
import Combine

class LoginViewModel: ObservableObject {
    @Published var user: String = ""
    @Published var pass: String = ""
    @Published var isLoading: Bool = false
    @Published var errorMessage: String? = nil
    @Published var isLoginSuccessful: Bool = false
    
    private let dataUseCase = KoinHelper.shared.getDataUseCase()
    
    var isButtonEnabled: Bool {
        user.count > 2 && pass.count > 2
    }
    
    func login(onSuccess: @escaping () -> Void) {
        isLoading = true
        errorMessage = nil
        
        let request = LoginRequest(nameUser: user, password: pass)
        
        dataUseCase.login(data: request) { [weak self] response, error in
            DispatchQueue.main.async {
                self?.isLoading = false
                
                if let error = error {
                    self?.errorMessage = error.localizedDescription
                    return
                }
                
                if let response = response {
                    let userValid = response.userValid
                    let entity = UserEntity(
                        uid: userValid.uid ?? "",
                        nameUser: userValid.nameUser ?? "",
                        names: userValid.names ?? "",
                        lastName: userValid.lastName ?? "",
                        document: userValid.document ?? "",
                        email: userValid.email ?? "",
                        phone: userValid.phone ?? "",
                        address: userValid.address ?? "",
                        rol: userValid.rol ?? "CLIENTE",
                        area: userValid.area ?? "1",
                        longitude: userValid.longitude ?? "",
                        latitude: userValid.latitude ?? "",
                        token: response.token
                    )
                    
                    self?.dataUseCase.saveUserLocal(user: entity) { _ in
                        DispatchQueue.main.async {
                            self?.isLoginSuccessful = true
                            onSuccess()
                        }
                    }
                }
            }
        }
    }
}
