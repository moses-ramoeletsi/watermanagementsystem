package com.example.watermanagementsystem;

import android.os.Parcel;
import android.os.Parcelable;

public class UserDetails implements Parcelable {
    private String userId;  // Added field for Firebase user ID
    private String name;
    private String nationalId;
    private String email;
    private String residentialAddress;
    private String physicalAddress;
    private String contacts;
    private String role;

    public UserDetails() {
        // Required empty constructor for Firestore
    }

    public UserDetails(String userId, String name, String nationalId, String email,
                       String residentialAddress, String physicalAddress,
                       String contacts) {
        this.userId = userId;
        this.name = name;
        this.nationalId = nationalId;
        this.email = email;
        this.residentialAddress = residentialAddress;
        this.physicalAddress = physicalAddress;
        this.contacts = contacts;
        this.role = role;
    }

    // Getters and setters for existing fields...
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(String residentialAddress) {
        this.residentialAddress = residentialAddress;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Parcelable implementation
    protected UserDetails(Parcel in) {
        userId = in.readString();
        name = in.readString();
        nationalId = in.readString();
        email = in.readString();
        residentialAddress = in.readString();
        physicalAddress = in.readString();
        contacts = in.readString();
        role = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(name);
        dest.writeString(nationalId);
        dest.writeString(email);
        dest.writeString(residentialAddress);
        dest.writeString(physicalAddress);
        dest.writeString(contacts);
        dest.writeString(role);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserDetails> CREATOR = new Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel in) {
            return new UserDetails(in);
        }

        @Override
        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };
}