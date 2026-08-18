# Implementación de Lógica y UI en iOS (SwiftUI + Koin + Firebase)

Este plan detalla los pasos para replicar la funcionalidad de Android en la versión de iOS, incluyendo la interfaz moderna, el filtrado de pedidos, la actualización de datos y la integración con Firebase para avisos por voz.

## Cambios Propuestos

### 1. Módulo Compartido (`:shared`)

Prepararemos Koin para que sea accesible desde Swift y expondremos los casos de uso necesarios.

#### [NUEVO] [KoinInit.kt](file:///Users/tayler/Desktop/project/android/PizzzaApp/shared/src/commonMain/kotlin/com/tayler/pizzzaapp/di/KoinInit.kt)
Crearemos un punto de entrada común para inicializar Koin en ambas plataformas.

#### [NUEVO] [KoinHelper.kt](file:///Users/tayler/Desktop/project/android/PizzzaApp/shared/src/iosMain/kotlin/com/tayler/pizzzaapp/di/KoinHelper.kt)
Un objeto `KoinHelper` que permitirá a Swift obtener instancias de `DataUseCase` inyectadas.

---

### 2. Aplicación iOS (`iosApp`)

Implementaremos la interfaz en SwiftUI y la lógica de voz/notificaciones.

#### [NUEVO] [VoiceManager.swift](file:///Users/tayler/Desktop/project/android/PizzzaApp/iosApp/iosApp/VoiceManager.swift)
Gestor que utiliza `AVSpeechSynthesizer` para leer los pedidos en voz alta en español.

#### [NUEVO] [OrderViewModel.swift](file:///Users/tayler/Desktop/project/android/PizzzaApp/iosApp/iosApp/OrderViewModel.swift)
ViewModel en Swift que maneja el estado de la UI (lista de pedidos, filtros, cargando) y se comunica con `DataUseCase`.

#### [MODIFICAR] [ContentView.swift](file:///Users/tayler/Desktop/project/android/PizzzaApp/iosApp/iosApp/ContentView.swift)
Rediseño completo para igualar la interfaz de Android:
- Tema claro con tarjetas blancas.
- Cabecera con selector de filtros (TODOS, PENDIENTE, etc.).
- Botón de refresco manual.
- Lista con indicadores de color según el estado.

#### [MODIFICAR] [AppDelegate.swift](file:///Users/tayler/Desktop/project/android/PizzzaApp/iosApp/iosApp/AppDelegate.swift)
- Inicialización de Koin al arrancar la app.
- Integración de `VoiceManager` al recibir notificaciones push de Firebase en primer plano.
- Notificación al ViewModel para refrescar la lista cuando llega un nuevo pedido.

## Plan de Verificación

### Verificación Manual
1. Abrir la app en el simulador de iOS o dispositivo físico.
2. Verificar que la lista de pedidos carga correctamente desde la base de datos local/remota.
3. Probar los filtros de estado.
4. Presionar el botón de refresco y verificar que los datos se actualizan.
5. Enviar una notificación push de prueba desde el backend y verificar que la app (en primer plano) lee el pedido en voz alta.
