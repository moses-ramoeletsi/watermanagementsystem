package com.example.watermanagementsystem;


import com.google.firebase.Timestamp;

public class WaterIssue {
    private String id;
    private String issueType;
    private String location;
    private String status;
    private String imageUrl;
    private Timestamp timestamp;

    public WaterIssue(String id, String issueType, String location, String status, String imageUrl, Timestamp timestamp) {
        this.id = id;
        this.issueType = issueType;
        this.location = location;
        this.status = status;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public WaterIssue() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
