
package com.example.watermanagementsystem;

public class Message {
    private String messageId;
    private String senderId;
    private String senderName;
    private String senderContacts;
    private String recipientId;
    private String subject;
    private String content;
    private String response;
    private String responseAuthorId;

    public Message () {
    }

    public Message (String senderId, String senderName, String senderContacts, String recipientId,
                    String subject, String content) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderContacts = senderContacts;
        this.recipientId = recipientId;
        this.subject = subject;
        this.content = content;
    }

    // Getters and Setters
    public String getMessageId () {
        return messageId;
    }

    public void setMessageId (String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId () {
        return senderId;
    }

    public void setSenderId (String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName () {
        return senderName;
    }

    public void setSenderName (String senderName) {
        this.senderName = senderName;
    }

    public String getSenderContacts () {
        return senderContacts;
    }

    public void setSenderContacts (String senderContacts) {
        this.senderContacts = senderContacts;
    }

    public String getRecipientId () {
        return recipientId;
    }

    public void setRecipientId (String recipientId) {
        this.recipientId = recipientId;
    }

    public String getSubject () {
        return subject;
    }

    public void setSubject (String subject) {
        this.subject = subject;
    }

    public String getContent () {
        return content;
    }

    public void setContent (String content) {
        this.content = content;
    }

    public String getResponse () {
        return response;
    }

    public void setResponse (String response) {
        this.response = response;
    }

    public String getResponseAuthorId () {
        return responseAuthorId;
    }

    public void setResponseAuthorId (String responseAuthorId) {
        this.responseAuthorId = responseAuthorId;
    }
}