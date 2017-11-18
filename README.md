# ShushMe
Google Places API demo app

## What is ShushMe

ShushMe allows you to pick a set of locations using Google's Places API, it then creates geofences around those places and turns your device into silent mode if you enter any of them. Once the device exits again ShushMe turns your device back to normal (whatever ringer mode and volume you had before entering)

## Screenshots

![Screenshot1](screenshots/screen_1.png) ![Screenshot2](screenshots/screen_2.png) ![Screenshot3](screenshots/screen_3.png)
![Screenshot4](screenshots/screen_4.png) ![Screenshot5](screenshots/screen_5.png) ![Screenshot6](screenshots/screen_6.png)


## Instructions
1. [Setup the API key](.\Documentation\api_key_setup_instructions.md)
2. Add the API key into the `AdroidManifest.xml`
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


## References

* [Places API for Android](https://developers.google.com/places/android-api/)
* [Location](https://developer.android.com/reference/android/location/Location.html)