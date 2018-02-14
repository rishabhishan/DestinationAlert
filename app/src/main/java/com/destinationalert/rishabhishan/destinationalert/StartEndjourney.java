package com.destinationalert.rishabhishan.destinationalert;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by rishu on 3/8/2016.
 */
public class StartEndjourney extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,ResultCallback<Status> {
    private GoogleMap mMap;
    private TextView startstop;
    private ImageView startStopImage;
    String str[] = new String[3];
    TextView heading, desc;

    protected static final String TAG = "ConfigureFinalize";
    protected GoogleApiClient mGoogleApiClient;
    protected Geofence mGeofenceList;
    private boolean mGeofencesAdded;
    private PendingIntent mGeofencePendingIntent;
    private SharedPreferenceActivity sh;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.configurefinalize);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);
        sh = new SharedPreferenceActivity(this);
        mGeofenceList = new Geofence.Builder().setCircularRegion(Double.parseDouble(sh.getForCurrent(this)[1]), Double.parseDouble(sh.getForCurrent(this)[2]), Constants.GEOFENCE_RADIUS_IN_METERS).setRequestId(Constants.GEOFENCES_ADDED_KEY).setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT).build();
        mGeofencePendingIntent = null;
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);

        mGeofencesAdded = mSharedPreferences.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);

        startstop = (TextView) findViewById(R.id.tvstartstop);
        startStopImage = (ImageView) findViewById(R.id.iconstartstop);
        heading = (TextView) findViewById(R.id.place_name_heading);
        desc = (TextView) findViewById(R.id.place_name_desc);
        buildGoogleApiClient();




    }

    @Override
    protected void onStart() {

        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {

        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected synchronized void buildGoogleApiClient() {
     //   Toast.makeText(this, "building api", Toast.LENGTH_SHORT).show();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
     //   Toast.makeText(this, "finished building api", Toast.LENGTH_SHORT).show();
        if(mGoogleApiClient!=null){
            mGoogleApiClient.connect();

        }
    }



    @Override
    protected void onDestroy() {
        System.out.println("**** destroy is called first in startend");
        mGoogleApiClient.disconnect();
        mMap.clear();
        super.onDestroy();
    }

    protected void showSourceDestination() {

        LatLng dest = new LatLng(Double.parseDouble(str[1]), Double.parseDouble(str[2]));
        System.out.println("****  datas from shared pref = " + str[0] + "    " + str[1] + "    " + str[2]);
        mMap.addMarker(new MarkerOptions()
                .position(dest)
                .title(str[0]));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dest, 15));

    }

    Location current;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap == null) {
            System.out.println("**** map null hai  ");
        } else {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            mMap.setMyLocationEnabled(true);

        }
        //addMarkers();
        //addPolyobjects();
        System.out.println("**** map ready ho gya hai ");
        final View mapView = getSupportFragmentManager().findFragmentById(R.id.maps).getView();
        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation") // We use the new method when supported
                @SuppressLint("NewApi") // We check which build version we are using.
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                    displayData();
                    showSourceDestination();
                }
            });
        }
    }


    @Override
    public void onConnected(Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");

    }
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.

        builder.addGeofence(mGeofenceList);
        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofencesButtonHandler() {
        if (!mGoogleApiClient.isConnected()) {
          //  Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        System.out.println("**** inside adding ");
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
        System.out.println("**** finished adding");
    }


    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofencesButtonHandler() {
        if (!mGoogleApiClient.isConnected()) {
        //    Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     * <p/>
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    public void onResult(Status status) {
        if (status.isSuccess()) {
            System.out.println("**** inside success result");
            // Update state and save in shared preferences.
            mGeofencesAdded = !mGeofencesAdded;
            SharedPreferenceActivity sh = new SharedPreferenceActivity(this);
            sh.updateGeoKey(this,mGeofencesAdded);
       /*     Toast.makeText(
                    this,
                    getString(mGeofencesAdded ? R.string.geofences_added :
                            R.string.geofences_removed),
                    Toast.LENGTH_SHORT
            ).show();*/
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

    }


    protected void displayData() {
        SharedPreferenceActivity sh = new SharedPreferenceActivity(this);
        str = sh.getForCurrent(this);
        String splittedName[] = str[0].split(",");
        if (splittedName.length == 1) {
            desc.setText(splittedName[0]);

        } else {
            heading.setText(splittedName[0]);
            String str = splittedName[1].substring(1);
            for (int i = 2; i < splittedName.length; i++) {
                str += splittedName[i];

            }
            desc.setText(str);

        }
        if (sh.getJourneystatus()) {
            startstop.setText("STOP");
            startStopImage.setImageResource(R.mipmap.ic_cancel_white_48dp);


        } else {

            startstop.setText("START");
            startStopImage.setImageResource(R.mipmap.ic_navigation_white_48dp);


        }

    }

    boolean flag = true;

    public void startJourney(View v) {
        if (startstop.getText().equals("START")) {
            addGeofencesButtonHandler();
            startstop.setText("STOP");
            startStopImage.setImageResource(R.mipmap.ic_cancel_white_48dp);
            SharedPreferenceActivity sh = new SharedPreferenceActivity(this);
            sh.setJourneyStatus(true);
            sh.addnewPlace(this, Constants.ADDRESS, sh.getForCurrent(this)[1], sh.getForCurrent(this)[2]);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            removeGeofencesButtonHandler();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            startstop.setText("START");
            startStopImage.setImageResource(R.mipmap.ic_navigation_white_48dp);
            SharedPreferenceActivity sh = new SharedPreferenceActivity(this);
            sh.setJourneyStatus(false);

        }

    }

    @Override
    public void onBackPressed() {
        // do nothing.

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

