# 📍 Background Location Fetch App (Android)

A sample Android app to demonstrate how to fetch device location in the background using foreground services, WorkManager, and broadcast receivers.

## 🚀 Features

- Retrieves location updates even when the app is in the background
- Uses **Foreground Service** for continuous tracking
- **WorkManager** to schedule background location sync tasks
- **BroadcastReceiver** to listen for system or custom intents
- Compatible with modern Android versions (targetSdk >= 30)

## 🛠 Tech Stack

- **Language**: Kotlin
- **Libraries**:
  - `com.google.android.gms:play-services-location:<latestversion>`
  - `androidx.work:work-runtime-ktx:<latestversion>`

## 🧩 Key Components

- **Foreground Service**: Keeps the app alive to track location reliably
- **WorkManager**: Used for periodic or one-time background location updates
- **BroadcastReceiver**: Listens to triggers like boot completed, network change, or custom actions
- **FusedLocationProviderClient**: For efficient location retrieval

  ## 🚀 Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/aravindhan2612/FetchLocation-Android-Native.git
   ```

# License
This project is open-source and available under the MIT License.
