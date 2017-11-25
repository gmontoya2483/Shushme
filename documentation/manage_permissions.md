# Manage Permissions

Android 6.0 Marshmallow introduces a new model for handling permissions, which streamlines the process for users when they install and upgrade apps. Provided you're using version 8.1 or later of Google Play services, you can configure your app to target the Android 6.0 Marshmallow SDK and use the new permissions model.

If your app supports the new permissions model, the user does not have to grant any permissions when they install or upgrade the app. Instead, the app must request permissions when it needs them at runtime, and the system shows a dialog to the user asking for the permission.

## How to define the permissions needed for the application

```xml
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

```



## How to check the assigned permissions

```java
         //Check if the Access_FINE_LOCATION permision is granted
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,getString(R.string.need_location_permission_message), Toast.LENGTH_LONG).show();
                    return;
                }



             @Override
             protected void onResume() {
                 super.onResume();

                 //Initialize location permissions checkbox
                 CheckBox locationPermissions = (CheckBox) findViewById(R.id.location_permission_checkbox);
                 if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED){
                     locationPermissions.setChecked(false);
                 }else {
                     locationPermissions.setChecked(true);
                     locationPermissions.setEnabled(false);
                 }
             }

```


## How to request permissions

```java
        public void onLocationPermissionClicked(View view) {
                       ActivityCompat.requestPermissions(MainActivity.this,
                               new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                               PERMISSIONS_REQUEST_FINE_LOCATION);
            }
```


## References

[System permissions policies](https://developer.android.com/guide/topics/permissions/index.html)