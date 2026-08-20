# Permitir tráfico HTTP (Cleartext) para pruebas locales

Para que la aplicación pueda consumir servicios a través de HTTP (específicamente `http://127.0.0.1`), es necesario configurar el `networkSecurityConfig` en el módulo Android.

## Cambios Propuestos

### [Android App Module]

Resumen de los cambios en el módulo `:composeApp`.

#### [NEW] [network_security_config.xml](file:///Users/tayler/Desktop/project/android/PizzzaApp/composeApp/src/androidMain/res/xml/network_security_config.xml)
Crear este archivo para permitir explícitamente el tráfico sin cifrar para los dominios utilizados en desarrollo.

#### [MODIFY] [AndroidManifest.xml](file:///Users/tayler/Desktop/project/android/PizzzaApp/composeApp/src/androidMain/AndroidManifest.xml)
Añadir el atributo `android:networkSecurityConfig` en la etiqueta `<application>`.

## Plan de Verificación

### Pruebas Manuales
1. Ejecutar la aplicación en un emulador Android.
2. Verificar que las peticiones a `http://127.0.0.1:8080` (o `10.0.2.2` si se ajusta para el emulador) ya no fallen por errores de "Cleartext HTTP traffic not permitted".
