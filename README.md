<div align="center">
  <img src="resources/shortcutlauncher.svg" width="100" alt="ShortcutLauncher">
  <img src="resources/quicklaunch.svg" width="100" alt="QuickLaunch">
  <h1>DAP Apps for HiBy M300</h1>
  <p>Two small Android apps that make the <strong>HiBy M300</strong> boot straight into your music player, while keeping a full launcher one tap away.</p>
  <img src="https://img.shields.io/badge/device-HiBy%20M300-blue" alt="Device">
  <img src="https://img.shields.io/badge/android-13%20(API%2033)-green" alt="Android">
</div>

## About

A DAP is for listening to music, so it should open the music player, not a home screen. **ShortcutLauncher** acts as the system Home and opens your player (e.g. [Poweramp](https://powerampapp.com/)) every time the device starts or you press Home. **QuickLaunch** adds Quick Settings tiles that open any app, so a full launcher (e.g. [Niagara Launcher](https://niagaralauncher.app/)) is always one swipe away.

Both apps share a dark monochrome UI with glass cards and gradients. They follow the system language: Spanish when it's set to Spanish, English otherwise.

## Features

### ShortcutLauncher

- **Boot into any app** — opens the app you choose on boot and on every Home press, then gets out of the way with no flash or animation
- **Loop-safe** — if the chosen app is uninstalled or fails to open, shows the settings screen instead of retrying
- **Startup delay** — optional 0–3 s wait on the first launch after boot, in case your music library isn't ready yet
- **Default Home toggle** — set or change the default Home from the app itself

### QuickLaunch

- **4 Quick Settings tiles** — each one opens the app you assign to it
- **Custom text** — rename any tile, or leave it empty to use the app name
- **Icon set** — 14 monochrome icons (home, play, music, headphones, equalizer, settings…) plus **Automatic**, which picks one based on the app
- **Hide tiles** — hidden tiles disappear from Quick Settings and its edit list; only tile 1 is shown on a fresh install
- **Live preview** — see how the tile will look while you edit it
- **Long-press to edit** — long-press any tile in Quick Settings to jump straight to its settings

## Download

Go to the [Releases page](https://github.com/Nivek-GP/dap-apps/releases) and download the APKs:

| App | File |
| -- | -- |
| ShortcutLauncher | `ShortcutLauncher-x.x.x.apk` |
| QuickLaunch | `QuickLaunch-x.x.x.apk` |

## Installation

1. On the M300, enable **Developer options** and **USB debugging**
2. Connect it and check that it shows up with `adb devices`
3. Install both apps:

```bash
adb install -r ShortcutLauncher-x.x.x.apk
adb install -r QuickLaunch-x.x.x.apk
```

> You can also copy the APKs to the device and open them from a file manager, after allowing it to install unknown apps.

## Setup

### ShortcutLauncher

1. Open **ShortcutLauncher** from your app drawer
2. **Startup app** — choose your music player
3. **Default Home** — tap **Set as default Home** and confirm
4. **Startup delay** — optional, only if the player opens before your library is ready

### QuickLaunch

1. Open **QuickLaunch** and tap **Tile 1**
2. **App** — choose your full launcher (e.g. Niagara)
3. **Text**, **Icon** and **Visibility** — adjust them if you like; enable more tiles from their **Visibility** switch
4. Pull down Quick Settings, tap the pencil and drag the **QuickLaunch** tiles into the active area

> **Using Niagara Launcher?** When it isn't the default Home, Niagara keeps asking to be set as default. Turn that prompt off with its secret command `/suppress set launcher prompt`, or by opening [nlaun.ch/c/suppresssetlauncherprompt](https://nlaun.ch/c/suppresssetlauncherprompt) on the device ([source](https://help.niagaralauncher.app/article/102-launcher-not-staying-as-default)).

## Usage

- **Power on or press Home** — your music player opens
- **Tap a tile** — its app opens; pressing Home brings you back to the player
- **Long-press a tile** — opens that tile's settings. If your ROM ignores long-press, open QuickLaunch from your launcher's app drawer instead
- **Change the startup app** — open ShortcutLauncher from your launcher, or assign it to a tile

## Project structure

| Module | Contents |
| -- | -- |
| `core` | Shared dark theme, drawables and app picker (Android library) |
| `shortcutlauncher` | ShortcutLauncher app (`dev.kevin.shortcutlauncher`) |
| `quicklaunch` | QuickLaunch app (`dev.kevin.quicklaunch`) |

Stack: Kotlin, Gradle 9.8, AGP 9.4 (built-in Kotlin), `compileSdk` 37, `minSdk`/`targetSdk` 33.

## Building from source

Requires [Android Studio](https://developer.android.com/studio), or just its bundled JDK and the Android SDK.

```bash
export JAVA_HOME="/c/Program Files/Android/Android Studio/jbr"  # Android Studio's JDK on Windows
./gradlew assembleRelease  # APKs in */build/outputs/apk/release/
./gradlew assembleDebug    # debug builds
```

> Release APKs are signed with the debug key, which is enough for sideloading on your own device.

## Related

- [Poweramp](https://powerampapp.com/) — the music player this setup was built around
- [Niagara Launcher](https://niagaralauncher.app/) — the full launcher reached from QuickLaunch
