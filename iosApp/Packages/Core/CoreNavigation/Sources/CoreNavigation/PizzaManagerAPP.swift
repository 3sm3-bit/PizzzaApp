//
//  PizzaManagerAPP.swift
//  CoreNavigation
//
//  Created by Developer on 12/09/26.
//
//  Estado global del flujo de la app (pantalla raíz, loading y error global).
//  La vista raíz que decide qué pantalla mostrar (`AppContentView`) vive en
//  el target de la app, porque es la que conoce a todas las features.
//

import Foundation
import Combine

public final class PizzaManagerAPP: ObservableObject {
    @Published public var currentScreen: ContentViewScreen? = .splash
    @Published public var isLoading: Bool = false
    @Published public var hbError: Bool = false

    public init() {}
}

public enum ContentViewScreen: Hashable {
    case splash
    case auth
    case home
}
