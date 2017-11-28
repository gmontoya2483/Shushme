# Defining the Geofences

Geofencing combines awareness of the user's current location with awareness of the user's proximity to locations that may be of interest. To mark a location of interest, you specify its latitude and longitude. To adjust the proximity for the location, you add a radius. The latitude, longitude, and radius define a geofence, creating a circular area, or fence, around the location of interest.

You can have multiple active geofences, with a limit of **100** per device user. For each geofence, you can ask Location Services to send you entrance and exit events, or you can specify a duration within the geofence area to wait, or dwell, before triggering an event. You can limit the duration of any geofence by specifying an expiration duration in milliseconds. After the geofence expires, Location Services automatically removes it.

![Screenshot1](../screenshots/geofences_map.png)

## Considerations

Geofences are not adecuated for GPS pooling and the device could take a couple of minutes in realized it has entered in Geofence.



## Overview

1. Build Geofences objects (lat, long and radius)

    ![Screenshot1](../screenshots/geofences_objects.png)

2. Store all the geofences in a list.

    ![Screenshot1](../screenshots/geofences_objects_list.png)


3. Register the geofences by creating Geofence request object with the list of geofences from above

    ![Screenshot1](../screenshots/geofences_register.png)

4. Define a pending intent to specify which intent to launch when the geofence entry or exit envent trigger

    ![Screenshot1](../screenshots/geofences_pendingIntent.png)

5. Use a broadcast receiver in the Pending Intent. Whenever the device enters or exist any of the geofences, the onReceive method in the broadcast receiver will run (the logic will be implemented there)

    ![Screenshot1](../screenshots/geofences_broadcastReceiver.png)

6. Register the geofences by passing the Geofence Request and the pending intent objects

    ![Screenshot1](../screenshots/geofences_registerGooglePlazservice.png)

7. Add in the Google API client

    ![Screenshot1](../screenshots/geofences_add_in_googleAPIClient.png)



## Implemantation


1. Ensure the  ```ACCESS_FINE_LOCATION```permission was granted

```xml
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
```

2. Create a ```Geofencing``` class

```java
        public class Geofencing {
            private GoogleApiClient mGoogleApiClient;
            private Context mContext;
            private List<Geofence> mGeofenceList;
            private PendingIntent mGeofencePendingIntent;


            public Geofencing(Context context, GoogleApiClient client) {
                this.mGoogleApiClient = client;
                this.mContext = context;
                this.mGeofenceList = new ArrayList<>();
                this.mGeofencePendingIntent = null;
            }


            ....

        }
```

3. Implement a update geofences list, which retrieves a place buffer and convert fills the mGeofenceList ArrayList

 ```java
        public class Geofencing {

                ...


            /**
             * Updates the local ArrayList of Geofences using data from the passed in List
             * Uses the Place ID defined by the API as the Geofence object id
             * @param places the PlaceBuffer result of the getPlaceById call
             */
            public void updateGeofenceList (PlaceBuffer places){
                mGeofenceList = new ArrayList<>();
                if (places == null || places.getCount() == 0){
                    return;
                }

                for (Place place : places){

                    // Read the place information from the DB Cursor
                    String placeUID = place.getId();
                    double placeLat = place.getLatLng().latitude;
                    double placeLng = place.getLatLng().longitude;

                    //Build a Geofence object
                    Geofence geofence = new Geofence.Builder()
                            .setRequestId(placeUID)
                            .setExpirationDuration(GEOFENCE_TIMEOUT)
                            .setCircularRegion(placeLat, placeLng, GEOFENCE_RADIUS)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build();

                    // Add it to the List
                    mGeofenceList.add(geofence);
                }
            }


                ...
 ```

4. Build the Geofence request Object

```java

   public class Geofencing {

                   ...


            /**
            * Creates a GeofencingRequest object using the mGeofenceList ArrayList of Geofences
            * used by {@code #registerGeofences}
            *
            * @return the geofencingRequest object
            */

           private GeofencingRequest getGeofencingRequest(){
               GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
               builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
               builder.addGeofences(mGeofenceList);
               return builder.build();

           }



                  ...

```

5. Create the Pending Intent which extends a broadcast receiver class

    **5.1.** Create a BroadcastReceiver class

    ```java

        public class GeofenceBroadcastReceiver extends BroadcastReceiver {


            public static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();


            /**
             * Handles the Broadcast message sent when the Geofence Transition is triggered
             * Careful here thought this is running on the main thread so make sure you start an asynctask for anything that takes longer than say 10 seconds
             * @param context
             * @param intent
             */
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "onReceived called");
            }
        }

     ```


    ```AndroidManifest.xml```


    ```xml

        <application

                ...

                <receiver android:name=".GeofenceBroadcastReceiver"/>

            </application>

    ```

    **5.2.** Create a create a helper method in the ```Geofencing``` class, ```getGeofencePendingIntent``` which will create the pending intent

    ```java
            /**
             * Creates a PendingIntent object using the GeofenceTransitionsIntentService class
             * used by {@code #registerGeoFences
             * }
             * @return the pendingIntet Object
             */
            private PendingIntent getmGeofencePendingIntent(){
                //Pause the pending intent if we already have it.
                if (mGeofencePendingIntent != null){
                     return mGeofencePendingIntent;
                }

                Intent intent = new Intent(mContext,GeofenceBroadcastReceiver.class);
                mGeofencePendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                return mGeofencePendingIntent;
            }
    ```


