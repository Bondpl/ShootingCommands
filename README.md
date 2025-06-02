# Random Shooting Commands

This is an Android application that plays random audio commands with a customizable delay between each playback. The user can start and stop the sequence and set the delay (up to 5000 ms) between commands.

## Features

- Play random audio commands from assets
- Set custom delay (max 5000 ms) between commands
- Start and stop playback at any time
- Simple and intuitive UI

## Getting Started

### Prerequisites

- Android Studio (latest version recommended)
- Android device or emulator (API 21+)

### Building the App

1. Clone this repository with SSH:
   ```bash
   git clone git@github.com:Bondpl/ShootingCommands.git
   ```
2. Open the project in Android Studio.
3. Build and run the app on an emulator or a physical device.

### Installing APK on a Real Device

1. Build the APK in Android Studio:  
   **Build > Build Bundle(s) / APK(s) > Build APK(s)**
2. Transfer the generated APK file to your device.
3. On your device, open the APK file and follow the installation instructions.


## Customization

- Add your own audio files to the `assets/Audio/` directory.
- Update the `audioFiles` list in the code to include your new files.

## License

This project is licensed under the MIT License.
