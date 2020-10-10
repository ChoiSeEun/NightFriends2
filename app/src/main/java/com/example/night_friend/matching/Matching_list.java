package com.example.night_friend.matching;

public class Matching_list {
    Integer time;
    String road;
    Double score;
    String text;

    public Matching_list(Integer time, String road, Double score, String text){
        this.time = time;
        this.road = road;
        this.score = score;
        this.text = text;
    }
    public void setTime(Integer time){
        this.time = time;
    }
    public Integer getTime(){
        return time;
    }
    public void setRoad(String road){
        this.road = road;
    }
    public String getRoad(){
        return road;
    }
    public void setScore(Double score){
        this.score = score;
    }
    public Double getScore(){
        return score;
    }
    public void setText(String text){
        this.text = text;
    }
    public String getText(){
        return text;
    }
}