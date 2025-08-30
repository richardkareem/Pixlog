# 📸 Pixlog - Story Sharing Android App

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Retrofit](https://img.shields.io/badge/Retrofit-2.0.0-blue?style=for-the-badge)](https://square.github.io/retrofit/)
[![Jetpack](https://img.shields.io/badge/Jetpack-007ACC?style=for-the-badge&logo=jetpack-compose&logoColor=white)](https://developer.android.com/jetpack)

A modern Android application for sharing stories and photos with friends and family. Built with the latest Android development technologies and best practices.

## ✨ Features

### 🔐 Authentication
- **User Registration & Login** - Secure authentication system
- **Token-based Security** - JWT token management with DataStore
- **Session Management** - Automatic token refresh and validation

### 📱 Core Functionality
- **Story Feed** - Infinite scrolling story timeline with pagination
- **Photo Upload** - Camera and gallery integration for story creation
- **Real-time Updates** - Live data synchronization
- **Offline Support** - Data caching and offline-first approach

### 🎨 User Experience
- **Modern Material Design** - Beautiful and intuitive UI
- **Smooth Animations** - Shared element transitions and micro-interactions
- **Responsive Layout** - Optimized for various screen sizes
- **Dark/Light Theme** - Automatic theme switching support

### 🔧 Technical Features
- **MVVM Architecture** - Clean and maintainable code structure
- **Repository Pattern** - Centralized data management
- **Dependency Injection** - Modular and testable architecture
- **Coroutines & Flow** - Asynchronous programming with Kotlin

## 🛠️ Technology Stack

### **Frontend & UI**
- **Kotlin** - Primary programming language
- **XML Layouts** - Traditional Android view system
- **ViewBinding** - Type-safe view binding
- **Material Design Components** - Modern UI components
- **Navigation Component** - Fragment-based navigation

### **Architecture & Patterns**
- **MVVM (Model-View-ViewModel)** - Clean architecture pattern
- **Repository Pattern** - Data access abstraction
- **LiveData** - Observable data holders
- **ViewModel** - UI-related data management

### **Networking & Data**
- **Retrofit** - HTTP client for API communication
- **OkHttp** - HTTP client with logging and interceptors
- **Gson** - JSON serialization/deserialization
- **Paging 3** - Efficient list pagination

### **Local Storage**
- **DataStore** - Modern data storage solution
- **SharedPreferences Migration** - Legacy data compatibility

### **Image Handling**
- **Glide** - Fast and efficient image loading
- **CameraX** - Modern camera integration
- **Image Compression** - Optimized file sizes

### **Development Tools**
- **Gradle** - Build automation
- **Safe Args** - Type-safe navigation
- **ProGuard** - Code obfuscation and optimization

## 📱 Screenshots

<div align="center">
  <img src="app/src/main/res/drawable-nodpi/example_appwidget_preview.png" width="200" alt="App Screenshot 1"/>
  <img src="app/src/main/res/drawable-nodpi/example_appwidget_preview.png" width="200" alt="App Screenshot 2"/>
  <img src="app/src/main/res/drawable-nodpi/example_appwidget_preview.png" width="200" alt="App Screenshot 3"/>
</div>

## 🚀 Getting Started

### Prerequisites
- **Android Studio** Arctic Fox or later
- **Android SDK** API level 33 (Android 13) or higher
- **Java Development Kit (JDK)** 11 or higher
- **Kotlin** 1.8.0 or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/pixlog.git
   cd pixlog
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory and select it

3. **Configure API Endpoint**
   - Open `app/build.gradle.kts`
   - Update `BASE_URL` in buildConfigField if needed:
   ```kotlin
   buildConfigField("String", "BASE_URL", "\"https://your-api-endpoint.com/\"")
   ```

4. **Build and Run**
   - Connect an Android device or start an emulator
   - Click the "Run" button (▶️) in Android Studio
   - Select your target device and wait for the app to install

### Build Variants

- **Debug** - Development build with logging enabled
- **Release** - Production build with ProGuard optimization

## 🏗️ Project Structure

```
app/
├── src/main/
│   ├── java/com/richard/pixlog/
│   │   ├── data/                    # Data layer
│   │   │   ├── di/                  # Dependency injection
│   │   │   ├── local/               # Local data sources
│   │   │   ├── remote/              # Remote data sources
│   │   │   └── repository/          # Repository implementations
│   │   ├── ui/                      # UI layer
│   │   │   ├── component/           # Reusable UI components
│   │   │   ├── screen/              # Screen implementations
│   │   │   └── widget/              # App widgets
│   │   ├── utils/                   # Utility classes
│   │   └── MainActivity.kt          # Main activity
│   ├── res/                         # Resources
│   └── AndroidManifest.xml          # App manifest
├── build.gradle.kts                 # App-level build configuration
└── proguard-rules.pro              # ProGuard rules
```

## 🔧 Configuration

### API Configuration
The app uses a centralized API configuration in `ApiConfig.kt`:
- **Base URL** - Configurable via build.gradle.kts
- **Authentication** - Automatic token injection
- **Logging** - HTTP request/response logging in debug builds
- **Timeouts** - Configurable connection and read timeouts

### Build Configuration
Key build configurations in `build.gradle.kts`:
- **Min SDK**: 33 (Android 13)
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 35
- **Java Version**: 11

## 📊 Key Features Implementation

### Pagination
```kotlin
fun getAllStory(): LiveData<PagingData<ListStory>> {
    return Pager(
        config = PagingConfig(pageSize = 5),
        pagingSourceFactory = { HomePagingSource(apiService) }
    ).liveData
}
```

### Image Upload
```kotlin
@Multipart
@POST("stories")
suspend fun addStory(
    @Part file: MultipartBody.Part,
    @Part("description") description: RequestBody
): UploadResponse
```

### Authentication
```kotlin
val authInterceptor = Interceptor { chain ->
    val token = runBlocking { loginPreferences.getToken().first() }
    val request = if (token.isNullOrEmpty()) {
        original
    } else {
        original.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
    }
    chain.proceed(request)
}
```

## 🧪 Testing

The project includes comprehensive testing setup:
- **Unit Tests** - Business logic testing
- **Instrumented Tests** - UI and integration testing
- **Test Dependencies** - JUnit, Espresso, and Mockito

## 📦 Dependencies

### Core Dependencies
- **AndroidX Core KTX** - Kotlin extensions
- **AndroidX AppCompat** - Backward compatibility
- **Material Design** - Material Design components
- **ConstraintLayout** - Flexible layouts

### Architecture Components
- **ViewModel** - UI-related data management
- **LiveData** - Observable data holders
- **Navigation Component** - Fragment navigation
- **Lifecycle** - Lifecycle-aware components

### Networking
- **Retrofit** - HTTP client
- **OkHttp** - HTTP client implementation
- **Logging Interceptor** - HTTP request logging

### Image & Media
- **Glide** - Image loading library
- **CameraX** - Camera functionality
- **CircleImageView** - Circular image views

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/AmazingFeature`)
3. **Commit your changes** (`git commit -m 'Add some AmazingFeature'`)
4. **Push to the branch** (`git push origin feature/AmazingFeature`)
5. **Open a Pull Request**

### Contribution Guidelines
- Follow Kotlin coding conventions
- Add tests for new functionality
- Update documentation as needed
- Ensure all tests pass before submitting

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Dicoding Indonesia** - For providing the API endpoints and project requirements
- **Android Developer Community** - For continuous support and resources
- **Open Source Contributors** - For the amazing libraries that make this possible

## 📞 Contact

- **Developer**: Richard Kareem
- **Email**: your.email@example.com
- **LinkedIn**: [Your LinkedIn Profile]
- **GitHub**: [@yourusername](https://github.com/yourusername)

## 🌟 Star History

[![Star History Chart](https://api.star-history.com/svg?repos=yourusername/pixlog&type=Date)](https://star-history.com/#yourusername/pixlog&Date)

---

<div align="center">
  <p>Made with ❤️ by Richard Kareem</p>
  <p>If you find this project helpful, please give it a ⭐</p>
</div>
