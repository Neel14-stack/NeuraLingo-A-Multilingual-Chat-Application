package com.invincible.neuralingo.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class ApiDataModel {
    @SerializedName("message")
    private final String message;
    @SerializedName("message_id")
    private final String messageId;
    @SerializedName("room_name")
    private final String roomName;
    @SerializedName("sender_id")
    private final String senderId;
    @SerializedName("t_keys")
    private ArrayList<String> modelName;
    @SerializedName("position")
    int position;
    @SerializedName("replied")
    private boolean replied = false;
    @SerializedName("repliedModel")
    private ChatMessageModel repliedChatMessageModel;

    public ApiDataModel(String message, String messageId, String roomName, String senderId)
    {
        this.message = message;
        this.messageId = messageId;
        this.roomName = roomName;
        this.senderId = senderId;
        this.modelName = new ArrayList<>();
    }

    public void addModelName(String modelName) {
        this.modelName.add(modelName);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ArrayList<String> getModelName() {
        return modelName;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public int getPosition() {
        return position;
    }

    public void setReplied(boolean replied) {
        this.replied = replied;
    }

    public boolean isReplied() {
        return replied;
    }

    public void setRepliedChatMessageModel(ChatMessageModel repliedChatMessageModel) {
        this.repliedChatMessageModel = repliedChatMessageModel;
    }

    public ChatMessageModel getRepliedChatMessageModel() {
        return repliedChatMessageModel;
    }
}
