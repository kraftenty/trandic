package org.kraftenty.api.unsplash;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UnsplashService {
    @GET("search/photos")
    Call<UnsplashResponse> searchPhotos(
        @Query("client_id") String clientId,
        @Query("query") String query,
        @Query("per_page") int perPage
    );
}
