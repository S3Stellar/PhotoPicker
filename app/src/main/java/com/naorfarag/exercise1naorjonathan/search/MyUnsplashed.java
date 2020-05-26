package com.naorfarag.exercise1naorjonathan.search;

import android.util.Log;

import com.kc.unsplash.Unsplash;
import com.kc.unsplash.api.HeaderInterceptor;
import com.kc.unsplash.api.endpoints.CollectionsEndpointInterface;
import com.kc.unsplash.api.endpoints.PhotosEndpointInterface;
import com.kc.unsplash.api.endpoints.StatsEndpointInterface;
import com.kc.unsplash.models.Photo;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyUnsplashed extends Unsplash {

    private static final String BASE_URL = "https://api.unsplash.com/";

    public static final String ORIENTATION_PORTRAIT = "portrait";
    public static final String ORIENTATION_LANDSCAPE = "landscape";
    public static final String ORIENTATION_SQUARISH = "squarish";

    private PhotosEnhancedService photosApiService;

    public MyUnsplashed(String clientId) {
        super(clientId);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor(clientId)).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        photosApiService = retrofit.create(PhotosEnhancedService.class);
    }

    public void nisim(){
        photosApiService.likePhoto("K4mSJ7kc0As").enqueue(new Callback<Photo>() {
            @Override
            public void onResponse(Call<Photo> call, Response<Photo> response) {
                if(!response.isSuccessful()){
                    Log.i("ERR", "ERROR");
                    return;
                }
                Log.i("SUCCESS","PHOTO" + response.body());
            }

            @Override
            public void onFailure(Call<Photo> call, Throwable t) {

            }
        });
    }
}
