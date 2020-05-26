package com.naorfarag.exercise1naorjonathan.search;

import com.kc.unsplash.models.Photo;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface PhotosEnhancedService {
    @POST("photos/{id}/like")
    Call<Photo> likePhoto(@Path("id") String id);
}
