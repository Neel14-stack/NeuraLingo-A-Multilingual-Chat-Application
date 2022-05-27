package com.invincible.neuralingo.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatRoomModel implements Serializable {
    String roomName;
    String imageString;
    String lastMessages;
    boolean joined;
    ArrayList<String> language = new ArrayList<>();
    public ChatRoomModel()
    {

    }

    public void setLastMessages(String lastMessages) {
        this.lastMessages = lastMessages;
    }

    public String getLastMessages() {
        return lastMessages;
    }

    public String getImageString() {
        return imageString;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public boolean isJoined() {
        return joined;
    }

    public ArrayList<String> getLanguage()
    {
        return language;
    }
    public void setLanguage(ArrayList<String> language) {
        this.language = language;
    }
}

