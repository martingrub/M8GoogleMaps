package com.example.m8googlemaps.model;

public class ModelApi {
    public String status;
    public ModelResults results;
    public String stat;
    public ModelPhotos photos;

    public ModelPhotos getPhotos() {
        return photos;
    }
    public String getStat() {
        return stat;
    }
    public String getStatus() {
        return status;
    }

    public ModelResults getResults() {
        return results;
    }
}