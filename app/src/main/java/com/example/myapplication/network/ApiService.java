package com.example.myapplication.network;

import com.example.myapplication.network.model.ActiveNetwork;
import com.example.myapplication.network.model.DayOfWeek;
import com.example.myapplication.network.model.LocationNetwork;
import com.example.myapplication.network.model.MessageNetwork;
import com.example.myapplication.network.model.StatisticalNetwork;
import com.example.myapplication.network.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    Gson gson = new GsonBuilder().create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl("https://dcs3.onrender.com/") //"http://10.0.2.2:5000/" https://dcs3.onrender.com
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(ApiService.class);

    @GET("user/{id}")
    Call<User> getUser(@Path("id") String id);

    @POST("login")
    Call<User> login(@Body User user);

    @POST("register")
    Call<MessageNetwork> register(@Body User user);

    @GET("actives/{id}")
    Call<List<ActiveNetwork>> getActives(@Path("id") String id);

    @GET("statistical/{id}")
    Call<StatisticalNetwork> statistical(@Path("id") String id);

    @PUT("update-user/{id}")
    Call<User> updateUser(@Path("id") String id, @Body User user);

    @DELETE("delete-user/{id}")
    Call<MessageNetwork> deleteUser(@Path("id") String id);

    @GET("get-day-of-week/{id}")
    Call<DayOfWeek> getDayOfWeek(@Path("id") String id);

    @GET("get-day-of-week-distance/{id}")
    Call<DayOfWeek> getDayOfWeekDistance(@Path("id") String id);

    @POST("add-active/")
    Call<MessageNetwork> addActive(@Body ActiveNetwork activeNetwork);

    @POST("add-location/{id}")
    Call<MessageNetwork> addLoction(@Path("id") String id, @Body List<LocationNetwork> locationNetworkList);

    @GET("location/{id}")
    Call<ArrayList<LocationNetwork>> getLocation(@Path("id") String id);

}
