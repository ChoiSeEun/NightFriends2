package com.example.night_friend.main_map;

public class test {
    private String location;
    private int number;
    private String address_road;
    private String address_land;
    private double latitude;
    private double longitude;
    private String install_year;
    private String install_form;
    private String admin_phoneNo;
    private String admin_name;
    private String data_basicDate;

    public String getLocation(){
        return location;
    }

    public void setLocation(String location){
        this.location=location;
    }
    public int getNumber(){
        return number;
    }

    public void setNumber(int number){
        this.number=number;
    }

    public String getAddress_road(){
        return address_road;
    }

    public void setAddress_road(String address_road){
        this.address_road=address_road;
    }

    public String getAddress_land(){
        return address_land;
    }


    public void setAddress_land(String address_land){
        this.address_land=address_land;
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

    public String getInstall_year(){
        return install_year;
    }

    public void setInstall_year(String install_year){
        this.install_year=install_year;
    }

    public String getInstall_form(){
        return install_form;
    }

    public void setInstall_form(String install_form){
        this.install_form=install_form;
    }

    public String getAdmin_phoneNo(){
        return admin_phoneNo;
    }

    public void setAdmin_phoneNo(String admin_phoneNo){
        this.admin_phoneNo=admin_phoneNo;
    }

    public String getAdmin_name(){
        return admin_name;
    }

    public void setAdmin_name(String admin_name){
        this.admin_name=admin_name;
    }

    public String getData_basicDate(){
        return data_basicDate;
    }

    public void setData_basicDate(String data_basicDate){
        this.data_basicDate=data_basicDate;
    }

    @Override
    public String toString() {
        return "test{" +
                "location='" + location + '\'' +
                ", number=" + number +
                ", address_road='" + address_road + '\'' +
                ", address_land='" + address_land + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", install_year='" + install_year + '\'' +
                ", install_form='" + install_form + '\'' +
                ", admin_phoneNo='" + admin_phoneNo + '\'' +
                ", admin_name='" + admin_name + '\'' +
                ", data_basicDate='" + data_basicDate + '\'' +
                '}';
    }
}
