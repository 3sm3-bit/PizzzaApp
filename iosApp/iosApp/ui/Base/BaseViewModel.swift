//
//  BaseViewModel.swift
//  iosApp
//
//  Created by Developer on 12/09/26.
//

import Foundation


@MainActor
class BaseViewModel:ObservableObject,Sendable{
    
    @Published var uiTayLoading : Bool = false
    @Published var uiTayShimmer : Bool = false
    @Published var uiTayError : Bool = false
    @Published var uiTayEnableService : Bool = false
    @Published var uiTayErrorAction : CmActionErrorFlow = CmActionErrorFlow.actionDefault
    @Published var uiTayErrorException : ErrorGenericModel = ErrorGenericModel()
    
    init() {}
    @MainActor
    func execute(loading : Bool = true,errorFlag : Bool = true,hbShimmer : Bool = true,action : CmActionErrorFlow = CmActionErrorFlow.actionDefault ,closure: @escaping()async throws  -> Void) async{
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
                self.uiTayErrorException = ErrorGenericModel(message: "Ocurrio un errro intentelo ma starde.")
            }
        }
    }
    
    func setErrorCm(code : String = "o",title : String = "",message : String,action : CmActionErrorFlow = CmActionErrorFlow.actionDefault){
        self.uiTayError = true
        self.uiTayErrorAction = action
        self.uiTayErrorException = ErrorGenericModel(code : code,title:title, message: message)
    }
    
}


public enum CmActionErrorFlow: Hashable {
    case finalizeView
    case actionDefault
}
