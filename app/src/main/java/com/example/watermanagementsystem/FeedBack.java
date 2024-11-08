package com.example.watermanagementsystem;

public class FeedBack {


    private String feedbackId;
    private String userEmail;
    private String userContacts;
    private String feedbackType;
    private String details;

    private String adminResponse;
    private String adminResponseId;
    private long responseTimestamp;

    public FeedBack () {
    }

    public FeedBack (String feedbackId, String userEmail, String userContacts, String feedbackType, String details, String adminResponse, String adminResponseId, long responseTimestamp) {
        this.feedbackId = feedbackId;
        this.userEmail = userEmail;
        this.userContacts = userContacts;
        this.feedbackType = feedbackType;
        this.details = details;
        this.adminResponse = adminResponse;
        this.adminResponseId = adminResponseId;
        this.responseTimestamp = responseTimestamp;
    }

    public String getFeedbackId () {
        return feedbackId;
    }

    public void setFeedbackId (String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getUserEmail () {
        return userEmail;
    }

    public void setUserEmail (String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserContacts () {
        return userContacts;
    }

    public void setUserContacts (String userContacts) {
        this.userContacts = userContacts;
    }

    public String getFeedbackType () {
        return feedbackType;
    }

    public void setFeedbackType (String feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getDetails () {
        return details;
    }

    public void setDetails (String details) {
        this.details = details;
    }

    public String getAdminResponse () {
        return adminResponse;
    }

    public void setAdminResponse (String adminResponse) {
        this.adminResponse = adminResponse;
    }

    public String getAdminResponseId () {
        return adminResponseId;
    }

    public void setAdminResponseId (String adminResponseId) {
        this.adminResponseId = adminResponseId;
    }

    public long getResponseTimestamp () {
        return responseTimestamp;
    }

    public void setResponseTimestamp (long responseTimestamp) {
        this.responseTimestamp = responseTimestamp;
    }
}
