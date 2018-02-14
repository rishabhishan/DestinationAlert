package com.destinationalert.rishabhishan.destinationalert;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {


    boolean google = false;
    boolean mPermissionDenied = false;
    boolean mResolvingError = false;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    int HISTORY_SELECTION_CODE = 2;
    int REQUEST_LOCATION = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 3;
    private static final int LOCATION_PERMISSION_REQUEST_CODE2 = 4;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final int REQUEST_CHECK_SETTINGS = 5;
    private static final String DIALOG_ERROR = "dialog_error";
    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    private AddressResultReceiver mResultReceiver;
    GoogleApiClient mGoogleApiClient;
    static TextView markerAddress;
    private CoordinatorLayout coordinatorLayout;
    Location markerloc;
    SharedPreferenceActivity sh;
    private GoogleMap mMap;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        System.out.println("**** oncreate");

        buildGoogleApiClient();
        createLocationRequest();


        markerAddress = (TextView) findViewById(R.id.searchText);
        sh = new SharedPreferenceActivity(this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        sh.setJourneyStatus(false);
        mapFragment.getMapAsync(this);

        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        getLastKnownLocation();

                        if (markerloc != null) {
                            System.out.println("**** onConnected markerloc not null");

                            LatLng t = new LatLng(markerloc.getLatitude(), markerloc.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(t, 15));
                            changeLastlocation();

                        }

                        System.out.println("**** Location settings satisfied");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MapsActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                            System.out.println("***** Location settings not satisfied.Dialog must appear ");

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        System.out.println("**** Location settings not satisfied");
                        break;
                }
            }
        });
    }

    protected void buildGoogleApiClient() {
        System.out.println("**** build googe api client");

        if (mGoogleApiClient == null) {
            System.out.println("**** build google api client . apiclient == null");

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


    }


    protected void onStart() {
        System.out.println("**** onstart");

        mGoogleApiClient.connect();
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.destinationalert.rishabhishan.destinationalert/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    protected void onStop() {
        System.out.println("**** onstop");

        mGoogleApiClient.disconnect();
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.destinationalert.rishabhishan.destinationalert/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    protected void onDestroy() {
        System.out.println("**** onDestroy");

        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        System.out.println("**** onResume");
        mGoogleApiClient.connect();
        super.onResume();
    }


    @Override
    protected void onResumeFragments() {
        System.out.println("**** inside on resume frgment");

        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            System.out.println("**** inside on resume permission denied");
            showMissingPermissionError();
            mPermissionDenied = false;
        }
        super.onResumeFragments();
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */

    private void showMissingPermissionError() {
        System.out.println("**** inside show missing perm dialog");

        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        System.out.println("**** onConnected ");

/*
        getLastKnownLocation();

        if (markerloc != null) {
            System.out.println("**** onConnected markerloc not null");

            LatLng t = new LatLng(markerloc.getLatitude(), markerloc.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(t, 15));
            changeLastlocation();

        }

  */
        if (mAddressRequested) {

            startIntentService();

        }

    }

    private void getLastKnownLocation() {
        System.out.println("**** getlastknownlocation ");


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            System.out.println("**** inside last location permission missing");
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE2,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            System.out.println("**** getlastknown location permission given ");

            markerloc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }


    }


    private void enableMyLocation() {
        System.out.println("**** enable my location ");


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            System.out.println("**** inside enable location permission missing");
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            System.out.println("**** inside enable location permission given");
            mMap.setMyLocationEnabled(true);

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        System.out.println("**** request permission result");

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            System.out.println("**** request permission result for enablemylocation");
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Enable the my location layer if the permission has been granted.
                System.out.println("**** inside req permission result for enablemylocation perm granted");
                enableMyLocation();
            } else {
                // Display the missing permission error dialog when the fragments resume.
                System.out.println("**** inside req permission result for enablemylocation perm denied");
                mPermissionDenied = true;
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE2) {
            System.out.println("**** inside req permission result for getLastLocation ");

            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Enable the my location layer if the permission has been granted.
                System.out.println("**** inside req permission result for getLastLocation perm granted");
                getLastKnownLocation();
            } else {
                // Display the missing permission error dialog when the fragments resume.
                System.out.println("**** inside req permission result for getLastLocation perm denied");
                mPermissionDenied = true;
            }
        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        System.out.println("**** onsavedinstancestate");

        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        System.out.println("**** inside connection failed");

        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }


    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MapsActivity) getActivity()).onDialogDismissed();
        }

    }


    public void startGooglePrediction(View v) {
        System.out.println("**** starting google prediction");
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(MapsActivity.this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Google Play Services not available", Snackbar.LENGTH_LONG);

            snackbar.show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("**** inside activityresult");

        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            System.out.println("**** inside req code");

            if (resultCode == RESULT_OK) {

                google = true;
                Place place = PlaceAutocomplete.getPlace(this, data);

                markerloc.setLatitude(place.getLatLng().latitude);
                markerloc.setLongitude(place.getLatLng().longitude);
                System.out.println("*** google prediction se location " + markerloc.getLatitude());
                System.out.println("*** google prediction se location " + markerloc.getLongitude());
                LatLng t = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                Constants.ADDRESS = place.getName().toString() + ", " + place.getAddress().toString();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(t, 15));


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Unexpected error" + status.toString(), Snackbar.LENGTH_LONG);

                snackbar.show();


            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Action cancelled by user", Snackbar.LENGTH_LONG);

                snackbar.show();
            }
        }


        if (requestCode == HISTORY_SELECTION_CODE) {

            if (resultCode == RESULT_OK) {
                String lat = data.getStringExtra("latitude");
                String longi = data.getStringExtra("longitude");
                System.out.println("**** latitude jo aay hai wo hai " + lat);
                System.out.println("**** longitude jo aay hai wo hai " + longi);

                LatLng history = new LatLng(Double.parseDouble(lat), Double.parseDouble(longi));

                markerloc.setLatitude(Double.parseDouble(lat));
                markerloc.setLongitude(Double.parseDouble(longi));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(history, 15));


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Unexpected error" + status.toString(), Snackbar.LENGTH_LONG);

                snackbar.show();


            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Action cancelled by user", Snackbar.LENGTH_LONG);

                snackbar.show();
            }
        }

    }


    protected void startIntentService() {
        System.out.println("**** start intent service");
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, markerloc);
        startService(intent);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("**** onMapReady is called first");
        mMap = googleMap;
        enableMyLocation();
        getLastKnownLocation();

        if (markerloc != null && mGoogleApiClient.isConnected()) {
            System.out.println("**** onConnected markerloc not null");

            LatLng t = new LatLng(markerloc.getLatitude(), markerloc.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(t, 15));
            changeLastlocation();

        }


        if (mGoogleApiClient != null && mMap != null && markerloc != null)
            System.out.println("**** inside mapready everything not null");
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                markerloc.setLatitude(cameraPosition.target.latitude);
                markerloc.setLongitude(cameraPosition.target.longitude);
                System.out.println("**** after camera change " + google + "    " + markerloc.getLatitude() + "       " + markerloc.getLongitude());
                if (!google) {


                    changeLastlocation();

                }
                if (google) {

                    updateUIWidgets();
                    google = false;

                }


            }
        });


    }


    boolean mAddressRequested = false;

    public void changeLastlocation() {
        System.out.println("**** change last location");
        if (mGoogleApiClient.isConnected() && markerloc != null) {

            startIntentService();

        }
        mAddressRequested = true;

    }


    public void updateUIWidgets() {
        System.out.println("**** update UI ");
        markerAddress.setText(Constants.ADDRESS);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    public void continueButtonHandler(View view) {
        Constants.BAY_AREA_LANDMARKS.put(Constants.ADDRESS, new LatLng(markerloc.getLatitude(), markerloc.getLongitude()));
        saveTosharedpref();
        Intent i = new Intent(MapsActivity.this, StartEndjourney.class);
        startActivity(i);
    }

    public void saveTosharedpref() {
        System.out.println("**** locations = " + markerloc.getLatitude());
        System.out.println("**** locations = " + markerloc.getLongitude());
        System.out.println("****  datas stored in shared pref = " + Constants.ADDRESS + "    " + " " + markerloc.getLatitude() + "    " + "" + markerloc.getLongitude());
        sh.saveForCurrent(this, Constants.ADDRESS, "" + markerloc.getLatitude(), "" + markerloc.getLongitude());

    }

    public void historybuttonhandler(View view) {


        Intent i = new Intent(MapsActivity.this, FavouriteList.class);
        startActivityForResult(i, HISTORY_SELECTION_CODE);
    }


}


