package com.example.smartwaste;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface RetrofitAPI {
    @Headers({
            "X-NCP-APIGW-API-KEY-ID: 3mnxhpaoam",
            "X-NCP-APIGW-API-KEY: zNG25t0CxdJh5a7eAbWsD6PJLd1vMGL2BoDDeBNK",
    })
    @GET("map-geocode/v2/geocode")
    Call<GeocodeResultVO> getGeocodeResult(@Query("query") String query);
}
