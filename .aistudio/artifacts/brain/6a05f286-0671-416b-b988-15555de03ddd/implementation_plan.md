# GitHub Actions CI Workflow Fix & Verification

Comprehensive fix and verification plan for the BudSwipe automated build pipeline in GitHub Actions, ensuring reliable execution on Ubuntu runners and successful publishing of the Debug APK.

### User Review & Critical Decisions

> [!IMPORTANT]
> The following operational choices were confirmed during the interactive clarification:
> - **Artifact Scope**: The workflow will focus on compiling, verifying, and uploading the **Debug APK only** (`app/build/outputs/apk/debug/app-debug.apk`).
> - **Gradle Runner Execution**: The pipeline will integrate the official **`gradle/actions/setup-gradle@v4`** action to handle Gradle environment provisioning, dependency caching, and build-cache optimization natively on GitHub runners.

- **Confirmed Decision 1**: Build and publish `budswipe-debug-apk` on pushes and pull requests to `main` and `master`, plus manual triggers (`workflow_dispatch`).
- **Confirmed Decision 2**: Provision Gradle through `gradle/actions/setup-gradle@v4` with version pinning (`gradle-version: 9.3.1` or wrapper resolution), eliminating runner dependency on system-installed Gradle.

---

### 1. Overview & Core Concept

- **What It Does**: Automates Android validation, running unit/Robolectric tests, compiling the Android application, and publishing the installable `app-debug.apk` as a downloadable GitHub Actions artifact on every push, pull request, or manual dispatch.
- **Root Causes of Past Failure**:
  1. **Missing `gradlew` wrapper script**: The workflow previously attempted `chmod +x gradlew || true`, but `gradlew` did not exist in the root directory.
  2. **Unmet system Gradle dependency**: The workflow called `gradle testDebugUnitTest` and `gradle assembleDebug`, which fails on standard GitHub Ubuntu runners because `gradle` is not installed on the system PATH by default.
  3. **Incomplete Caching Pipeline**: `actions/setup-java@v4` cache option was used alone without the specialized Gradle build cache integration provided by `gradle/actions/setup-gradle`.
- **Target Value**: Zero-touch continuous integration delivering a verified APK artifact ready to install on Android devices.

---

### 2. User Experience & Visual Design (Developer Experience)

- **Workflow Triggering**:
  - Automatically runs on branch pushes to `main` or `master`.
  - Automatically runs on pull requests targeting `main` or `master`.
  - Can be manually executed anytime via GitHub's "Run workflow" UI (`workflow_dispatch`).
- **Execution Feedback & Telemetry**:
  - Step 1: Source checkout (`actions/checkout@v4`).
  - Step 2: JDK 17 setup (`actions/setup-java@v4` with Temurin distribution).
  - Step 3: Gradle environment & cache setup (`gradle/actions/setup-gradle@v4`).
  - Step 4: Unit & Robolectric test execution (`testDebugUnitTest`).
  - Step 5: Debug APK assembly (`assembleDebug`).
  - Step 6: Artifact upload (`actions/upload-artifact@v4` storing `app-debug.apk` under `budswipe-debug-apk`).

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Use `gradle/actions/setup-gradle@v4` vs Relying on pre-installed runner binaries**
  - *Chosen Approach*: Use `gradle/actions/setup-gradle@v4` paired with JDK 17.
  - *Why*: Standard GitHub Actions `ubuntu-latest` virtual environments do not guarantee a pre-installed `gradle` binary matching AGP 9.1 / Gradle 9.3.1. `setup-gradle` downloads and configures the exact compatible Gradle version, handles build-cache keys, and logs build scans cleanly.
  - *Alternatives Considered*: Writing a custom shell script to `apt-get install gradle` (unsupported for modern Gradle 9.3+ and AGP 9.1).

- **Decision 2: Generate and commit standard `gradlew` wrapper scripts**
  - *Chosen Approach*: Generate `gradlew` and `gradlew.bat` in the workspace root matching `gradle/wrapper/gradle-wrapper.properties` (Gradle 9.3.1).
  - *Why*: Ensures local developers and CI environments can run `./gradlew` seamlessly without requiring manual Gradle installation on any machine.

---

### 4. Technical Architecture & CI Pipeline

```
┌────────────────────────────────────────────────────────┐
│               GitHub Actions Runner                    │
│                  (ubuntu-latest)                       │
└───────────────────────────┬────────────────────────────┘
                            │
                            ▼
         ┌──────────────────────────────────────┐
         │      1. actions/checkout@v4          │
         └──────────────────┬───────────────────┘
                            │
                            ▼
         ┌──────────────────────────────────────┐
         │      2. actions/setup-java@v4        │
         │         (JDK 17 Temurin)             │
         └──────────────────┬───────────────────┘
                            │
                            ▼
         ┌──────────────────────────────────────┐
         │   3. gradle/actions/setup-gradle@v4  │
         │   (Gradle 9.3.1 + Smart Caching)     │
         └──────────────────┬───────────────────┘
                            │
                            ▼
         ┌──────────────────────────────────────┐
         │ 4. Unit & Robolectric Tests          │
         │    (./gradlew testDebugUnitTest)     │
         └──────────────────┬───────────────────┘
                            │
                            ▼
         ┌──────────────────────────────────────┐
         │ 5. Compile Debug APK                 │
         │    (./gradlew assembleDebug)         │
         └──────────────────┬───────────────────┘
                            │
                            ▼
         ┌──────────────────────────────────────┐
         │   6. actions/upload-artifact@v4      │
         │      Output: app-debug.apk           │
         └──────────────────────────────────────┘
```

#### Step-by-Step Implementation Sequence:
1. **Generate Gradle Wrapper**: Execute `gradle wrapper --gradle-version 9.3.1 --distribution-type bin` to create official `gradlew`, `gradlew.bat`, and `gradle/wrapper/gradle-wrapper.jar`.
2. **Update Workflow File**: Rewrite `.github/workflows/build-apk.yml` with:
   - Modern `actions/checkout@v4`
   - `actions/setup-java@v4` (Java 17)
   - `gradle/actions/setup-gradle@v4`
   - Execution commands targeting `./gradlew testDebugUnitTest` and `./gradlew assembleDebug`
   - `actions/upload-artifact@v4` targeting `app/build/outputs/apk/debug/app-debug.apk`
3. **Local Pipeline Verification**: Run local test validation and assemble verification before committing changes.
