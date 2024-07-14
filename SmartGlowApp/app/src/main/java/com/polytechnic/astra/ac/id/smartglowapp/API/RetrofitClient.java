package com.polytechnic.astra.ac.id.smartglowapp.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit sRetrofitFirebase = null;
    private static Retrofit sRetrofitCustom = null;

    public static Retrofit getClient(String url){
        if (sRetrofitCustom == null){
            sRetrofitCustom = new Retrofit.Builder().baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return sRetrofitCustom;
    }

    public static Retrofit getClientFirebase(String url){
        if (sRetrofitFirebase == null){
            sRetrofitFirebase = new Retrofit.Builder().baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return sRetrofitFirebase;
    }
}
