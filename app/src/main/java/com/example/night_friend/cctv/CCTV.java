package com.example.night_friend.cctv;

public class CCTV {
    String addr="";
    Double Xpos;
    Double Ypos;
    public CCTV(){

    }
    CCTV(String addr, Double Xpos, Double Ypos){
        this.addr=addr;
        this.Xpos=Xpos;
        this.Ypos=Ypos;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void setXpos(Double xpos) {
        Xpos = xpos;
    }

    public void setYpos(Double ypos) {
        Ypos = ypos;
    }

    public String getAddr() {
        return addr;
    }

    public Double getXpos() {
        return Xpos;
    }

    public Double getYpos() {
        return Ypos;
    }
}
