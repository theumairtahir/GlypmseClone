package com.example.glypmse_clone.Models;

import com.google.android.gms.maps.model.LatLng;

public class Destination {
    private LatLng destination;
    private String name;


    private double timeRequiredMins;

    public Destination() {
    }

    public Destination(LatLng destination, String name, double timeRequiredMins) {
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
