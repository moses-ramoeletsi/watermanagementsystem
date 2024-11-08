package com.example.watermanagementsystem;

public class UserModel {
    private String uid;
    private String name;
    private String nationalId;


    private String contacts;
    private String residentialAddress;

    public UserModel (String uid, String name, String nationalId, String contacts, String residentialAddress) {
        this.uid = uid;
        this.name = name;
        this.nationalId = nationalId;
        this.contacts = contacts;
        this.residentialAddress = residentialAddress;
    }

    // Getters and setters
    public String getUid () {
        return uid;
    }

    public String getName () {
        return name;
    }

    public String getNationalId () {
        return nationalId;
    }

    public String getContacts () {
        return contacts;
    }

    public String getResidentialAddress () {
        return residentialAddress;
    }
}
