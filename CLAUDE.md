# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MoodyMusicForAndroid is a native Android application built with Kotlin and XML layouts. The app provides mood-based music recommendations using MVVM architecture with a custom base class framework.

**Technology Stack:**
- **Language**: Kotlin 2.1.0
- **UI Framework**: XML Layouts with DataBinding
- **Build System**: Gradle with Kotlin DSL
- **Architecture**: MVVM with generic base classes
- **Async**: Kotlin Coroutines
- **Networking**: Retrofit 2.9.0 + OkHttp + Gson
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 14)

## Build Commands

### Building the App
```bash
# Build debug APK
gradlew assembleDebug

# Build release APK
gradlew assembleRelease

# Clean build
gradlew clean

# Install debug build to connected device/emulator
gradlew installDebug

# Build and run tests
gradlew build
```

### Running Tests
```bash
# Run all unit tests
gradlew testDebugUnitTest

# Run specific test class
gradlew test --tests com.example.moodymusicforandroid.ExampleUnitTest

# Run instrumented tests (requires connected device/emulator)
gradlew connectedAndroidTest
```

## Architecture

### Generic Base Class Framework

The project uses a custom framework with generic base classes that reduce boilerplate:

**BaseViewModel** (`base/BaseViewModel.kt`):
- Provides unified network request wrapper with `request()` method
- Automatic loading state management (`LoadingState` sealed class)
- Built-in error handling with `errorMessage` and `toastMessage` LiveData
- Uses `viewModelScope` for coroutine management
- All ViewModels must inherit from `BaseViewModel`

**BaseActivity<VB : ViewDataBinding, VM : BaseViewModel>** (`base/BaseActivity.kt`):
- Generic class accepting ViewBinding type and ViewModel type
- Automatic ViewModel initialization via `getViewModelClass()`
- Automatic DataBinding setup with lifecycle owner
- Auto-observes ViewModel LiveData (toast, errors, loading state)
- Template methods: `initView()`, `initData()`, `setupBindingVariables()`
- Optional hooks: `showLoadingView()`, `hideLoadingView()`, `handleError()`

**BaseFragment<VB : ViewDataBinding, VM : BaseViewModel>** (`base/BaseFragment.kt`):
- Same generic pattern as BaseActivity for fragments

### Network Layer

**RetrofitClient** (`common/network/RetrofitClient.kt`):
- Singleton Retrofit client with OkHttp interceptor
- Base URL configuration
- Logging interceptor for debug builds

**ApiResponse Interface** (`common/network/ApiResponse.kt`):
- All API response models must implement this interface
- Methods: `isError()`, `getErrorMsg()`, `isSuccess()`

**MoodyApiProvider** (`data/api/MoodyApiProvider.kt`):
- Lazy-initialized API service singleton
- Helper method `getMediaUrl(path)` for media file URLs

### Package Structure

```
com.example.moodymusicforandroid/
├── base/                           # Base classes
│   ├── BaseActivity.kt             # Generic Activity with VB, VM
│   ├── BaseFragment.kt             # Generic Fragment with VB, VM
│   └── BaseViewModel.kt            # ViewModel with request wrapper
│
├── common/                         # Cross-cutting concerns
│   └── network/
│       ├── RetrofitClient.kt       # Retrofit singleton
│       ├── ApiResponse.kt          # Response interface
│       └── BaseResponse.kt         # Standard response wrapper
│
├── data/                           # Data layer
│   ├── api/
│   │   ├── MoodyApiProvider.kt     # API service provider
│   │   └── MoodyApiService.kt      # Retrofit interface
│   └── model/                      # Data models (Song, Artist, Album)
│
└── ui/                             # Presentation layer
    └── [feature]/                  # Feature-based packaging
        ├── activity/
        │   └── [Feature]Activity.kt
        └── viewmodel/
            └── [Feature]ViewModel.kt
```

## Standard Development Patterns

### Creating a New Feature Module

1. **Create ViewModel** inheriting `BaseViewModel`:

```kotlin
class MusicViewModel : BaseViewModel() {

    val musicList = MutableLiveData<List<Song>>()

    fun loadMusic() {
        request(isShowLoading = true) {
            val response = MoodyApiProvider.apiService.getMusicList()
            if (response.isSuccess()) {
                musicList.value = response.data
            }
            response
        }
    }
}
```

2. **Create Activity** inheriting `BaseActivity<VB, VM>`:

```kotlin
class MusicActivity : BaseActivity<ActivityMusicBinding, MusicViewModel>() {

    override fun getViewModelClass() = MusicViewModel::class.java

    override fun getLayoutId() = R.layout.activity_music

    override fun setupBindingVariables() {
        binding.viewModel = viewModel
    }

    override fun initView() {
        // Setup UI components
    }

    override fun initData() {
        viewModel.musicList.observe(this) { list ->
            // Update UI
        }
        viewModel.loadMusic()
    }
}
```

3. **Create Layout** with DataBinding:

```xml
<layout>
    <data>
        <variable
            name="viewModel"
            type="com.example.moodymusicforandroid.ui.music.MusicViewModel" />
    </data>

    <androidx.constraintlayout.widget.ConstraintLayout>
        <!-- UI components binding to viewModel -->
    </androidx.constraintlayout.widget.ConstraintLayout>
</layout>
```

### Network Request Pattern

All network requests must use the `request()` method from `BaseViewModel`:

```kotlin
protected fun <T : ApiResponse<*>> request(
    isShowLoading: Boolean = false,
    showErrorToast: Boolean = true,
    requestBlock: suspend CoroutineScope.() -> T
)
```

- Automatically handles loading state
- Catches exceptions and shows toast
- Processes API responses
- Uses `viewModelScope` for lifecycle awareness

For code already in a coroutine, use `suspendRequest()` instead.

## Dependency Management

Update `gradle/libs.versions.toml` to add dependencies:

1. Add version to `[versions]` section
2. Add library definition to `[libraries]` section
3. Reference in `app/build.gradle.kts` using `libs.library-name`

Example:
```toml
[versions]
coil = "2.5.0"

[libraries]
coil = { group = "io.coil-kt", name = "coil", version.ref = "coil" }
```

In `build.gradle.kts`:
```kotlin
implementation(libs.coil)
```

## Current Dependencies

**Core**: AndroidX Core KTX, AppCompat, Material, ConstraintLayout
**Lifecycle**: ViewModel, LiveData, Runtime
**Coroutines**: Core, Android
**Networking**: Retrofit, Gson converter, OkHttp, Logging interceptor
**Testing**: JUnit, AndroidX JUnit, Espresso

## Key Implementation Details

- **DataBinding must be enabled**: All activities use `useDataBinding() = true` by default
- **ApiResponse implementation**: All network response models must implement `ApiResponse` interface for automatic error handling
- **LiveData observation**: BaseActivity automatically observes `toastMessage`, `errorMessage`, and `loadingState`
- **Media URLs**: Use `MoodyApiProvider.getMediaUrl(path)` for media file URLs
- **Coroutine scopes**: Always use `request()` method which uses `viewModelScope`
- **Feature-based packaging**: UI code organized by feature (home, music, player, etc.)
