package com.example.night_friend.matching;

import java.io.Serializable;

public class ChatData implements Serializable {
    private String msg;
    private String name;

    public ChatData(){}

    public ChatData(String name, String msg){
        this.msg = msg;
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
