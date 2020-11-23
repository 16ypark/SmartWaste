package com.example.smartwaste;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitAPI {
    @GET("/movie.json")
    Call<String> getGeocodeResult();
}
