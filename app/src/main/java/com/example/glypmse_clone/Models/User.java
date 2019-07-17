package com.example.glypmse_clone.Models;

public class User {
    String name, phoneNumber, country;
    public LatLng lastPosition;


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

    public LatLng getlastPosition(){
        return lastPosition;
    }

    public void setLastPosition(LatLng lastPosition) {
        this.lastPosition = new LatLng(lastPosition.latitude,lastPosition.longitude);
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
