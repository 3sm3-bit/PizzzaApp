# Plan de Mejora de la Vista de Pedidos

Este plan corrige el problema de la pantalla en blanco mediante la adición de estados de error, carga y "lista vacía", además de corregir un error en el mapeo de base de datos que eliminaba los productos de los pedidos.

## Cambios Propuestos

### [Componente UI] - [composeApp]

#### [MODIFICAR] [BaseActivity.kt](file:///Users/tayler/Desktop/project/android/PizzzaApp/composeApp/src/androidMain/kotlin/com/tayler/pizzzaapp/ui/base/BaseActivity.kt)
Implementar una interfaz básica de carga y error en el `setContent` para que el usuario sepa si algo falló globalmente.

#### [MODIFICAR] [MainActivity.kt](file:///Users/tayler/Desktop/project/android/PizzzaApp/composeApp/src/androidMain/kotlin/com/tayler/pizzzaapp/ui/MainActivity.kt)
- Añadir una vista de "No hay pedidos" cuando `filteredOrders` esté vacío.
- Asegurar que el `LinearProgressIndicator` sea visible y claro.

---

### [Componente de Datos] - [shared]

#### [MODIFICAR] [Mappers.kt](file:///Users/tayler/Desktop/project/android/PizzzaApp/shared/src/commonMain/kotlin/com/tayler/pizzzaapp/manager/db/Mappers.kt)
Actualmente, `toModel()` pone `orders = emptyList()`. Como la base de datos local no guarda los productos individuales todavía, modificaremos la lógica en `DataNetwork` para que, si los datos locales no tienen productos, intente refrescar desde la red o al menos informe que faltan datos.

#### [MODIFICAR] [DataNetwork.kt](file:///Users/tayler/Desktop/project/android/PizzzaApp/shared/src/commonMain/kotlin/com/tayler/pizzzaapp/repository/network/DataNetwork.kt)
Ajustar la lógica de carga para priorizar datos completos.

## Plan de Verificación

### Pruebas Manuales
1. Abrir la app con internet para verificar la carga inicial.
2. Forzar un error de red (modo avión) para ver el mensaje de error.
3. Verificar que si no hay pedidos, aparezca el texto "No se encontraron pedidos".
