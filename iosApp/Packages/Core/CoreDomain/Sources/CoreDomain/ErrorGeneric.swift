//
//  ErrorGeneric.swift
//  CoreDomain
//
//  Created by Developer on 12/09/26.
//

public struct ErrorGenericModel: Codable {
    public var code: String = ""
    public var title: String = ""
    public var message: String = ""

    public init(code: String = "", title: String = "", message: String = "") {
        self.code = code
        self.title = title
        self.message = message
    }

    public static func getErrorApi(code: String = "0", title: String = "Error geneal",
                                   message: String = "Ocurrio un error intentalo mas tarde") -> GenericError {
        return GenericError.runtimeError(ErrorGenericModel(code: code, title: title, message: message))
    }
}

public enum GenericError: Error {
    case runtimeError(ErrorGenericModel)
}
