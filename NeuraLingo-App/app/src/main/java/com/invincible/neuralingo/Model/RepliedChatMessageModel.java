package com.invincible.neuralingo.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RepliedChatMessageModel implements Serializable {
    @SerializedName("repliedToUserId")
    String repliedToUserId;
    @SerializedName("repliedPosition")
    int repliedPosition;
    @SerializedName("repliedToUserName")
    String repliedToUserName;
    @SerializedName("repliedToMessageId")
    String repliedToMessageId;
    @SerializedName("repliedMessageMap")
    Map<String, String> repliedMessageMap = new HashMap<>();

    public RepliedChatMessageModel()
    {

    }

    public RepliedChatMessageModel(String repliedToUserId, int repliedPosition, String repliedToUserName)
    {
        this.repliedPosition = repliedPosition;
        this.repliedToUserId = repliedToUserId;
        this.repliedToUserName = repliedToUserName;
    }

    public void setRepliedMessageMap(Map<String, String> repliedMessageMap) {
        this.repliedMessageMap = repliedMessageMap;
    }

    public Map<String, String> getRepliedMessageMap() {
        return repliedMessageMap;
    }

    public String getRepliedToMessage(String key)
    {
        return repliedMessageMap.get(key);
    }

    public void setRepliedPosition(int repliedPosition) {
        this.repliedPosition = repliedPosition;
    }

    public int getRepliedPosition() {
        return repliedPosition;
    }

    public void setRepliedToUserId(String repliedToUserId) {
        this.repliedToUserId = repliedToUserId;
    }

    public String getRepliedToMessageId() {
        return repliedToMessageId;
    }

    public void setRepliedToMessageId(String repliedToMessageId) {
        this.repliedToMessageId = repliedToMessageId;
    }

    public String getRepliedToUserId() {
        return repliedToUserId;
    }

    public String getRepliedToUserName() {
        return repliedToUserName;
    }

    public void setRepliedToUserName(String repliedToUserName) {
        this.repliedToUserName = repliedToUserName;
    }
}
