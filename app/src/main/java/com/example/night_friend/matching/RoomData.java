package com.example.night_friend.matching;

public class RoomData {

    String roomName;
    ChatData lastChat;

    public RoomData(){}

    public RoomData(String name, ChatData lastChat){
        roomName = name;
        this.lastChat = lastChat;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public ChatData getLastChat() {
        return lastChat;
    }

    public void setLastChat(ChatData lastChat) {
        this.lastChat = lastChat;
    }
}
