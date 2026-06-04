# The Short News

An Android application that provides the latest news articles in a concise and easy-to-read format. Built with modern Android development practices, Jetpack Compose, and Material Design 3.

## Features

- **Latest News Feed**: Browse top news articles from around the world.
- **Search**: Search for specific topics or keywords using the Event Registry API.
- **Search History**: Quickly access your recent searches.
- **Pull-to-Refresh**: Easily refresh the news feed to get the latest updates.
- **Article Details**: View summaries and full content of news stories.
- **Optimized Image Loading**: Smooth and efficient image loading with advanced caching.
- **Google Fonts Integration**: Uses custom typography (Poppins) via Downloadable Fonts.

## Tech Stack

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern declarative UI toolkit.
- **Design System**: [Material Design 3](https://m3.material.io/) - Latest version of Google's design language.
- **Architecture**: MVVM (Model-View-ViewModel) - Clean separation of concerns.
- **Networking**: [Retrofit](https://square.github.io/retrofit/) & [Gson](https://github.com/google/gson) - Type-safe HTTP client and JSON parsing.
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/) - Kotlin-first image loading library.
- **Dependency Management**: [Gradle Version Catalog](https://developer.android.com/build/migrate-to-catalogs) - Centralized dependency management.
- **Navigation**: [Navigation Compose](https://developer.android.com/develop/ui/compose/navigation) - Navigation component for Compose.

## Architecture

The project follows the recommended Android Architecture components (MVVM):

- **UI Layer**: Composable functions (`NewsScreen`, `AppNavigation`) that observe state from ViewModels.
- **ViewModel Layer**: `NewsViewModel` manages the UI state and interacts with the Repository.
- **Repository Layer**: `NewsRepository` abstracts the data source (API) and manages search history.
- **Data Layer**: `NewsApiService` handles network requests using Retrofit.

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
- **Base URL**: `https://eventregistry.org/api/v1/`
- **Language**: Defaulted to English.
- **Sort**: Defaulted to latest articles.
