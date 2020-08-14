package com.example.ireminder.Network;

import com.example.ireminder.LocationModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface NetworkService {
    @GET
    Call<LocationModel> sendData(@Url String url);
}
