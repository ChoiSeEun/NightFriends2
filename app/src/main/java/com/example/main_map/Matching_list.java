package com.example.main_map;

public class Matching_list {
    String time;
    String road;
    String score;
    String text;

    public Matching_list(String time, String road, String score, String text){
        this.time = time;
        this.road = road;
        this.score = score;
        this.text = text;
    }
    public void setTime(String time){
        this.time = time;
    }
    public String getTime(){
        return time;
    }
    public void setRoad(String road){
        this.road = road;
    }
    public String getRoad(){
        return road;
    }
    public void setScore(String score){
        this.score = score;
    }
    public String getScore(){
        return score;
    }
    public void setText(String text){
        this.text = text;
    }
    public String getText(){
        return text;
    }
}
