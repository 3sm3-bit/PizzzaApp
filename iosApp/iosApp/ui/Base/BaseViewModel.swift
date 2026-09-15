import Foundation
import Shared

@MainActor
class BaseViewModel:ObservableObject,Sendable{
    
    @Published var uiTayLoading : Bool = false
    @Published var uiTayShimmer : Bool = false
    @Published var uiTayError : Bool = false
    @Published var uiTayEnableService : Bool = false
    @Published var uiTayErrorAction : UiTayActionErrorFlow = UiTayActionErrorFlow.actionDefault
    @Published var uiTayErrorException : ErrorGenericModel = ErrorGenericModel()
    
    init() {}
    @MainActor
    func execute(loading : Bool = true,errorFlag : Bool = true,hbShimmer : Bool = true,action : UiTayActionErrorFlow = UiTayActionErrorFlow.actionDefault ,closure: @escaping()async throws  -> Void) async{
        do{
            self.uiTayLoading = loading
            self.uiTayShimmer = hbShimmer
            self.uiTayEnableService = true
            try await closure()
            self.uiTayLoading = false
            self.uiTayShimmer = false
            self.uiTayEnableService = false
        }catch{
            self.uiTayLoading = false
            self.uiTayShimmer = false
            self.uiTayEnableService = false
            if(errorFlag){
                self.uiTayError = true
                self.uiTayErrorAction = action
                if let apiError = error as? UiTayApiException {
                    self.uiTayErrorException = ErrorGenericModel(
                        code: String(apiError.code),
                        title: apiError.title.isEmpty ? "Error" : apiError.title,
                        message: apiError.messageApi.isEmpty ? "Ocurrió un error inesperado" : apiError.messageApi
                    )
                } else if error is ErrorNetwork {
                    self.uiTayErrorException = ErrorGenericModel(
                        code: "0",
                        title: "Sin conexión",
                        message: "No hay conexión a internet"
                    )
                } else {
                    self.uiTayErrorException = ErrorGenericModel(
                        code: "0",
                        title: "Error",
                        message: error.localizedDescription.contains("KotlinException") ? "Ocurrio un error intentalo mas tarde." : error.localizedDescription
                    )
                }
            }
        }
    }
    
    func setErrorCm(code : String = "o",title : String = "",message : String,action : UiTayActionErrorFlow = UiTayActionErrorFlow.actionDefault){
        self.uiTayError = true
        self.uiTayErrorAction = action
        self.uiTayErrorException = ErrorGenericModel(code : code,title:title, message: message)
    }
    
}


public enum UiTayActionErrorFlow: Hashable {
    case finalizeView
    case actionDefault
}
