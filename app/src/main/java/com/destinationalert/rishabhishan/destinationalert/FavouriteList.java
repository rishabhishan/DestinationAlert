package com.destinationalert.rishabhishan.destinationalert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by SaurabhAgrawal on 11/16/2015.
 */
public class FavouriteList extends AppCompatActivity implements AdapterView.OnItemClickListener {


    String[] placename;
    String[] lat;
    String[] longi;
    String[] datetime;
    int k = 0;
    ArrayList<RowItemForHistory> rowItems;
    ListView mylistview;
    CustomAdapterHistory adapter;
    SharedPreferenceActivity sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sh = new SharedPreferenceActivity(this);
        setContentView(R.layout.fav_list_result);

        getSharedPreferencesdata();
        initialize();
        mylistview = (ListView) findViewById(R.id.list);
        adapter = new CustomAdapterHistory(FavouriteList.this, rowItems);
        mylistview.setAdapter(adapter);
        mylistview.setOnItemClickListener(FavouriteList.this);


    }


    public void getSharedPreferencesdata() {

        placename = sh.getPlaceName(this);
        System.out.println("**** lengths gspf  " + placename.length);
        System.out.println("**** plce  " + placename[0]);


        datetime = sh.getdateTime(this);
        lat = sh.getLatitude(this);
        longi = sh.getLongitude(this);

    }

    String latselected, longselected;

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        latselected = lat[i];
        longselected = longi[i];
        System.out.println("**** selected address has name = " + placename[i] + "     lat = " + latselected + "     long = " + longselected);
        Intent output = new Intent();
        output.putExtra("latitude", latselected);
        output.putExtra("longitude", longselected);
        setResult(RESULT_OK, output);
        finish();

        // start previous activity with this data

    }


    public void initialize() {
        rowItems = new ArrayList<RowItemForHistory>();
        System.out.println("**** lengths  " + placename.length);
        if (!placename[0].equals("")) {
            for (int i = 0; i < placename.length; i++) {
                RowItemForHistory item;
                if (placename[i].length() == 0) {
                    item = new RowItemForHistory("Location not found", "NA" + "", lat[i], longi[i], datetime[i]);
                } else {
                    item = new RowItemForHistory(placename[i], placename[i].charAt(0) + "", lat[i], longi[i], datetime[i]);
                }
                rowItems.add(item);
            }
        }
    }


}
