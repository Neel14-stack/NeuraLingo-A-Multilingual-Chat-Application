package com.invincible.neuralingo.Model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class ChatMessageModel{
    @SerializedName("messageMap")
    private Map<String, String> messageMap = new HashMap<>();
    @SerializedName("sender_id")
    private String senderId;
    @SerializedName("message_id")
    private String messageId;
    @SerializedName("received")
    private int received=-1;
    @SerializedName("replied")
    private boolean isReplied = false;
    @SerializedName("position")
    private int position;
    @SerializedName("repliedModel")
    private ChatMessageModel chatMessageModel;
    public ChatMessageModel()
    {
        super();
        received = -1;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setReplied(boolean replied) {
        isReplied = replied;
    }

    public boolean isReplied() {
        return isReplied;
    }

    public int getReceived() {
        return received;
    }

    public void setReceived(int received) {
        this.received = received;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setMessageMap(Map<String, String> messageMap) {
        this.messageMap = messageMap;
    }

    public void setSender_id(String senderId) {
        this.senderId = senderId;
    }

    public String getSender_id() {
        return senderId;
    }

    public String getMessageId() {
        return messageId;
    }

    public Map<String, String> getMessageMap() {
        return messageMap;
    }

    public String getMessage(String langKey)
    {
        return messageMap.get(langKey);
    }

    public ChatMessageModel getRepliedModel()
    {
        return this.chatMessageModel;
    }
    public void setRepliedModel(ChatMessageModel chatMessageModel)
    {
        this.chatMessageModel = chatMessageModel;
    }

}
