# Get place by ID

this functionality is used to request the the place details from its ID.

A place ID is a textual identifier that uniquely identifies a place. In the Google Places API for Android, you can retrieve the ID of a place by calling [Place.getId()](https://developers.google.com/android/reference/com/google/android/gms/location/places/Place#getId()). The [Place Autocomplete](https://developers.google.com/places/android-api/autocomplete) service also returns a place ID for each place that matches the supplied search query and filter. You can store the place ID and use it to retrieve the [Place](https://developers.google.com/android/reference/com/google/android/gms/location/places/Place) object again later.

To get a place by ID, call [GeoDataClient.getPlaceById](https://developers.google.com/android/reference/com/google/android/gms/location/places/GeoDataClient), passing one or more place IDs.

The API returns a [PlaceBuffer](https://developers.google.com/android/reference/com/google/android/gms/location/places/PlaceBuffer) in a [PendingResult](https://developers.google.com/android/reference/com/google/android/gms/common/api/PendingResult). The [PlaceBuffer](https://developers.google.com/android/reference/com/google/android/gms/location/places/PlaceBuffer) contains a list of [Place](https://developers.google.com/android/reference/com/google/android/gms/location/places/Place) objects that match the supplied place IDs.


The following code example shows calling ```getPlaceById()``` to get details for the specified place.


```java


    private GoogleApiClient mClient;

       ...

    public void refreshPlacesData() {
            Uri uri = PlaceContract.PlaceEntry.CONTENT_URI;
            Cursor data = getContentResolver().query(
                    uri,
                    null,
                    null,
                    null,
                    null);

            if (data == null || data.getCount() == 0) {
                return; //If there is no data in the database exists-
            }

            //It is created from the Cursor data a list of IDs
            List<String> guids = new ArrayList<String>();
            while (data.moveToNext()) {
                guids.add(data.getString(data.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PLACE_ID)));
            }


             //Retrieve the places information wich is buffered in the PlaceBuffer
             //The callback result is called when all the information is retrieved.
             // Finally the Adapter is triggered to swap the places
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mClient, guids.toArray(new String[guids.size()]));
            placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(@NonNull PlaceBuffer places) {
                    mAdapter.swapPlaces(places);

                }
            });

        }


            ....



            /***
             * Called when the Place Picker Activity returns back with a selected place (or after canceling)
             *
             * @param requestCode The request code passed when calling startActivityForResult
             * @param resultCode  The result code specified by the second activity
             * @param data        The Intent that carries the result data.
             */
            @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent data) {


                    ...

                    // Get live data information
                    refreshPlacesData();
                }
            }


            /**
            * Called when the Google API client is successfully connected
            * @param connectionHint Bundle of data provided to clients by Google Play services
            */
             @Override
             public void onConnected(@Nullable Bundle connectionHint) {
                 refreshPlacesData();
                 Log.i (TAG,"API Client Connection Successful!");
             }



                   ...

```


## PlaceListAdapter

```java

    public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder> {

            private Context mContext;
            private PlaceBuffer mPlaces;

            /**
             * Constructor using the context and the db cursor
             *
             * @param context the calling context/activity
             * @param places PlaceBuffer used to full fill the list of places
             */
            public PlaceListAdapter(Context context, PlaceBuffer places) {

                this.mContext = context;
                this.mPlaces = places;
            }


                    ....

```



## References

[Place IDs and Details](https://developers.google.com/places/android-api/place-details)

[Place](https://developers.google.com/android/reference/com/google/android/gms/location/places/Place)