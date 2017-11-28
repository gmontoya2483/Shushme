package com.example.android.shushme;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriel on 27/11/2017.
 */

public class Geofencing implements ResultCallback {

    // Constants
    public static final String TAG = Geofencing.class.getSimpleName();
    private static final float GEOFENCE_RADIUS = 50; // 50 meters
    private static final long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000; // 24 hours




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


    /**
     * Updates the local ArrayList of Geofences using data from the passed in List
     * Uses the Place ID defined by the API as the Geofence object id
     * @param places the PlaceBuffer result of the getPlaceById call
     */
    public void updateGeofencesList (PlaceBuffer places){
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



    @Override
    public void onResult(@NonNull Result result) {

        Log.e(TAG, String.format("Error adding/removing geofence : %s",
                result.getStatus().toString()));

    }
}
