// swift-tools-version: 5.9
//
// Módulos iOS de PizzzaApp.
//
// Un solo manifiesto con un target por módulo: cada target es un módulo Swift
// independiente (con su propio control de acceso), pero Xcode solo necesita
// referenciar un paquete local.
//
// Reglas de dependencias:
//   - Core*   no depende de ninguna Feature.
//   - Feature* depende solo de Core* (nunca de otra Feature).
//   - La navegación entre features pasa por `PizzaRouter` (CoreNavigation),
//     que la app configura en `AppContentView`.
//
// `Shared` es el framework de Kotlin Multiplatform. Se genera con:
//   ./scripts/build-shared-xcframework.sh   (desde iosApp/)
import PackageDescription

let tayUI: Target.Dependency = .product(name: "TaySwitfUILibrary", package: "TaySwitfUILibrary")

let package = Package(
    name: "PizzaModules",
    platforms: [.iOS(.v17)],
    products: [
        .library(name: "SharedKMP", targets: ["Shared"]),
        .library(name: "CoreDomain", targets: ["CoreDomain"]),
        .library(name: "CoreNavigation", targets: ["CoreNavigation"]),
        .library(name: "CoreUI", targets: ["CoreUI"]),
        .library(name: "FeatureAuth", targets: ["FeatureAuth"]),
        .library(name: "FeatureAddress", targets: ["FeatureAddress"]),
        .library(name: "FeatureHome", targets: ["FeatureHome"]),
        .library(name: "FeatureCart", targets: ["FeatureCart"]),
        .library(name: "FeatureOrders", targets: ["FeatureOrders"]),
        .library(name: "FeatureInfo", targets: ["FeatureInfo"]),
        .library(name: "FeatureMonitoring", targets: ["FeatureMonitoring"]),
    ],
    dependencies: [
        .package(path: "../../../../project/ios/TaySwitfUILibrary"),
    ],
    targets: [
        // MARK: - Core

        .binaryTarget(
            name: "Shared",
            path: "Core/SharedKMP/Shared.xcframework"
        ),
        .target(
            name: "CoreDomain",
            dependencies: ["Shared"],
            path: "Core/CoreDomain/Sources/CoreDomain"
        ),
        .target(
            name: "CoreNavigation",
            dependencies: ["Shared"],
            path: "Core/CoreNavigation/Sources/CoreNavigation"
        ),
        .target(
            name: "CoreUI",
            dependencies: ["Shared", "CoreDomain", "CoreNavigation", tayUI],
            path: "Core/CoreUI/Sources/CoreUI"
        ),

        // MARK: - Features

        .target(
            name: "FeatureAuth",
            dependencies: ["Shared", "CoreDomain", "CoreNavigation", "CoreUI", tayUI],
            path: "Features/FeatureAuth/Sources/FeatureAuth"
        ),
        .target(
            name: "FeatureAddress",
            dependencies: ["CoreUI", tayUI],
            path: "Features/FeatureAddress/Sources/FeatureAddress"
        ),
        .target(
            name: "FeatureHome",
            dependencies: ["Shared", "CoreDomain", "CoreNavigation", "CoreUI", tayUI],
            path: "Features/FeatureHome/Sources/FeatureHome"
        ),
        .target(
            name: "FeatureCart",
            dependencies: ["Shared", "CoreDomain", "CoreNavigation", "CoreUI", tayUI],
            path: "Features/FeatureCart/Sources/FeatureCart"
        ),
        .target(
            name: "FeatureOrders",
            dependencies: ["Shared", "CoreDomain", "CoreNavigation", "CoreUI", tayUI],
            path: "Features/FeatureOrders/Sources/FeatureOrders"
        ),
        .target(
            name: "FeatureInfo",
            dependencies: ["Shared", "CoreUI", tayUI],
            path: "Features/FeatureInfo/Sources/FeatureInfo"
        ),
        .target(
            name: "FeatureMonitoring",
            dependencies: ["Shared", "CoreUI", tayUI],
            path: "Features/FeatureMonitoring/Sources/FeatureMonitoring"
        ),
    ]
)
