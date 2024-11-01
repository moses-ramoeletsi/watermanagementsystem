package com.example.watermanagementsystem;

public class UserDetails {

    private String nationalId;
    private String email;
    private String  residentialAddress;
    private String contacts;
    private String role;

    public UserDetails(String nationalId, String email, String residentialAddress, String contacts, String role) {
        this.nationalId = nationalId;
        this.email = email;
        this.residentialAddress = residentialAddress;
        this.contacts = contacts;
        this.role = role;
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
}
