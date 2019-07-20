package com.example.glypmse_clone.Models;

public class Glympse {
    private User sender, receiver;
    private double expiryTimeMins;
    private boolean isActive;
    private String dateTime, message;
    private Destination destination;

    public Glympse() {
    }

    public Glympse(User sender, User receiver,String message, double expiryTimeMins, boolean isActive, String dateTime, Destination destination) {
        this.sender = sender;
        this.receiver = receiver;
        this.expiryTimeMins = expiryTimeMins;
        this.isActive = isActive;
        this.dateTime = dateTime;
        this.destination = destination;
        this.message=message;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public double getExpiryTimeMins() {
        return expiryTimeMins;
    }

    public void setExpiryTimeMins(double expiryTimeMins) {
        this.expiryTimeMins = expiryTimeMins;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }
}
