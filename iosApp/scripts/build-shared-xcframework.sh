#!/bin/sh
#
# Compila el módulo Kotlin Multiplatform `shared` como XCFramework y lo copia
# dentro del paquete Swift local (iosApp/Packages/Core/SharedKMP), que es de
# donde lo consumen los módulos iOS (`import Shared`).
#
# Uso (desde cualquier carpeta):
#   iosApp/scripts/build-shared-xcframework.sh            # Debug (por defecto)
#   iosApp/scripts/build-shared-xcframework.sh Release
#
# Xcode lo ejecuta también como "pre-action" del scheme iosApp, con la
# configuración activa ($CONFIGURATION).
#
set -e

CONFIG="${1:-${CONFIGURATION:-Debug}}"
case "$CONFIG" in
  Release*) VARIANT="Release"; OUT_DIR="release" ;;
  *)        VARIANT="Debug";   OUT_DIR="debug" ;;
esac

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
IOS_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
ROOT_DIR="$(cd "$IOS_DIR/.." && pwd)"
DEST_DIR="$IOS_DIR/Packages/Core/SharedKMP"

if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
  echo "Skipping Gradle build (OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED=YES)"
  exit 0
fi

echo "▶︎ Compilando Shared.xcframework ($VARIANT)…"
cd "$ROOT_DIR"
./gradlew ":shared:assembleShared${VARIANT}XCFramework"

SRC="$ROOT_DIR/shared/build/XCFrameworks/$OUT_DIR/Shared.xcframework"
if [ ! -d "$SRC" ]; then
  echo "error: no se encontró $SRC" >&2
  exit 1
fi

mkdir -p "$DEST_DIR"
rm -rf "$DEST_DIR/Shared.xcframework"
cp -R "$SRC" "$DEST_DIR/Shared.xcframework"
echo "✔︎ Shared.xcframework copiado en $DEST_DIR"
