package com.example.m8googlemaps.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiCall {
    @GET("json?")
    Call<ModelApi> getData(@Query("lat") String lat, @Query("lng") String lng);


    @GET("?method=flickr.photos.search&api_key=79d466885188b99d6762980d64029892&lat=41.3879&lon=2.16992&format=json&nojsoncallback=1")
    Call<ModelApi> getPhotos();


}