# Mazaady Movies App

## Overview
A modern Android application that showcases movies from the FreeTestAPI Movies service, featuring a clean and intuitive interface with offline support. Built using the latest Android development practices and libraries.

## Features
### Home Screen
- Display paginated list of movies with infinite scrolling
- Show movie poster, title, and release date
- Toggle between grid and list view layouts
- Pull-to-refresh functionality
- Offline caching of movies
- Search movies
- Preserve scroll position when returning from details

### Favorites Screen
- View and manage favorite movies
- Toggle between grid and list layouts
- Offline access to favorites
- Real-time sync with movie details
- Reflect favorite state in home list

### Details Screen
- Comprehensive movie information display including:
  - Overview
  - Genres
  - Runtime
  - Release date
  - Additional metadata
- Toggle favorite status
- Share movie information
- Back navigation with preserved list state

### Error Handling
- Proper error messages for:
  - API failures
  - No network connection
  - Empty states
  - Server errors
- Retry functionality
- Offline fallback to cached data

## Technical Implementation
### Architecture
- **MVI (Model-View-Intent)** pattern for unidirectional data flow
- **Clean Architecture** with three layers:
  - Data: API integration and local storage
  - Domain: Business logic and models
  - Presentation: UI components and ViewModels
- **Single Activity** pattern with Navigation Component
- **Repository Pattern** for data operations

### Tech Stack
- **Kotlin** - Primary development language
- **Coroutines + Flow** - Asynchronous programming
- **Jetpack Libraries**
  - Paging 3 - Handle paginated data
  - Room - Local database
  - Navigation - Screen navigation
  - ViewModel - UI state management
  - ViewBinding - View binding
- **Hilt** - Dependency injection
- **Retrofit** - API communication
- **Mockito** - Unit testing
- **Turbine** - Flow testing

### Project Structure
```
app/
├── data/                  # Data Layer
│   ├── db/               # Room Database
│   │   ├── dao/         # Data Access Objects
│   │   └── entity/      # Database Entities
│   ├── model/           # Data Models
│   ├── repository/      # Repository Implementations
│   └── remote/          # API Service
├── domain/               # Business Logic Layer
│   ├── model/           # Domain Models
│   ├── repository/      # Repository Interfaces
│   └── usecase/         # Use Cases
└── presentation/         # UI Layer
    ├── base/            # Base Classes
    ├── home/            # Home Screen
    ├── favorites/       # Favorites Screen
    └── details/         # Details Screen
```

## Testing
- **Unit Tests** for:
  - ViewModels
  - UseCases
  - Repositories

## Getting Started
1. Clone repository
2. Open in Android Studio
3. Run on emulator/device

## API
Using [FreeTestAPI Movies](https://www.freetestapi.com/apis/movies) for movie data.
