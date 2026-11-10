//
//  SharedModels+Extensions.swift
//  CoreDomain
//
//  Conformidades de modelos Kotlin (Shared) usadas por varias features.
//  Deben declararse una sola vez para toda la app, por eso viven aquí.
//

import Shared

extension ParentOrderModel: Identifiable {
    public var id: String { uid }
}
