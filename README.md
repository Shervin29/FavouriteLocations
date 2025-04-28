📍 Favourite Locations App

An Android application to save, view, and rate your favorite locations on Google Maps!
Built using Java, Google Maps SDK, Fused Location Provider, and SharedPreferences.

🛠 Features

🗺️ Display Google Maps with user location access

➕ Add favorite locations by tapping on the map

✏️ Enter a name, description and rating for each saved location

💾 Save favorite places using SharedPreferences (local storage)

📋 View and edit your list of saved locations

🌐 Dynamically update map markers for saved locations

📸 Screenshots: 

<img width="201" alt="Fav_Locs_1" src="https://github.com/user-attachments/assets/537e8eb2-680f-4276-a61c-31a3fb95c819" />

<img width="199" alt="Fav_Locs_2" src="https://github.com/user-attachments/assets/7fee97a6-dae0-4eaa-b7ce-147473d8fe31" />

🚀 Tech Stack: Purpose

Java:	Primary Language

Android SDK (API 35):	App Development

Google Maps SDK:	Map Integration

Fused Location Provider:	Get user's real-time location

Gson:	JSON serialization

SharedPreferences:	Data storage

ViewModel:	Manage UI-related data lifecycle


📦 Project Structure: 

FavouriteLocations/

├── app/

│   ├── src/main/java/com/example/favouritelocations/

│   │   ├── MainActivity.java         # Main map screen

│   │   ├── FavouriteLocationDataFragment.java  # Manage saved locations

│   │   ├── LocationData.java         # Model class for locations

│   │   └── LocationViewModel.java    # ViewModel for location data

│   └── res/

│       ├── layout/                   # Layout XML files

│       ├── drawable/                 # App icons and images

│       └── values/                   # Strings and styles

├── build.gradle.kts                  # Gradle build file (Kotlin DSL)

└── README.md                          # Project description (this file!)


🏗 How to Build and Run Locally: 

1. Clone this repository.
2. Open the project in Android Studio.
3. Make sure you have:
    API Key for Google Maps (add it in AndroidManifest.xml);
    Minimum SDK 26+, Target SDK 35;
    Internet permission enabled.
4. Sync Gradle and Build > Rebuild Project.
5. Run on emulator or physical device with Google Play Services.

💡 Future Improvements: 

Use Room Database instead of SharedPreferences

Add Search functionality for saved locations

Implement Marker Clustering for large datasets

Allow editing or deleting saved locations

Light/Dark Theme support for Maps


🧑‍💻 Author

Shervin Rumao

Feel free to connect with me on LinkedIn! (https://www.linkedin.com/in/shervin-rumao-b4543a199/)