6.  Register the geofences

    **6.1.** Create a new public method called ```registerAllGeofences``` within the ```Geofencing``` class

    ```java
        /**
         * Register the list of Geofences specified in mGeofenceList with Google Place Services
         * Uses {@code #mGoogleApiClient} to connect to Google Place Service
         * Uses {@Link #getGeofencingRequest} to get the list of geofences to be registered
         * Uses {@Link #getGeofencePendingIntent} to get the pending intent o launch the IntentService when the Geofence is triggered
         * Triggers {@Link #onResult} when the geofences have been registered successfully
         */
        public void registerAllGeofences(){
            //Check that the API client is connected and that the list has Geofences in it
            if (mGoogleApiClient == null || !mGoogleApiClient.isConnected() || mGeofenceList == null || mGeofenceList.size()==0){
                return;
            }
            try{
                LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,getGeofencingRequest(),getmGeofencePendingIntent()).setResultCallback(this);

            }catch (SecurityException securityException){
                Log.e (TAG, securityException.getMessage());

            }

        }
   ```

   **Note:**  the ```LocationServices.GeofencingApi``` is deprecated. Verify how to use the connectionless API ```GeofencingClient``` instead.

    ```java
        private GeofencingClient mGeofencingClient;

        // ...

        mGeofencingClient = LocationServices.getGeofencingClient(this);
    ```

    ```java
            mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        // ...
                    }
                });

    ```



    **6.2.** Make the Geofencing class implement the ```ResultCallback``` interface and implement the ```onResult``` method

    ```java
        public class Geofencing implements ResultCallback {

        \\...

        @Override
            public void onResult(@NonNull Result result) {

                Log.e(TAG, String.format("Error adding/removing geofence : %s",
                        result.getStatus().toString()));

            }


        }
     ```

7. Unregister all the Geofences

```java
        /**
         * Unregister the list of Geofences specified in mGeofenceList with Google Place Services
         * Uses {@code #mGoogleApiClient} to connect to Google Place Service
         * Uses {@Link #getGeofencePendingIntent} to get the pending intent o launch the IntentService when the Geofence is triggered
         * Triggers {@Link #onResult} when the geofences have been unregistered successfully
         */
        public void unRegisterAllGeofences(){
            if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()){
                return;
            }
            try{
                LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient,getmGeofencePendingIntent()).setResultCallback(this);

            }catch (SecurityException securityException){
                Log.e (TAG, securityException.getMessage());
            }

        }
 ```

**Note:**  the ```LocationServices.GeofencingApi``` is deprecated. Verify how to use the connectionless API ```GeofencingClient``` instead.

```java
    mGeofencingClient.removeGeofences(getGeofencePendingIntent())
            .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Geofences removed
                    // ...
                }
            })
            .addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Failed to remove geofences
                    // ...
                }
            });
```


8. Setup everything in the MainActivity

    **8.1.** Create an instance of the ```Geofencing``` class


    ```java

        ...

        private Geofencing mGeofencing;

        ...

        @Override
            protected void onCreate(Bundle savedInstanceState) {

                ...

                mGeofencing = new Geofencing(this, mClient);

                ...

            }

    ```

    **8.2.** Initialize the switch state and handle enable/disable switch change

    ```java

        private boolean mIsEnabled;

        ...

        @Override
            protected void onCreate(Bundle savedInstanceState) {

                ...


                // Initialize the switch state and Handle enable/disable switch change
                Switch onOffSwitch = (Switch) findViewById(R.id.enable_switch);
                mIsEnabled = getPreferences(MODE_PRIVATE).getBoolean(getString(R.string.setting_enabled), false);
                onOffSwitch.setChecked(mIsEnabled);
                onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                        editor.putBoolean(getString(R.string.setting_enabled), isChecked);
                        mIsEnabled = isChecked;
                        editor.commit();
                        if (isChecked) mGeofencing.registerAllGeofences();
                        else mGeofencing.unRegisterAllGeofences();
                    }

                });

                ....

    ```


    **8.3.** Within the ```refreshPlacesData()``` method in the ```onResult``` update the list of places and register the all the geofences if the switch is enables

     ```java
            /**
             * Queries all the locally stored Places IDs
             * Calls Places.GeoDataApi.getPlaceById with that list of IDs
             * Note: When calling Places.GeoDataApi.getPlaceById use the same GoogleApiClient created
             * in MainActivity's onCreate (you will have to declare it as a private member)
             */
            public void refreshPlacesData() {

            ...


                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mClient, guids.toArray(new String[guids.size()]));

                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        mAdapter.swapPlaces(places);
                        mGeofencing.updateGeofencesList(places);
                        if (mIsEnabled) mGeofencing.registerAllGeofences();

                    }
                });
            }

     ```



## References

[Creating and Monitoring GeoFences](https://developer.android.com/training/location/geofencing.html)

[Google APIs for Android Geofences](https://developers.google.com/android/reference/com/google/android/gms/location/Geofence)


