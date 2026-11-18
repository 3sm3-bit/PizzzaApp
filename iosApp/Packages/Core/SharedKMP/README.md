# SharedKMP

Aquí vive `Shared.xcframework`: el módulo Kotlin Multiplatform `shared`
compilado para iOS. Lo usa el target binario `Shared` de `Packages/Package.swift`,
de modo que cualquier módulo iOS puede hacer `import Shared`.

El `.xcframework` **no se versiona** (está en `.gitignore`); se genera con:

```sh
iosApp/scripts/build-shared-xcframework.sh          # Debug
iosApp/scripts/build-shared-xcframework.sh Release
```

El scheme `iosApp` lo ejecuta automáticamente como *pre-action* antes de cada build,
así que normalmente no hace falta correrlo a mano. Si cambias código Kotlin y
Xcode no lo detecta, ejecútalo y vuelve a compilar.
