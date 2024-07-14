package com.polytechnic.astra.ac.id.smartglowapp.API.Service;

import com.polytechnic.astra.ac.id.smartglowapp.API.VO.MyVO;
import com.polytechnic.astra.ac.id.smartglowapp.Model.MyModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface MyService {
    @GET("getDataVO")
    Call<List<MyVO>> getMyVO();

    @GET("v1/animals")
    Call<List<MyModel>> getMyModel(
        @Header("x-api-key") String token,
        @Query("name") String name
    );

    @GET("v1/animals")
    Call<List<MyModel>> getMyModel(
            @Body MyModel model
    );
}
