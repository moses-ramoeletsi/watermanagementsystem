package com.example.watermanagementsystem;

public class WaterAuthorityModel {
    private String uid;
    private String name;
    private String contacts;
    private String address;

    public WaterAuthorityModel (String uid, String name, String contacts, String address) {
        this.uid = uid;
        this.name = name;
        this.contacts = contacts;
        this.address = address;
    }

    // Getters
    public String getUid () {
        return uid;
    }

    public String getName () {
        return name;
    }

    public String getContacts () {
        return contacts;
    }

    public String getAddress () {
        return address;
    }
}