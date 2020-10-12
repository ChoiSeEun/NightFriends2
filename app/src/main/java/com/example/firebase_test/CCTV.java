package com.example.firebase_test;

public class CCTV {
    Double Xpos;
    Double Ypos;
    public CCTV(){

    }
    CCTV( Double Xpos, Double Ypos){
        this.Xpos=Xpos;
        this.Ypos=Ypos;
    }

    public void setXpos(Double xpos) {
        Xpos = xpos;
    }

    public void setYpos(Double ypos) {
        Ypos = ypos;
    }

    public Double getXpos() {
        return Xpos;
    }

    public Double getYpos() {
        return Ypos;
    }
}
