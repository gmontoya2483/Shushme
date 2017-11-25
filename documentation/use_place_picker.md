# Place picker

The place picker is a simple and yet flexible built-in UI widget, part of the Google Places API for Android.

## Introducing the place picker

<img src="../screenshots/placepicker.png" width="200" height="350" alt="Image"/>


The [PlacePicker](https://developers.google.com/android/reference/com/google/android/gms/location/places/ui/PlacePicker) provides a UI dialog that displays an interactive map and a list of nearby places, including places corresponding to geographical addresses and local businesses. Users can choose a place, and your app can then retrieve the details of the selected place.

The place picker provides the following advantages over developing your own UI widget:

1. The user experience is consistent with other apps using the place picker, including Google apps and third parties. This means users of your app already know how to interact with the place picker.

2. The map is integrated into the place picker.
3. Accessibility is built in.
4. It saves development time.

The place picker features autocomplete functionality, which displays place predictions based on user search input. This functionality is present in all place picker integrations, so you don't need to do anything extra to enable autocomplete. For more information about autocomplete, see [Place Autocomplete](https://developers.google.com/places/android-api/autocomplete).

## Launch a place picker

Here is a summary of the steps required to launch the place picker:

1. Use the ```PlacePicker.IntentBuilder()``` to construct an ```Intent```.
2. If you want to change the place picker's default behavior, you can use the builder to set the initial latitude and longitude bounds of the map displayed by the place picker. Call ```setLatLngBounds()``` on the builder, passing in a ```LatLngBounds``` to set the initial latitude and longitude bounds. These bounds define an area called the 'viewport'. By default, the viewport is centered on the device's location, with the zoom at city-block level.
3. Call ```startActivityForResult()```, passing it the intent and a pre-defined request code, so that you can identify the request when the result is returned.


To launch the place picker interface it is needed to set an intent which stars a new activity  

```java
        private final int PLACE_PICKER_REQUEST = 1;

        ....

        try {
                    // Start a new Activity for the Place Picker API, this will trigger {@code #onActivityResult}
                    // when a place is selected or with the user cancels.
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    Intent i = builder.build(this);
                    startActivityForResult(i, PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException e) {
                    Log.e(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
                } catch (Exception e) {
                    Log.e(TAG, String.format("PlacePicker Exception: %s", e.getMessage()));
                }

        ....
```  


## Retrive the selected location

The selected place is received by the ```onActivityResult()``` method. When the user selects a place, it can be retrieved  by calling ```PlacePicker.getPlace()```. If the user has not selected a place, the method will return null.

You can also retrieve the most recent bounds of the map by calling ```PlacePicker.getLatLngBounds()```.

```java
         /***
             * Called when the Place Picker Activity returns back with a selected place (or after canceling)
             *
             * @param requestCode The request code passed when calling startActivityForResult
             * @param resultCode  The result code specified by the second activity
             * @param data        The Intent that carries the result data.
             */
            @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(this, data);
                    if (place == null) {
                        Log.i(TAG, "No place selected");
                        return;
                    }

                    // Extract the place information from the API
                    String placeName = place.getName().toString();
                    String placeAddress = place.getAddress().toString();
                    String placeID = place.getId();

                    // Insert a new place into DB
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(PlaceContract.PlaceEntry.COLUMN_PLACE_ID, placeID);
                    getContentResolver().insert(PlaceContract.PlaceEntry.CONTENT_URI, contentValues);
                }
            }
```


## Display attributions in your app

When your app displays information obtained via the place picker, the app must also display attributions. See the documentation on [attributions](https://developers.google.com/places/android-api/attributions).


## References
[Google Place API](https://developers.google.com/places/android-api/placepicker)