package com.example.glypmse_clone.Models;

import com.google.android.gms.maps.model.LatLng;

public class User {
    private String name, phoneNumber, country;


    private LatLng lastPosition;

    public User() {
    }

    public User(String name, String phoneNumber, String country ,LatLng lastPosition) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.lastPosition = lastPosition;
        this.country=country;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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
}
