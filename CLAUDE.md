# FatRobin - PERT Dosing Calculator

## Project Overview

FatRobin is a precision calculator for people managing pancreatic enzyme replacement therapy (PERT). It's a Kotlin Multiplatform mobile application that helps users calculate enzyme pill dosages for their meals without guesswork or math errors.

**Purpose**: Calculate how many enzyme pills are needed for portions, packages, or food items based on fat content and weight.

## Technology Stack

### Core Technologies
- **Kotlin Multiplatform** (2.0.21) - Shared business logic across platforms
- **Jetpack Compose** (1.8.1) - Modern Android UI framework
- **Compose Material 3** - Material Design 3 UI components with dynamic theming
- **Android Gradle Plugin** (8.10.1) - Build system

### Platform Support
- ✅ **Android** - Primary target (API 24+, target SDK 35)
- 🚧 **iOS** - In development (Kotlin/Native ready)

### Build Tools & Code Quality
- **Gradle Kotlin DSL** - Build configuration
- **Spotless** (6.25.0) - Code formatting with ktlint
- **ktlint** (1.0.1) - Kotlin code style enforcement

## Architecture

### Project Structure
```
src/
├── commonMain/          # Shared business logic
│   └── kotlin/
│       └── app.pmsoft.fatrobin/
│           ├── FatRobinApp.kt           # Main UI composable
│           ├── FatRobinCalculator.kt    # Core calculation engine
│           └── ui/theme/Theme.kt        # Common theming
├── androidMain/         # Android-specific code
│   ├── kotlin/
│   │   └── app.pmsoft.fatrobin/
│   │       ├── MainActivity.kt          # Android entry point
│   │       ├── MyApplication.kt         # Application class
│   │       └── ui/theme/AndroidFatRobinTheme.kt  # Material You theming
│   ├── AndroidManifest.xml             # Android app configuration
│   └── res/                            # Android resources
├── iosMain/             # iOS-specific code (in development)
│   └── kotlin/
│       └── app.pmsoft.fatrobin/
│           └── MainViewController.kt    # iOS entry point
└── commonTest/          # Shared tests
    └── kotlin/
        └── app.pmsoft.fatrobin/
            ├── FatRobinCalculatorTest.kt  # Unit tests
            └── FatRobinAppTest.kt         # App tests
```

### Key Components

#### FatRobinCalculator.kt
Core calculation engine that handles:
- Multiple input methods (direct weight, package division, food items)
- Pill dosage calculations for different enzyme strengths
- Auto-calculation of interdependent values
- Validation and null safety

#### FatRobinApp.kt
Main UI composable featuring:
- Real-time calculation results
- Visual connection system showing field dependencies
- Material 3 design with dynamic theming
- Multiple input methods with smart validation

### Calculation Methods

The app supports 3 calculation approaches:

1. **Direct Weight**: "This portion weighs 300g. How many pills do I need?"
2. **Package Division**: "This package weighs 800g and has 4 servings. What's the dose per serving?"
3. **Food Items**: "I eat 1 slice at a time. What's the dose per slice?"

## Development Commands

### Build & Run
```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Install debug APK on connected device
./gradlew installDebug

# Run Android lint
./gradlew lint

# Check code formatting
./gradlew spotlessCheck

# Apply code formatting
./gradlew spotlessApply
```

### Code Quality
```bash
# Run all checks (tests + lint + formatting)
./gradlew check

# Fix code formatting issues
./gradlew spotlessApply

# Run specific test class
./gradlew testDebugUnitTest --tests "*FatRobinCalculatorTest*"
```

### Build Variants
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Build AAB (Android App Bundle)
./gradlew bundleRelease
```

## Key Features

### Calculation Features
- Multiple pill strengths support (10k, 35k units out of the box)
- Automatic rounding (up for pills, down for fat coverage)
- Real-time results as you type
- Smart auto-completion between related fields

### UI Features
- Material 3 design with dynamic theming
- Visual branching connection system showing field dependencies
- Responsive layout with scrollable interface
- Instant results table with multiple calculation modes

### Technical Features
- Offline operation (no internet required)
- Shared business logic across platforms
- Comprehensive unit test coverage
- Modern Android development practices

## Code Style & Standards

### Formatting Rules
- **Indent size**: 2 spaces
- **Continuation indent**: 2 spaces
- **Function naming**: Disabled ktlint rule (allows custom naming)
- **Trailing whitespace**: Trimmed
- **End with newline**: Enforced

### Code Organization
- Explicit imports (no wildcards)
- Material 3 theming with system dynamic colors
- Compose-first UI architecture
- Null-safe calculation logic

## Testing Strategy

### Test Coverage
- **FatRobinCalculatorTest.kt**: Comprehensive unit tests for calculation engine
  - Basic calculations
  - Edge cases (zero fat, large portions)
  - Decimal handling
  - Auto-calculation logic
  - Input validation

### Test Commands
```bash
# Run all tests
./gradlew test

# Run debug unit tests only
./gradlew testDebugUnitTest

# Run with verbose output
./gradlew test --info
```

## Build Configuration

### Gradle Properties
- **Kotlin code style**: Official
- **AndroidX**: Enabled
- **Jetifier**: Enabled
- **JVM Args**: -Xmx2048m optimized for performance
- **Parallel builds**: Enabled

### Android Configuration
- **Application ID**: app.pmsoft.fatrobin
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 35
- **Java Version**: 11

## Deployment

### Release Preparation
```bash
# Check code quality
./gradlew spotlessCheck lint

# Run all tests
./gradlew test

# Build release
./gradlew assembleRelease
```

### Current Status
- **Android**: Production ready
- **iOS**: Development in progress (Kotlin Multiplatform foundation complete)

## Important Notes

### Medical Disclaimer
FatRobin is a dosing calculator tool, not medical advice. Users should always follow their healthcare provider's instructions.

### Known Issues
- Kotlin MPP ↔ Android Gradle Plugin compatibility warning (suppressed in gradle.properties)
- iOS implementation pending completion

This is a well-architected, modern Android application with Kotlin Multiplatform foundation, comprehensive testing, and production-ready code quality standards.
