package com.example.favouritelocations;

public class LocationData {
    public double latitude;
    public double longitude;
    public String title;
    public String description;
    public float rating;

    public LocationData(double latitude, double longitude, String title, String description, float rating) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.description = description;
        this.rating = rating;
    }
}