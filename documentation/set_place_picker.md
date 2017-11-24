# Set a place picker  


To launch the place picker interface it is needed to set an intent which stars a new activity  

```java
        PlacePicker.IntentBuilder builder = new Placepicker.IntentBuilder();
        Intent i=builder.build (this);
        startActivityForResult (i, PLACE_PICKER_REQUEST);
```  

The ```startActivityForResult``` method 
