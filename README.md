# ShushMe
Demo Application which explains how to use the Google place API. The Demo and most of the documentation are part of the **Udacity - Advance Android App Development** course.

## What is ShushMe

ShushMe allows you to pick a set of locations using Google's Places API, it then creates geofences around those places and turns your device into silent mode if you enter any of them. Once the device exits again ShushMe turns your device back to normal (whatever ringer mode and volume you had before entering)


## Content

* PlacePicker
* GoogleApiClient
* Place
* getPlaceByID
* Geofencing
* Permissions
* Audio Manager



## Screenshots

![Screenshot1](screenshots/screen_1.png) ![Screenshot2](screenshots/screen_2.png) ![Screenshot3](screenshots/screen_3.png)
![Screenshot4](screenshots/screen_4.png) ![Screenshot5](screenshots/screen_5.png) ![Screenshot6](screenshots/screen_6.png)


## Instructions
1. [Setup the API key](documentation/api_key_setup_instructions.md)
2. Add the API key into the `AdroidManifest.xml` file
```xml
    <application
       .... >

        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="AIzaSyBdVl-cTICSwYKrZ95SuvNw7dbMuDt1KG0"/> <!-- Api unique key-->

        ...

    </application>
```

3. Add the dependencies in app `build.gradle`

```
dependencies {
    ...

    compile 'com.google.android.gms:play-services-places:11.6.0'
    compile 'com.google.android.gms:play-services-location:11.6.0'

    ...
}
```

4. Add the following permissions into the `AndroidManifest.xml` file
```xml
    <uses-permission android:name="android.permission.INTERNET"/>
        <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/><!-- Allows to use GPS, WIFI and CELL towers-->
        <!--<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/> --> <!-- Allows to use only WIFI and CELL towers-->
```

5. [Set the Google API Client](documentation/google_api_client.md)  


## Place Picker

[Use the Place picker](documentation/use_place_picker.md)


## Place by ID

[Get place by ID](documentation/get_place_by_id.md)


## Geofencing
[Defining the Geofences](documentation/geofence.md)


## Silent Mode
[Silent Mode](documentation/silent_mode.md)


## Privacy and Attribution

[Privacy and Attributions](documentation/privacy_and_attribution.md)





## Manage Permissions

For managing the needed permissions, [Manage permissions](documentation/manage_permissions.md) Document.


## References

* [Places API for Android](https://developers.google.com/places/android-api/)
* [Location](https://developer.android.com/reference/android/location/Location.html)