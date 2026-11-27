# AGENTS.md - iOS App Architecture & Guidelines

## Tech Stack & Versions
- **Language**: Swift 5.9+ / Swift 6
- **UI Framework**: SwiftUI, MapKit, CoreLocation, WebKit
- **Modularization**: Swift Package Manager (SPM) local packages
- **State & Concurrency**: `@StateObject`, `@ObservedObject`, `ObservableObject`, Swift Concurrency (`async`/`await`), Combine.
- **Shared Integration**: KMP Shared XCFramework (`Shared`)

## Project Structure & Swift Packages
- **`iosApp` (Main App Target)**: Entry point (`iOSApp.swift`, `ContentView.swift`), routing (`PizzaManagerAPP`), and assembly.
- **`CoreUI` Package**: Design system (`PizzaColors`, `PizzaFonts`, `BaseViewGeneral`, `BaseViewModel`).
- **`FeatureAuth` Package**: Login and registration views (`LoginView`, `RegisterView`, `AuthViewModel`).
- **`FeatureHome` Package**: Catalog, product cards, extra views, and store view models.
- **`FeatureCart` Package**: Cart management (`CartManager`), checkout, address selection (`AddressSelectionView`), payment web view, and order summary (`OrderSummaryView`).
- **`FeatureOrders` Package**: Order history, active orders view models (`OrderViewModel`, `ClientOrderViewModel`).
- **`FeatureMonitoring` Package**: Real-time order tracking map (`MonitorView`).
- **`FeatureInfo` Package**: Help and information screens (`InfoMainView`, `InfoDetailView`, `InfoWebView`).

## Conventions & Rules
- All views in feature packages must use `public` visibility for their initializers and structs where accessed by the main app target.
- ViewModels must conform to `ObservableObject` and handle KMP callbacks or async/await correctly on the `@MainActor`.
- Use `PizzaColors` and `PizzaFonts` from `CoreUI` for consistent styling.
