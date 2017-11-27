package com.example.android.shushme;

import android.app.PendingIntent;
import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriel on 27/11/2017.
 */

public class Geofencing {

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

}
