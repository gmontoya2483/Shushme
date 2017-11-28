# Silent Mode

Now for the last piece of our puzzle we need to actually set the device to silent when a Geofence triggers an entry transition and change it back to normal when an exit transition is triggered!

Ever since Android N things have changed a little: Now to change Android’s ringer mode you need to have the right permissions, and unfortunately it’s slightly more complicated than the regular permissions we have dealt with before, like the location permission for example.

So let’s start with that.

First let’s add a checkbox to ask the user for **RingerMode permissions**, the onclick event will launch an intent for ```ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS``` and start it.


```java
        public void onRingerPermissionsClicked(View view) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }
```

This intent launches a new built-in activity that allows the user to turn **RingerMode permissions** on or off for their apps. Once the user turns that on, they can then press back and continue to use the app.

To make sure our checkbox is always reflecting the correct permission status we need to initialize its state in ```onResume```.

In case of Android N or later, we need to check if the permission was granted using ```isNotificationPolicyAccessGranted```, otherwise we could assume that the permission is granted by default.

Also since we don’t want the user to be unchecking this after permissions have been granted, it's best to disable the checkbox once everything seems to be set properly.


```java

           @Override
            protected void onResume() {
                super.onResume();

                ...


                // Initialize ringer permissions checkbox
                CheckBox ringerPermissions = (CheckBox) findViewById(R.id.ringer_permissions_checkbox);
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // Check if the API supports such permission change and check if permission is granted
                if (android.os.Build.VERSION.SDK_INT >= 24 && !nm.isNotificationPolicyAccessGranted()) {
                    ringerPermissions.setChecked(false);
                } else {
                    ringerPermissions.setChecked(true);
                    ringerPermissions.setEnabled(false);
                }


            }
```

```xml
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal">

                    <ImageView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:paddingBottom="4dp"
                        android:paddingRight="8dp"
                        android:paddingTop="4dp"
                        android:src="@drawable/ic_notifications_active_primary_24dp" />

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_gravity="center"
                        android:text="@string/ringer_permissions"
                        android:textAppearance="@style/TextAppearance.AppCompat.Medium"
                        android:textColor="@android:color/black" />

                    <RelativeLayout
                        android:layout_width="match_parent"
                        android:layout_height="match_parent">

                        <CheckBox
                            android:id="@+id/ringer_permissions_checkbox"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_alignParentRight="true"
                            android:layout_marginRight="8dp"
                            android:onClick="onRingerPermissionsClicked" />

                    </RelativeLayout>
                </LinearLayout>

```

Now let’s go back to our ```GeofenceBroadcastReceiver```.

In ```onReceive```, we can use ```GeofencingEvent.fromIntent``` to retrieve the ```GeofencingEvent``` that caused the transition.

From that we can call ```getGeofenceTransition``` to get the transition type.

```java
    // Get the Geofence Event from the Intent sent through
    GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
```

And based on the transition type, we can use ```AudioManager``` to set the phone ringer mode.

To keep things modular. I’ve created this helper method ```setRingerMode``` that using an ```AudioManager``` can set the ```setRingerMode``` to either silent or normal based on the mode parameter passed in. It also creates a ```NotificationManager``` to check for permissions for Android N or later.

```java

        /**
         * Changes the ringer mode on the device to either silent or back to normal
         *
         * @param context The context to access AUDIO_SERVICE
         * @param mode    The desired mode to switch device to, can be AudioManager.RINGER_MODE_SILENT or
         *                AudioManager.RINGER_MODE_NORMAL
         */
        private void setRingerMode(Context context, int mode) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // Check for DND permissions for API 24+
            if (android.os.Build.VERSION.SDK_INT < 24 ||
                    (android.os.Build.VERSION.SDK_INT >= 24 && !nm.isNotificationPolicyAccessGranted())) {
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setRingerMode(mode);
            }
        }
 ```

 ```java
                // Get the transition type.
                int geofenceTransition = geofencingEvent.getGeofenceTransition();
                // Check which transition type has triggered this event
                if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    setRingerMode(context, AudioManager.RINGER_MODE_SILENT);
                } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    setRingerMode(context, AudioManager.RINGER_MODE_NORMAL);
                } else {
                    // Log the error.
                    Log.e(TAG, String.format("Unknown transition : %d", geofenceTransition));
                    // No need to do anything else
                    return;
                }
 ```


## Notifications

It would also be nice to have the app notify the user whenever the device has been set to silent or back to normal.

So I’ve created this helper method ```sendNotification``` that will create a typical Android notification with an icon corresponding to either being silent or normal.

```java
            // Check the transition type to display the relevant icon image
            if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                builder.setSmallIcon(R.drawable.ic_volume_off_white_24dp)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.ic_volume_off_white_24dp))
                        .setContentTitle(context.getString(R.string.silent_mode_activated));
            } else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
                builder.setSmallIcon(R.drawable.ic_volume_up_white_24dp)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.ic_volume_up_white_24dp))
                        .setContentTitle(context.getString(R.string.back_to_normal));
            }

```


