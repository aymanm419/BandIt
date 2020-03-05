package my.bandit.Api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FilesApi {

    @GET("/post/hot")
    Call<JsonObject> getPosts(@Query("limitstart") Integer postStart, @Query("limitend") Integer postEnd);
}
