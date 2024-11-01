package com.example.watermanagementsystem;

public class FeedBack {


    private String feedbackId;
    private String userEmail;
    private String userContacts;
    private String feedbackType;
    private String details;

    public FeedBack() {}

    public FeedBack(String feedbackId, String userEmail, String userContacts, String feedbackType, String details) {
        this.feedbackId = feedbackId;
        this.userEmail = userEmail;
        this.userContacts = userContacts;
        this.feedbackType = feedbackType;
        this.details = details;
    }

    public String getFeedbackId() { return feedbackId; }
    public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getUserContacts() { return userContacts; }
    public void setUserContacts(String userContacts) { this.userContacts = userContacts; }
    public String getFeedbackType() { return feedbackType; }
    public void setFeedbackType(String feedbackType) { this.feedbackType = feedbackType; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
