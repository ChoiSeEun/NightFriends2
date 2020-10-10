package com.example.night_friend.matching;


public class Locationitem {

    private String place_name;
    private String phone;
    private double x,y;
    private String address;

    // 중심좌표까지의 거리 -> x,y 피라미터 필요(단위 미터)
    private String distance;

    public  Locationitem(){}

    public Locationitem(String place_name, String address, double x, double y){
        this.place_name=place_name;
        this.x=x;
        this.y=y;
        this.address = address;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String road_address_name) {
        this.address = road_address_name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Locationitem{");

        sb.append("place_name='").append(place_name).append('\'');
        sb.append(", x='").append(x).append('\'');
        sb.append(", y='").append(y).append('\'');
        sb.append('}');

        return sb.toString();
    }
}
