package com.mobileappclass.assignment3;

/**
 * Created by Aneesh on 11/6/2016.
 */
public class GPSDataDb {

    ///////// ID, Date-time, Lat, Long////////
    private int _id;
    private String _dateTime;
    private String _lat;
    private String _long;

    /////////////////// Constructors //////////////////////////////////////
    public GPSDataDb(){

    }

    public GPSDataDb(String _dateTime, String _lat, String _long){
        this._dateTime = _dateTime;
        this._lat = _lat;
        this._long = _long;
    }

    //////////////////////// Getters //////////////////////////////////
    public int get_id() {
        return _id;
    }

    public String get_dateTime() {
        return _dateTime;
    }

    public String get_lat() {
        return _lat;
    }

    public String get_long() {
        return _long;
    }

    //////////////////////////////////// Setters ////////////////////////////////
    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_dateTime(String _dateTime) {
        this._dateTime = _dateTime;
    }

    public void set_lat(String _lat) {
        this._lat = _lat;
    }

    public void set_long(String _long) {
        this._long = _long;
    }
}
