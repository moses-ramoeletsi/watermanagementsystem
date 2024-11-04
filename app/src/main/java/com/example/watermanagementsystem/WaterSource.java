package com.example.watermanagementsystem;

import java.util.Date;

public class WaterSource {
    private String sourceName;
    private String description;
    private String directions;
    private String status;
    private Date lastMaintenanceDate;
    private String userEmail;
    private String userContact;
    private String documentId;

    public WaterSource() {}

    public WaterSource(String sourceName, String description, String directions,
                       String status, Date lastMaintenanceDate,
                       String userEmail, String userContact) {
        this.sourceName = sourceName;
        this.description = description;
        this.directions = directions;
        this.status = status;
        this.lastMaintenanceDate = lastMaintenanceDate;
        this.userEmail = userEmail;
        this.userContact = userContact;
    }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDirections() { return directions; }
    public void setDirections(String directions) { this.directions = directions; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getLastMaintenanceDate() { return lastMaintenanceDate; }
    public void setLastMaintenanceDate(Date lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserContact() { return userContact; }
    public void setUserContact(String userContact) { this.userContact = userContact; }
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}