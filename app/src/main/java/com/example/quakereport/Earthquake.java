package com.example.quakereport;

public class Earthquake {
    private double mMagnitude;
    private String mLocation;
    private long mTimemillisecs;
    private String mUrl;

    public Earthquake(double magnitude, String location , long timeMillisecs, String url){
        mMagnitude = magnitude;
        mLocation = location;
        mTimemillisecs = timeMillisecs;
        mUrl = url;
    }

    public long getmDate() {
        return mTimemillisecs;
    }

    public String getmLocation() {
        return mLocation;
    }

    public double getmMagnitude() {
        return mMagnitude;
    }

    public String getmUrl() {
        return mUrl;
    }
}
