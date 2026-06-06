# The Short News

An Android application that provides the latest news articles in a concise and easy-to-read format. Built with modern Android development practices, Jetpack Compose, and Material Design 3.

## Features

- **Latest News Feed**: Browse top news articles from around the world.
- **Search**: Search for specific topics or keywords using the Event Registry API.
- **Search History**: Quickly access your recent searches.
- **Pull-to-Refresh**: Easily refresh the news feed to get the latest updates.
- **Article Details**: View summaries and full content of news stories.
- **Background Sync**: Periodic background updates to keep news fresh using WorkManager.
- **Optimized Image Loading**: Smooth and efficient image loading with advanced caching.
- **Google Fonts Integration**: Uses custom typography (Poppins) via Downloadable Fonts.

## Tech Stack

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern declarative UI toolkit.
- **Background Work**: [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) - Robust background task scheduling.
- **Design System**: [Material Design 3](https://m3.material.io/) - Latest version of Google's design language.
- **Architecture**: MVVM (Model-View-ViewModel) - Clean separation of concerns.
- **Networking**: [Retrofit](https://square.github.io/retrofit/), [OkHttp](https://square.github.io/okhttp/) & [Gson](https://github.com/google/gson) - Type-safe HTTP client, interceptors, and JSON parsing.
- **Logging**: [OkHttp Logging Interceptor](https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor) - HTTP request and response logging for debugging.
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/) - Kotlin-first image loading library.
- **Dependency Management**: [Gradle Version Catalog](https://developer.android.com/build/migrate-to-catalogs) - Centralized dependency management.
- **Navigation**: [Navigation Compose](https://developer.android.com/develop/ui/compose/navigation) - Navigation component for Compose.

## Architecture

The project follows the recommended Android Architecture components (MVVM):

- **UI Layer**: Composable functions (`NewsScreen`, `AppNavigation`) that observe state from ViewModels.
- **ViewModel Layer**: `NewsViewModel` manages the UI state and interacts with the Repository.
- **Worker Layer**: `SyncWorker` handles periodic background data synchronization using WorkManager.
- **Repository Layer**: `NewsRepository` abstracts data sources and manages news data.
- **Data Layer**: `NewsApiService` handles network requests using Retrofit, supported by OkHttp interceptors for authentication and logging.

## Networking & API Configuration

The application's networking layer is built with Retrofit and OkHttp, featuring:

- **Authentication Interceptor**: Automatically appends the required `apiKey` query parameter to every request, ensuring secure and consistent API access without manual parameter passing in service methods.
- **HTTP Logging**: Integrated `HttpLoggingInterceptor` (Body level) to monitor network traffic, request headers, and response payloads during development.
- **Base URL**: `https://eventregistry.org/api/v1/`
- **Error Handling**: Graceful handling of network failures with offline support via Room database (where implemented).

## Image Optimizations

To ensure a smooth user experience, the app includes several image optimizations:

- **Custom ImageLoader**: Configured globally in `MainApplication`.
- **Memory Caching**: 25% of available memory allocated for image caching.
- **Disk Caching**: Persistent disk cache for offline access and faster loading on subsequent launches.
- **Crossfade Transitions**: Smooth visual transitions when images load.
- **Accessibility**: All images include relevant content descriptions.

## Getting Started

1. Clone the repository.
2. Open the project in Android Studio (Ladybug or newer).
3. Ensure you have an internet connection for downloading dependencies and fetching news.
4. Run the `:app` module on an emulator or physical device.

## API Usage

This app uses the [Event Registry API](https://eventregistry.org/) for news content.
- **Language**: Defaulted to English.
- **Sort**: Defaulted to latest articles.
- **Key Features**: Keyword search, pagination support, and detailed article metadata.
