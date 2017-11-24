# API Key Setup Instructions
1. Go to the [Google API Console](https://console.developers.google.com/flows/enableapi?apiid=placesandroid&reusekey=true&pli=1).
2. Create or select a project.
3. Click Continue to enable the Google Places API for Android.
4. On the Credentials page, get an API key.

**Note:** If you have an existing API key with Android restrictions, you may use that key.

## Restricting your API key
To control who can use this key to access Google's API; restrict the key to a certain Android app

1. From the dialog displaying the API key, select Restrict key to set an Android restriction on the API key.
2. In the Restrictions section, select Android apps, then enter your app's SHA-1 fingerprint and package name. For example: BB:0D:AC:74:D3:21:E1:43:67:71:9B:62:91:AF:A1:66:6E:44:5D:75 com.example.android.places-example
3. Click Save.

To find your app's SHA-1 fingerprint information and for more information refer to this [Google documentation](https://developers.google.com/places/android-api/signup).

```
c:\Program Files\Java\jdk1.8.0_101\bin>keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android

Nombre de Alias: androiddebugkey
Fecha de Creación: 22-jul-2016
Tipo de Entrada: PrivateKeyEntry
Longitud de la Cadena de Certificado: 1
Certificado[1]:
Propietario: C=US, O=Android, CN=Android Debug
Emisor: C=US, O=Android, CN=Android Debug
Número de serie: 1
Válido desde: Fri Jul 22 22:13:21 CEST 2016 hasta: Sun Jul 15 22:13:21 CEST 2046
Huellas digitales del Certificado:
         MD5: XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX
         SHA1: XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX
         SHA256: XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:D7:68:D6:09:F3:49:51
         Nombre del Algoritmo de Firma: SHA1withRSA
         Versión: 1

```

**Note:** the command above gives you the debug SHA1

Your new Android-restricted API key appears in the list of API keys for your project. An API key is a string of characters, something like this:

```AIzaSyBdVl-cTICSwYKrZ95SuvNw7dbMuDt1KG0```

It may take up to 5 minutes for the key to be fully provisioned. If the key does not work immediately, try again in 5 minutes.


## Easier Way !!!!

[Get an API key from the Google API Console](https://developers.google.com/places/android-api/signup)