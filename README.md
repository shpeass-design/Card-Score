# Card Score Android App

Native Android score-keeping app written in Kotlin.

## Features
- Two teams with editable names
- Add (+) and subtract (-) score modes
- Winning target (default: 50), editable
- Player count, editable
- Round-by-round score history
- Undo restores the exact previous scores
- New game reset with confirmation
- Works offline

## GitHub upload: important folder structure

Upload the **contents** of this ZIP to the repository root. Keep the folders exactly as shown:

```text
Card-Score/
├── .github/
│   └── workflows/
│       └── build-apk.yml
├── app/
│   ├── build.gradle.kts
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           └── java/
│               └── com/
│                   └── example/
│                       └── cardscore/
│                           └── MainActivity.kt
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
└── README.md
```

Do not upload only the individual files from inside `app` or `.github`; their folder paths matter.

## Build APK with GitHub Actions
1. Create/open your GitHub repository.
2. Upload the extracted project contents, preserving the folder structure above, to the `main` branch.
3. Open the repository's **Actions** tab.
4. Select **Build Android APK** and run it (or wait for the push-triggered run).
5. When the workflow succeeds, open the run and download the `CardScore-debug-apk` artifact.
6. Extract the downloaded artifact ZIP. It contains `app-debug.apk`.

## Build locally
Requirements: JDK 17, Android SDK, Gradle 8.9 or compatible.
Run `gradle assembleDebug`.

APK output: `app/build/outputs/apk/debug/app-debug.apk`

This workflow creates a debug APK. A separate signing setup is needed for a Play Store release.
