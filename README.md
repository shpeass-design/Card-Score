# Card Score Android App

A native Android app written in Kotlin.

## Features
- Two teams with editable names
- Add and subtract score modes
- Configurable winning target (default 50)
- Configurable player count
- Round-by-round score history
- Undo restores the exact previous scores
- New game reset with confirmation
- Offline use

## Build APK using GitHub Actions
1. Create a GitHub repository and upload the contents of this project.
2. Push the files to the `main` branch.
3. Open the repository's **Actions** tab.
4. Wait for **Build Android APK** to finish successfully.
5. Open the workflow run, then download the `CardScore-debug-apk` artifact.
6. Extract the artifact ZIP to get `app-debug.apk`.

## Build locally
Requirements: JDK 17, Android SDK, Gradle 8.9.
Run:
```bash
gradle assembleDebug
```
APK output:
`app/build/outputs/apk/debug/app-debug.apk`

The APK is a debug build and has not been signed for Play Store release.
