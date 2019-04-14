package com.fivenine.sharebutter.models;

public class MessageChannel {

    private long id;
    private String senderId;
    private String receiverId;
    private String latestMessage;
    private String latestMessageTime;
    private int unSeenMessages;

    public MessageChannel(){}

    public MessageChannel(long id, String senderId, String receiverId,
                          String latestMessage, String latestMessageTime, int unSeenMessages) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.latestMessage = latestMessage;
        this.latestMessageTime = latestMessageTime;
        this.unSeenMessages = unSeenMessages;
    }

    public long getId() {
        return id;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public int getUnSeenMessages() {
        return unSeenMessages;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public String getLatestMessageTime() {
        return latestMessageTime;
    }

    public void setUnSeenMessages(int unSeenMessages) {
        this.unSeenMessages = unSeenMessages;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }

    public void setLatestMessageTime(String latestMessageTime) {
        this.latestMessageTime = latestMessageTime;
    }
}
