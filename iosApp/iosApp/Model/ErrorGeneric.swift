//
//  ErrorGeneric.swift
//  iosApp
//
//  Created by Developer on 12/09/26.
//

struct ErrorGenericModel : Codable{
    var  code : String = ""
    var  title : String = ""
    var message : String = ""
    static func getErrorApi(code : String = "0",title : String = "Error geneal"
                            ,message : String = "Ocurrio un error intentalo mas tarde") -> GenericError{
        return GenericError.runtimeError(ErrorGenericModel(code: code,title:title ,message: message))
    }
}

enum GenericError: Error {
    case runtimeError(ErrorGenericModel)
}
