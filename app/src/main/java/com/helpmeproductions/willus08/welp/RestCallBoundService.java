package com.helpmeproductions.willus08.welp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.helpmeproductions.willus08.welp.model.Buisnesses;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;


public class RestCallBoundService extends Service {
    private IBinder binder = new MyBinder();
    private static String BASE_URL = "https://api.yelp.com/";

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private static Retrofit create(){

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public Call<Buisnesses> BuisnessCall(String zip){
        Retrofit retrofit = create();
        Calls services = retrofit.create(Calls.class);
        return services.getBuisnesesZip(zip);
    }

    public Call<Buisnesses> BuisnessCall(double lat, double lng){
        Retrofit retrofit = create();
        Calls services = retrofit.create(Calls.class);
        return services.getBuissnessesLatLong(lat, lng);
    }

    private interface Calls{
        @Headers("Authorization:Bearer zQdHImMsLBo-hU4w3yLO0X9IApr7GmhP4QGfxQdGRO8ky2CxFyIJdHEiuSmVTYUxDgvD9QTQHyyNe4YWLDYjAMxBGSAvozyP6LONHnMSoq6BD9zrH4WlIjHfNzWHWnYx")
        @GET("v3/businesses/search")
        Call<Buisnesses> getBuisnesesZip (@Query("location") String location);

        @Headers("Authorization:Bearer zQdHImMsLBo-hU4w3yLO0X9IApr7GmhP4QGfxQdGRO8ky2CxFyIJdHEiuSmVTYUxDgvD9QTQHyyNe4YWLDYjAMxBGSAvozyP6LONHnMSoq6BD9zrH4WlIjHfNzWHWnYx")
        @GET("v3/businesses/search")
        Call<Buisnesses> getBuissnessesLatLong(@Query("latitude") double lat, @Query("longitude") double lng);
    }
    public class MyBinder extends Binder {
        RestCallBoundService getService(){
            return RestCallBoundService.this;
        }
    }

}


