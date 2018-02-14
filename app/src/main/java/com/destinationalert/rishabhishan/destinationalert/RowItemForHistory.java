package com.destinationalert.rishabhishan.destinationalert;

/**
 * Created by rishu on 3/8/2016.
 */
public class RowItemForHistory {


    private String placeName;
    private String lat;
    private String longi;

    private String dateTime;
    private String startletter;

    public RowItemForHistory(String placeName, String startletter, String lat, String longi, String dateTime) {

        this.placeName = placeName;
        this.startletter = startletter;
        this.lat = lat;
        this.longi = longi;
        this.dateTime = dateTime;

    }

    public String getPlaceName() {
        return placeName;
    }

    public String getstartLetter() {
        return startletter;
    }


    public String getDateTime() {
        return dateTime;
    }

    public String getLat() {
        return lat;

    }

    public String getLong() {
        return lat;

    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }


    public void setstartLetter(String startletter) {
        this.startletter = startletter;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLong(String longi) {
        this.longi = longi;
    }

}
