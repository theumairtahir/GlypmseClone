package com.example.glypmse_clone.Models;


public class Destination {
    private com.example.glypmse_clone.Models.LatLng destination;
    private String name;


    private double timeRequiredMins;

    public Destination() {
    }

    public Destination(com.example.glypmse_clone.Models.LatLng destination, String name, double timeRequiredMins) {
        this.destination = destination;
        this.name = name;
        this.timeRequiredMins = timeRequiredMins;
    }

    public void setTimeRequiredMins(double timeRequiredMins) {
        this.timeRequiredMins = timeRequiredMins;
    }

    public LatLng getDestination() {
        return destination;
    }

    public String getName() {
        return name;
    }
    public double getTimeRequiredMins() {
        return timeRequiredMins;
    }
}
