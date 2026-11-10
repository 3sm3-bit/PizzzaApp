//
//  Resolver.swift
//  CoreDomain
//
//  Created by Developer on 12/09/26.
//
//  Contenedor de dependencias compartido. Las features resuelven sus
//  ViewModels aquí; la app los registra al arrancar (ver `registerDependencies`).
//

public final class Resolver {
    public static let shared = Resolver()

    private var factories: [String: () -> Any] = [:]

    private init() {
        // not code
    }

    public func register<Service>(_ type: Service.Type, factory: @escaping () -> Service) {
        factories[String(describing: type)] = factory
    }

    public func resolve<Service>(_ type: Service.Type) -> Service {
        guard let factory = factories[String(describing: type)],
              let service = factory() as? Service else {
            fatalError("No factory for \(type)")
        }
        return service
    }
}
