package com.example.night_friend.main_map;

public class mPoint {
    private String mediName;
    private double latitude;
    private double longitude;

    public mPoint(){
        super();
    }
    public mPoint(String mediName, double latitude, double longitude){
        this.mediName=mediName;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public String getMediName(){
        return mediName;
    }

    public void setMediName(String mediName){
        this.mediName=mediName;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLatitude(double latitude){
        this.latitude=latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setLongitude(double longitude){
        this.longitude=longitude;
    }
}
