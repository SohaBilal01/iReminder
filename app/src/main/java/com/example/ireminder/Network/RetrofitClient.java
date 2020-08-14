package com.example.ireminder.Network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient
{
    private static final String BASE_URL="http://192.168.100.8:5000/predict/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitClient()
    {
        if(retrofit==null)
        {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
