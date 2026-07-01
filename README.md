# Open DualSpace

<p align="center">
  <strong>🔁 Clone Any App • 🚀 Run Dual Accounts • 🧊 Freeze to Save Resources</strong>
</p>

<p align="center">
  <a href="#features">Features</a> •
  <a href="#how-it-works">How It Works</a> •
  <a href="#installation">Installation</a> •
  <a href="#building">Building</a> •
  <a href="#contributing">Contributing</a> •
  <a href="#license">License</a>
</p>

---

## What is Open DualSpace?

Open DualSpace is a **free, open-source** Android app that lets you run **dual instances** of any installed app on your phone. Use two WhatsApp accounts, two Instagram accounts, or any app — simultaneously on one device.

Unlike other clone apps that use hacky virtualization or APK repackaging, Open DualSpace uses Android's **native Work Profile** system for rock-solid app isolation. This means:

- ✅ **No root required**
- ✅ **No APK modification**
- ✅ **Lightweight** — native OS-level isolation
- ✅ **Secure** — separate data storage per profile
- ✅ **Stable** — uses official Android APIs

## Features

| Feature | Description |
|---------|-------------|
| 🔁 **Clone Any App** | Browse your installed apps and clone them with one tap |
| 🚀 **Seamless Launch** | Cloned apps launch natively — no lag, no overhead |
| 🧊 **Freeze / Unfreeze** | Disable cloned apps when not in use to save battery & RAM |
| 🔄 **Background Toggle** | Keep cloned apps running or freeze them instantly |
| 🏠 **Home Screen Shortcuts** | Add cloned app shortcuts directly to your home screen |
| 📦 **Lightweight** | No virtualization overhead — uses native OS isolation |
| 🔒 **App Lock** | Optional PIN/biometric lock for privacy |
| 📊 **Resource Monitor** | Track RAM and storage usage per cloned app |
| 🗑️ **Easy Unclone** | Remove any cloned app with one tap |
| 🔔 **Notifications** | Full notification support for cloned apps |
| ⚡ **Auto-Start** | Optionally restore cloned apps on device boot |
| 🎨 **Beautiful UI** | White glassmorphism design with liquid glass animations |
| 🌙 **Dark Mode** | Full dark/light theme toggle |
| 📢 **Ad-Supported** | Free with Google AdMob integration |

## How It Works

Open DualSpace leverages Android's built-in **Work Profile** (Managed Profile) feature:

1. **Creates a Work Profile** — An isolated environment managed by Android OS
2. **Clones apps** — Installs apps into the Work Profile with separate data
3. **Manages lifecycle** — Freeze/unfreeze apps to control resource usage

This is the same technology used by enterprise MDM solutions, Samsung Secure Folder, and apps like [Shelter](https://github.com/PeterCxy/Shelter) and [Island](https://github.com/oasisfeng/island).

## Screenshots

*Coming soon*

## Installation

### From Play Store
*Coming soon*

### From APK
1. Download the latest APK from [Releases](https://github.com/yourusername/open-dualspace/releases)
2. Enable "Install from Unknown Sources" in your Android settings
3. Install the APK
4. Follow the setup wizard to create your DualSpace

### Requirements
- Android 7.0 (API 24) or higher
- No existing Work Profile on the device

## Building

### Prerequisites
- Android Studio Ladybug (2024.2) or newer
- JDK 17
- Android SDK with API 35

### Build Steps

```bash
# Clone the repository
git clone https://github.com/yourusername/open-dualspace.git
cd open-dualspace

# Build debug APK
./gradlew assembleDebug

# Install on connected device
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Project Structure
```
app/src/main/java/com/opendualspace/app/
├── data/                  # Data layer (models, repository, preferences)
├── domain/                # Business logic (Work Profile manager)
├── receiver/              # Broadcast receivers (Device Admin, Boot)
├── service/               # Foreground services (Clone operations)
├── ui/
│   ├── components/        # Reusable UI components
│   ├── navigation/        # Navigation graph
│   ├── screens/           # Screen composables + ViewModels
│   └── theme/             # Material 3 theme system
├── DualSpaceApp.kt        # Application class
└── MainActivity.kt        # Main entry point
```

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM + Clean Architecture
- **Core API:** Android DevicePolicyManager (Work Profile)
- **Storage:** DataStore Preferences
- **Ads:** Google AdMob
- **Min SDK:** Android 7.0 (API 24)

## Contributing

We welcome contributions! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Known Limitations

- Only **one** Work Profile can exist per device at a time
- If you already have a Work Profile (e.g., company MDM), this app cannot create another
- Some OEMs (Xiaomi MIUI, older Huawei EMUI) may have broken Work Profile support
- Banking apps and security-sensitive apps may refuse to run in a Work Profile
- Google Play Protect may show a warning during setup (this is normal for DPC apps)

## License

This project is licensed under the Apache License 2.0 — see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Inspired by [Shelter](https://github.com/PeterCxy/Shelter) and [Island](https://github.com/oasisfeng/island)
- Built with [Jetpack Compose](https://developer.android.com/jetpack/compose) and [Material 3](https://m3.material.io/)

---

<p align="center">
  Made with ❤️ for the open-source community
</p>
