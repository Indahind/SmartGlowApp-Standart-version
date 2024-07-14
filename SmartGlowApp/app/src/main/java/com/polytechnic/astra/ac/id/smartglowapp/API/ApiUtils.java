package com.polytechnic.astra.ac.id.smartglowapp.API;
import com.polytechnic.astra.ac.id.smartglowapp.API.Service.MyService;
public class ApiUtils {
    public static final String API_URL_1 = "https://api.api-ninjas.com/";
    public static final String API_URL_2 = "http://10.5.0.118:8080/";

    public ApiUtils() {
    }

    public static MyService getMyService(){
        return RetrofitClient.getClient(API_URL_1).create(MyService.class);
    }

    public static MyService getMyService2(){
        return RetrofitClient.getClient(API_URL_2).create(MyService.class);
    }
}
