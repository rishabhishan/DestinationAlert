package com.destinationalert.rishabhishan.destinationalert;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rishu on 3/6/2016.
 */
public class SharedPreferenceActivity extends AppCompatActivity {
    SharedPreferences pSharedPref;
  public SharedPreferenceActivity (){}
    public SharedPreferenceActivity(Context context) {

        pSharedPref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

    }

    protected String getAddressForCurrent(Context context) {


        return pSharedPref.getString("currentplacename","Unknown Location");


    }

    protected void saveForCurrent(Context context, String str, String lat, String longi) {


        SharedPreferences.Editor editor = pSharedPref.edit();
        editor.putString("currentplacename", str);
        editor.putString("currentplacelat", lat);
        editor.putString("currentplacelong", longi);
        editor.commit();
    }

    protected void setJourneyStatus(Boolean flag) {

        SharedPreferences.Editor editor = pSharedPref.edit();
        editor.putBoolean("journey", flag);
        editor.commit();

    }

    protected boolean getJourneystatus() {
        return pSharedPref.getBoolean("journey", false);


    }

    protected String[] getForCurrent(Context context) {

        String str[] = new String[3];
        str[0] = "";
        str[1] = "";
        str[2] = "";
        str[0] = pSharedPref.getString("currentplacename", "No Result");
        str[1] = pSharedPref.getString("currentplacelat", "77.0");
        str[2] = pSharedPref.getString("currentplacelong", "12.0");
        return str;

    }

    protected void updateGeoKey(Context context, boolean flag) {

        SharedPreferences.Editor editor = pSharedPref.edit();
        editor.putBoolean(Constants.GEOFENCES_ADDED_KEY, flag);
        editor.apply();
        editor.commit();

    }

    protected void addnewPlace(Context context, String str, String lat, String longi) {
        if (pSharedPref != null) {
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.putBoolean("journeystatus", true);
            editor.putString("placename", str + "|" + pSharedPref.getString("placename", ""));
            editor.putString("latitude", lat + "|" + pSharedPref.getString("latitude", ""));
            editor.putString("longitude", longi + "|" + pSharedPref.getString("longitude", ""));
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
            String currentDateandTime = sdf.format(new Date());
            sdf = new SimpleDateFormat("h:mm a");
            currentDateandTime += " at " + sdf.format(new Date());
            System.out.println("****** " + currentDateandTime);
            editor.putString("datetime", currentDateandTime.toUpperCase() + "|" + pSharedPref.getString("datetime", ""));
            ///editor.putString("My_map", jsonString);
            editor.commit();


        }
    }

    public String[] getPlaceName(Context context) {

        if (pSharedPref != null) {
            String placeCombined = pSharedPref.getString("placename", "");
            System.out.println("**** place list  " + placeCombined);

            String str[] = placeCombined.split("\\|");
            System.out.println("**** str len   " + str.length);

            return str;
        }

        return null;

    }

    public String[] getLatitude(Context context) {

        if (pSharedPref != null) {

            String latiCombined = pSharedPref.getString("latitude", "");
            String lat[] = latiCombined.split("\\|");

            return lat;
        }

        return null;

    }


    public String[] getLongitude(Context context) {

        if (pSharedPref != null) {

            String longiCombined = pSharedPref.getString("longitude", "");
            String longi[] = longiCombined.split("\\|");

            return longi;
        }

        return null;

    }


    public String[] getdateTime(Context context) {
        SharedPreferences pSharedPref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        if (pSharedPref != null) {

            String datetimeCombined = pSharedPref.getString("datetime", "");
            String datetime[] = datetimeCombined.split("\\|");

            return datetime;
        }

        return null;

    }
}
