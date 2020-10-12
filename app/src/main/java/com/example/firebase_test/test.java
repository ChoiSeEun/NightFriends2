package com.example.firebase_test;

public class test {

    double Latitude;
    double Longitude;
    public test(){

    }
    test(Double Latitude, Double Longitude){
        this.Latitude=Latitude;
        this.Longitude=Longitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }


}
