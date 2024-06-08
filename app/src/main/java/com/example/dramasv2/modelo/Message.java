package com.example.dramasv2.modelo;

public class Message {
    public String senderId;
    public String text;
    public long timestamp;
    public String senderName;

    public Message() {
    }

    public Message(String senderId, String text, long timestamp, String senderName) {
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
        this.senderName = senderName;
    }
}
