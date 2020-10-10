package com.example.night_friend.matching;

public class userdistance extends matching_data implements Comparable<userdistance>{

    private String userId;
    private double distance;

    public userdistance(String userId, double lat, double lon,double destlat,double destlon, double distance){
        super(userId,lat,lon,destlat,destlon);
        this.distance=distance;
    }


    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }


    @Override
    public int compareTo(userdistance o) {
        if (this.distance < o.getDistance()) {
            return -1;
        } else if (this.distance > o.getDistance()) {
            return 1;
        }
        return 0;

    }
}
