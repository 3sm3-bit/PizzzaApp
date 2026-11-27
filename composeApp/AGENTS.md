# AGENTS.md - Android App Architecture & Guidelines

## Tech Stack & Versions
- **Language**: Kotlin 2.x
- **UI Toolkit**: Jetpack Compose, Material 3
- **Navigation**: Jetpack Navigation / Compose Navigation
- **Dependency Injection**: Koin (`koin-androidx-compose`, `koin-compose-viewmodel`)
- **Async**: Coroutines, Flow, `collectAsStateWithLifecycle()`

## Project Structure & Modularization
- **`:composeApp`**: Main entry point, application class (`PizzaApplication`), navigation graph (`AppNavigation.kt`), and MainActivity.
- **`:core:ui`**: Design system, shared theme colors (`tay_red_600`, `tay_green_600`), typography (`textB20`, `textM14`), and reusable UI components.
- **`:core:navigation`**: Navigation routes and destination definitions (`ScreenInitNav`).
- **`:feature:auth`**: Authentication flow (Login, Register, AuthViewModel).
- **`:feature:home`**: Main client catalog, product cards, categories, and branch selection (`ScreenClientHome`, `ScreenCart`).
- **`:feature:cart`**: Cart management, delivery address selection (`AddressScreen`), order summary (`ScreenOrderSummary`), and payment web view.
- **`:feature:orders`**: Order history and active orders tracking.
- **`:feature:monitoring`**: Real-time order tracking map (`ScreenMonitor`, Google Maps Compose).
- **`:feature:info`**: Help, terms and conditions, and contact info.

## Conventions & Rules
- Use ViewModels extending or interacting with UseCases via Koin injection (`koinViewModel()`).
- State management must be driven by `StateFlow` and collected with `collectAsStateWithLifecycle()`.
- Follow Material 3 guidelines and project utility styles.
