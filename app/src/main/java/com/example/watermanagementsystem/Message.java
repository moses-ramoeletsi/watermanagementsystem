package com.example.watermanagementsystem;

public class Message {
    private String messageId;
    private String senderId;


    private String senderName;
    private String recipientId;
    private String subject;
    private String content;

    private boolean read;

    public Message() {

    }

    public Message(String senderId, String senderName, String recipientId, String subject,
                   String content) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.recipientId = recipientId;
        this.subject = subject;
        this.content = content;

        this.read = false;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }


    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
