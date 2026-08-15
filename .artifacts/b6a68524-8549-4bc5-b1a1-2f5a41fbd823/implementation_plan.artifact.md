# Plan de Inicialización del Proyecto PizzzaApp

Este plan detalla los pasos para configurar el proyecto siguiendo las especificaciones y la estructura proporcionada en las capturas de pantalla y fragmentos de código.

## Cambios Propuestos

### Configuración Global y Repositorio

#### [MODIFY] [.gitignore](file:///Users/tayler/Desktop/project/android/PizzzaApp/.gitignore)
- Se actualizará con las reglas específicas proporcionadas para ignorar archivos de IDE, builds y credenciales.

#### [MODIFY] [libs.versions.toml](file:///Users/tayler/Desktop/project/android/PizzzaApp/gradle/libs.versions.toml)
- Se añadirán las versiones y librerías necesarias: Room, Ktor, Koin, Datastore, Coroutines, Serialization, etc.

#### [MODIFY] [settings.gradle.kts](file:///Users/tayler/Desktop/project/android/PizzzaApp/settings.gradle.kts)
- Se renombrarán los módulos:
    - `:androidApp` -> `:composeApp`
    - `:sharedLogic` -> `:shared`
- Se mantendrá `:sharedUI` por el momento o se integrará según sea necesario.

#### [MODIFY] [build.gradle.kts (Raíz)](file:///Users/tayler/Desktop/project/android/PizzzaApp/build.gradle.kts)
- Se aplicarán los plugins globales según el fragmento "gradlew principal".

---

### Módulo Shared (Lógica Compartida)

#### [NEW] [shared/build.gradle.kts](file:///Users/tayler/Desktop/project/android/PizzzaApp/shared/build.gradle.kts)
- Configuración de Kotlin Multiplatform.
- Integración de Room, KSP y BuildConfig.
- Configuración de Ktor y base URL: `https://servertay.onrender.com/services`.

#### [NEW] Estructura de paquetes en `commonMain`
- `com.tayler.appvalutay.di`
- `com.tayler.appvalutay.manager.db` (incluye `AppDataBase.kt`)
- `com.tayler.appvalutay.manager.store` (incluye `ValePreferenceManager.kt`)
- `com.tayler.appvalutay.model`
- `com.tayler.appvalutay.repository` (db, mapper, network, preferences)
- `com.tayler.appvalutay.usecases` (db, network, preferences y archivos `.kt` base)
- `com.tayler.appvalutay.utils`

---

### Módulo Android (ComposeApp)

#### [NEW] [composeApp/build.gradle.kts](file:///Users/tayler/Desktop/project/android/PizzzaApp/composeApp/build.gradle.kts)
- Configuración de Android y Compose Multiplatform.
- Dependencias de Koin, Navigation y Accompanist.

#### [NEW] Estructura de paquetes en `androidMain`
- `com.tayler.appvalutay.application`
- `com.tayler.appvalutay.component`
- `com.tayler.appvalutay.di`
- `com.tayler.appvalutay.ui`
- `com.tayler.appvalutay.utils`

## Verificación

### Pruebas Automatizadas
- Sincronización de Gradle para asegurar que todas las dependencias y plugins se cargan correctamente.

### Verificación Manual
- Confirmar que la estructura de carpetas en el IDE coincide con las imágenes proporcionadas.
- Verificar que el `BuildConfig` genera correctamente la URL base.
