//
//  Resolver.swift
//  iosApp
//
//  Created by Developer on 12/09/26.
//

final class Resolver {
    static let shared = Resolver()
    
    private var factories: [String: () -> Any] = [:]
    
    private init() {
        // not code
    }
    
    func register<Service>(_ type: Service.Type, factory: @escaping () -> Service) {
        factories[String(describing: type)] = factory
    }
    
    
    func resolve<Service>(_ type: Service.Type) -> Service {
        guard let factory = factories[String(describing: type)],
              let service = factory() as? Service else {
            fatalError("No factory for \(type)")
        }
        return service
    }
}
