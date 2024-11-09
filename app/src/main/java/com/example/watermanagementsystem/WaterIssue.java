package com.example.watermanagementsystem;


public class WaterIssue {
    private String id;
    private String issueType;
    private String location;
    private String status;
    private String timestamp;
    private UserInfo userInfo;
    private String lastUpdatedBy;
    private String lastUpdatedAt;

    public WaterIssue (String id, String issueType, String location, String status,
                       String timestamp, UserInfo userInfo, String lastUpdatedBy, String lastUpdatedAt) {
        this.id = id;
        this.issueType = issueType;
        this.location = location;
        this.status = status;
        this.timestamp = timestamp;
        this.userInfo = userInfo;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public WaterIssue () {
    }

    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getIssueType () {
        return issueType;
    }

    public void setIssueType (String issueType) {
        this.issueType = issueType;
    }

    public String getLocation () {
        return location;
    }

    public void setLocation (String location) {
        this.location = location;
    }

    public String getStatus () {
        return status;
    }

    public void setStatus (String status) {
        this.status = status;
    }

    public String getTimestamp () {
        return timestamp;
    }

    public void setTimestamp (String timestamp) {
        this.timestamp = timestamp;
    }

    public UserInfo getUserInfo () {
        return userInfo;
    }

    public void setUserInfo (UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getLastUpdatedBy () {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy (String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getLastUpdatedAt () {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt (String lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}

class UserInfo {
    private String email;
    private String phoneNumber;
    private String uid;

    public UserInfo () {
    }

    public UserInfo (String email, String phoneNumber, String uid) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.uid = uid;
    }

    public String getEmail () {
        return email;
    }

    public void setEmail (String email) {
        this.email = email;
    }

    public String getPhoneNumber () {
        return phoneNumber;
    }

    public void setPhoneNumber (String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUid () {
        return uid;
    }

    public void setUid (String uid) {
        this.uid = uid;
    }
}