package com.destinationalert.rishabhishan.destinationalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by Aman on 1/7/2015.
 */
public class Splash extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    int count = 0;
    LinearLayout ll;
    SharedPreferences sharedPreferences;
    private static final int REQUEST_CHECK_SETTINGS = 5;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        ll = (LinearLayout) findViewById(R.id.networkerror);


        buildGoogleApiClient();
    }


    protected void createLocationRequest() {
        System.out.println("**** splash create location req ");
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());
        System.out.println("**** splash b4 result callback ");
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        System.out.println("**** splash Location settings satisfied");
                        checkLogin();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            Snackbar snack = Snackbar.make(ll, "Please provide GPS access", Snackbar.LENGTH_INDEFINITE);
                            snack.show();
                            status.startResolutionForResult(
                                    Splash.this,
                                    REQUEST_CHECK_SETTINGS);

                            System.out.println("***** splash Location settings not satisfied.Dialog must appear ");

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        System.out.println("**** splash Location settings not satisfied");
                        Snackbar snack = Snackbar.make(ll, "Please provide GPS access", Snackbar.LENGTH_INDEFINITE);
                        snack.show();
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

    @Override
    protected void onResume() {
        ll.setVisibility(View.GONE);
        // buildGoogleApiClient();
        // createLocationRequest();
        // checkLogin();
        mGoogleApiClient.connect();
        super.onResume();

    }

    private void showSettingsAlert() {

        Snackbar snack = Snackbar.make(ll, "No Internet connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("SETTINGS", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(
                                Settings.ACTION_SETTINGS);
                        Splash.this.startActivity(intent);

                    }
                });
        snack.show();
    }


    private void checkLogin() {
        System.out.println("**** check login ");
        ConnectivityManager check = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        count = 0;
        NetworkInfo[] info = check.getAllNetworkInfo();
        for (int i = 0; i < info.length; i++) {
            if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                count++;
            }
        }
        if (count == 0) {
            System.out.println("**** no internet connection ");
            showSettingsAlert();


        } else {
            Thread timer = new Thread() {
                @Override
                public void run() {
                    try {

                        sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {


                        Intent ola = new Intent(Splash.this, MapsActivity.class);
                        startActivity(ola);
                        finish();


                    }
                }
            };
            timer.start();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("**** splash on connected ");
        createLocationRequest();
        System.out.println("**** splash onconnected location req creaated");
        //checkLogin();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void onStart() {
        System.out.println("**** onstart");

        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        System.out.println("**** onstop");

        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        System.out.println("**** onDestroy");

        mGoogleApiClient.disconnect();
        super.onDestroy();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made
                        checkLogin();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        //Toast.makeText(Splash.this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        Snackbar snack = Snackbar.make(ll, "Please provide GPS access", Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        createLocationRequest();
                                    }
                                });
                        snack.show();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        System.out.println("**** splash connection failed ");
    }
}

