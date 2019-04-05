package com.fivenine.sharebutter.models;

public class Message {
    private String message;
    private String sender;
    private String receiver;
    private Long timeInMillis;

    public Message(){
    }

    public Message(String message, String sender, String receiver, Long timeInMillis){
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.timeInMillis = timeInMillis;
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
}
