# AGENTS.md - PizzzaApp Master Architecture Guide

This project is a cross-platform food ordering application built with **Kotlin Multiplatform (KMP)** sharing core business logic, data layers, and networking, accompanied by native UI clients for **Android (Jetpack Compose)** and **iOS (SwiftUI + SPM)**.

## Sub-project Context Guides
- **KMP Shared Module**: See [`shared/AGENTS.md`](./shared/AGENTS.md)
- **Android App & Features**: See [`composeApp/AGENTS.md`](./composeApp/AGENTS.md)
- **iOS App & Packages**: See [`iosApp/AGENTS.md`](./iosApp/AGENTS.md)

## Core Principles for AI Assistance
1. **Respect Module Boundaries**: Do not put platform-specific UI code into `shared`.
2. **Data Flow**: Network and Database operations are orchestrated in `shared` via `DataUseCase`, consumed by ViewModels on both platforms.
3. **Consistency**: Follow existing naming conventions, MVI/MVVM/ObservableObject patterns, and dependency injection (Koin).
