package com.example.night_friend.matching;

import java.io.Serializable;

public class matching_data implements Serializable {
    private String id;
    private double userLat;
    private double userLon;
    private double destLat;
    private double destLon;
    private double safeIndex;
    private int userAns;

    public matching_data(String id, double userLat, double userLon,double destLat,double destLon){
        this.id = id;
        this. userLat = userLat;
        this.userLon = userLon;
        this. destLat = destLat;
        this.destLon = destLon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getUserLat() {
        return userLat;
    }

    public void setUserLat(double userLat) {
        this.userLat = userLat;
    }

    public double getUserLon() {
        return userLon;
    }

    public void setUserLon(double userLon) {
        this.userLon = userLon;
    }

    public double getDestLat() {
        return destLat;
    }

    public void setDestLat(double destLat) {
        this.destLat = destLat;
    }

    public double getDestLon() {
        return destLon;
    }

    public void setDestLon(double destLon) {
        this.destLon = destLon;
    }

    public double getSafeIndex() {
        return safeIndex;
    }

    public void setSafeIndex(double safeIndex) {
        this.safeIndex = safeIndex;
    }

    public int getUserAns() {
        return userAns;
    }

    public void setUserAns(int userAns) {
        this.userAns = userAns;
    }
}