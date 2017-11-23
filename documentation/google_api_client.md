# Google API Client

Some Google Play Services APIs require you to create a client that will connect to Google Play Services and use that connection to communicate with the APIs

![Screenshot1](../screenshots/google_api_client_diagram.png)

In our case, both places API and location services API require that client, so how do we create one? A Google API Client is created using ```GoogleApiClient.Builder``` as follows:

``` java
    GoogleApiClient client = new GoogleApiClient.Builder (this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi (LocationServices.API)
        .addApi (Places.GEO_DATA_API)
        .enableAutoManage(this,this)
        .build();
```

