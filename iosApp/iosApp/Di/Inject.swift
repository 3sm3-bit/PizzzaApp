//
//  Inject.swift
//  iosApp
//
//  Created by Developer on 12/09/26.
//

import Swinject
import CoreDomain

@propertyWrapper
struct Inject<Component> {
    var component: Component
    
    init() {
        self.component = Resolver.shared.resolve(Component.self)
    }
    
    var wrappedValue: Component { component }
}


@MainActor
func registerDependencies() {
    let uiTayResolver = Resolver.shared
    assembleViewModel(uiTayResolver: uiTayResolver)
}
