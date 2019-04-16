package com.fivenine.sharebutter.models;

public class Chat {
    private String message;
    private String sender;
    private String senderImgUrl;
    private String receiver;
    private Long timeInMillis;
    private Long messageChannelId;

    public Chat(){
    }

    public Chat(String message, String sender, String receiver, Long timeInMillis, Long messageChannelId){
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.timeInMillis = timeInMillis;
        this.messageChannelId = messageChannelId;
    }

    public String getMessage() {
        return message;
    }
    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public Long getTimeInMillis() {
        return timeInMillis;
    }

    public Long getMessageChannelId() {
        return messageChannelId;
    }

    public String getSenderImgUrl() {
        return senderImgUrl;
    }

    public void setSenderImgUrl(String senderImgUrl) {
        this.senderImgUrl = senderImgUrl;
    }
}
