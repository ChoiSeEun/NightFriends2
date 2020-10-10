package com.example.night_friend.matching;

public class BUS {
    Double Xpos;
    Double Ypos;
    public BUS(){

    }
    public BUS(Double Xpos, Double Ypos){
        this.Xpos=Xpos;
        this.Ypos=Ypos;
    }

    public Double getXpos() {
        return Xpos;
    }

    public void setXpos(Double xpos) {
        Xpos = xpos;
    }

    public Double getYpos() {
        return Ypos;
    }

    public void setYpos(Double ypos) {
        Ypos = ypos;
    }
}
