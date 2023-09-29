# Weather App

![Weather App Logo](app_logo.png)

The Weather App is a simple Android application built using Kotlin that provides real-time weather information for cities around the world. It follows the Model-View-ViewModel (MVVM) architectural pattern and utilizes various Android libraries and components to deliver a user-friendly experience. This app was developed as part of the final project for university "Mobile App Programming" course.

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Screenshots](#screenshots)
- [Contributing](#contributing)


## Features

- **Real-time Weather Data**: Retrieve real-time weather information for a specified city, including temperature, humidity, wind speed, and more.

- **Search by City**: Enter the name of the city you want to get weather information for.

- **Location-Based Weather**: Use your device's location to automatically fetch weather data for your current location.

- **Historical Search**: Save a history of searched cities and their search dates.

- **Clear History**: Clear your search history with one tap.

## Technologies Used

- **Kotlin**: The primary programming language used for Android app development.

- **MVVM Architecture**: Follows the Model-View-ViewModel architectural pattern for a clean and organized codebase.

- **Retrofit**: A powerful HTTP client for making network requests to retrieve weather data from an external API.

- **RxJava**: Handles asynchronous operations and API requests.

- **LiveData**: Observes data changes and notifies UI components.

- **SQLite Database**: Stores and retrieves historical city search data.

- **Glide**: Loads weather icons from URLs and displays them in the UI.

## Getting Started

To get started with this project, follow these steps:

1. Clone the repository to your local machine:

   ```bash
   git clone https://github.com/your-username/your-weather-app.git
2. Open the project in Android Studio.
3. Build and run the app on an Android emulator or physical device.

## Usage

1. Launch the Weather App on your Android device.

2. Enter the name of the city for which you want to retrieve weather information into the provided input field.

3. Tap the "Search" button to fetch the latest weather data for the specified city.

4. The app will display the current weather information, including temperature, humidity, wind speed, and weather conditions.

5. Use the "Location" button to fetch weather data for your current location.

6. View your search history and clear it using the "Clear History" button.

## Screenshots
![Weather App Logo](app_logo.png)

## Contributing

Contributions to this project are welcome. If you have suggestions, enhancements, or bug fixes, please submit a pull request.
