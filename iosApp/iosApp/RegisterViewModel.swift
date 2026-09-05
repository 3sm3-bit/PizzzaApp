import Foundation
import Shared
import Combine

class RegisterViewModel: ObservableObject {
    @Published var nameUser: String = ""
    @Published var names: String = ""
    @Published var lastName: String = ""
    @Published var document: String = "11111111"
    @Published var email: String = ""
    @Published var phone: String = ""
    @Published var pass: String = ""
    @Published var address: String = ""
    @Published var latitude: String = ""
    @Published var longitude: String = ""
    
    @Published var isLoading: Bool = false
    @Published var errorMessage: String? = nil
    
    private let dataUseCase = KoinHelper.shared.getDataUseCase()
    
    var isButtonEnabled: Bool {
        !nameUser.isEmpty && !email.isEmpty && !phone.isEmpty && !pass.isEmpty
    }
    
    func register(onSuccess: @escaping (String) -> Void) {
        isLoading = true
        errorMessage = nil
        
        let request = UserResponse(
            nameUser: nameUser,
            names: names,
            lastName: lastName,
            document: document,
            email: email,
            password: pass,
            phone: "+52\(phone)",
            address: address,
            rol: "CLIENTE",
            area: "1",
            longitude: longitude,
            latitude: latitude,
            uid: ""
        )
        
        dataUseCase.registerUser(data: request) { [weak self] response, error in
            DispatchQueue.main.async {
                self?.isLoading = false
                if let error = error {
                    self?.errorMessage = error.localizedDescription
                    return
                }
                if let response = response {
                    onSuccess(response)
                }
            }
        }
    }
}
