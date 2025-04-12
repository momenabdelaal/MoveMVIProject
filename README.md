# Mazaady Portal - Android Task

## Overview
Android app displaying movies from TMDb API with favorites and offline support.

## Features
- Movie list with grid/list toggle
- Favorites management
- Offline support
- Movie details view
- Error handling

## Architecture
- MVI Architecture
- Clean Architecture principles
- Single Activity pattern
- Repository pattern

## Tech Stack
- Kotlin
- Coroutines + Flow
- Paging 3
- Room Database
- Hilt DI
- Navigation Components
- ViewBinding

## Project Structure
```
app/
├── data/          # Data Layer
│   ├── db/        # Room Database
│   ├── model/     # Data Models
│   └── remote/    # API Service
├── domain/        # Business Logic
│   ├── model/     # Domain Models
│   └── usecase/   # Use Cases
└── presentation/  # UI Layer
    ├── home/      # Home Screen
    └── details/   # Details Screen
```

## Getting Started
1. Clone repository
2. Open in Android Studio
3. Run on emulator/device

## API
Using [FreeTestAPI Movies](https://www.freetestapi.com/apis/movies)


