package com.example.glypmse_clone.Models;

import com.google.android.gms.maps.model.LatLng;

public class User {
    private String name, phoneNumber, userName, password;


    private LatLng lastPosition;

    public User() {
    }

    public User(String name, String phoneNumber, String userName, String password, LatLng lastPosition) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.userName = userName;
        this.password = password;
        this.lastPosition = lastPosition;
    }

    public LatLng getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(LatLng lastPosition) {
        this.lastPosition = lastPosition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
