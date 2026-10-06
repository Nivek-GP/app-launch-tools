# dap-apps

Dos apps Android pequeñas para el HiBy M300 (Android 13):

- **ShortcutLauncher**: un launcher "dummy". Cuando Android pide el Home (al arrancar o al pulsar Home), abre la app configurada (p. ej. Poweramp) y se cierra.
- **QuickLaunch**: 4 tiles para Ajustes rápidos. Cada uno abre la app que le asignes (p. ej. Niagara Launcher).

Ambas usan un tema oscuro monocromo y se muestran en español o inglés según el idioma del sistema. Si el sistema está en otro idioma, se usa inglés.

## Estructura

```
core/              tema, drawables y selector de apps compartidos (librería)
shortcutlauncher/  app 1 (dev.kevin.shortcutlauncher)
quicklaunch/       app 2 (dev.kevin.quicklaunch)
```

## Compilar

Stack: Gradle 9.8, AGP 9.4 (con Kotlin integrado), compileSdk 37, minSdk/targetSdk 33.

1. Abre esta carpeta en Android Studio (File > Open), o compila desde la terminal con la JDK de Android Studio:
   ```
   export JAVA_HOME="/c/Program Files/Android/Android Studio/jbr"
   ./gradlew :shortcutlauncher:assembleRelease :quicklaunch:assembleRelease
   ```
   Los APK quedan en `*/build/outputs/apk/release/`. Están firmados con la clave debug, lo justo para instalarlos por sideload.

## Instalar en el M300

1. En el M300, activa las Opciones de desarrollador y la Depuración USB.
2. Comprueba la conexión con `adb devices`.
3. Instala las dos apps:
   ```
   adb install -r shortcutlauncher/build/outputs/apk/release/shortcutlauncher-release.apk
   adb install -r quicklaunch/build/outputs/apk/release/quicklaunch-release.apk
   ```

## Configuración inicial

**ShortcutLauncher**
1. Abre ShortcutLauncher desde el cajón de apps.
2. En *App de arranque*, elige Poweramp.
3. Pulsa *Usar como Home por defecto* y acepta.
4. Opcional: activa el *Retraso al arrancar* (0–3 s) si Poweramp abre antes de que la biblioteca esté lista. Solo se aplica en el primer inicio tras encender.

**QuickLaunch**
1. Abre QuickLaunch y asigna apps a los tiles. Sugerencia: Tile 1 = Niagara, Tile 2 = ShortcutLauncher.
2. Despliega Ajustes rápidos, toca el lápiz y arrastra los tiles *QuickLaunch* al área activa.

## Uso diario

- Al encender el M300 o pulsar Home, se abre Poweramp.
- Para ir a Niagara, toca su tile. Al pulsar Home volverás a Poweramp.
- Para reasignar los tiles, **mantén pulsado** cualquier tile de QuickLaunch. Si la ROM no respeta la pulsación larga, abre QuickLaunch desde el cajón de Niagara.
- Para cambiar la app de arranque, usa el tile de ShortcutLauncher o abre ShortcutLauncher desde Niagara.
- Si la app de arranque se desinstala, el Home abre la configuración en lugar de quedarse en bucle.

## Verificación rápida

```
adb shell wm size                       # resolución real del M300
adb reboot                              # debe abrir Poweramp directamente
adb logcat | grep -iE "shortcutlauncher|quicklaunch"
```
