package my.bandit.Api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UsersActionApi {
    @POST("/user/action/like")
    Call<JsonObject> changeUserLike(@Query("userID") int userID, @Query("postID") int postID, @Query("change") int change);

    @POST("/user/action/dislike")
    Call<JsonObject> changeUserDislike(@Query("userID") int userID, @Query("postID") int postID, @Query("change") int change);

    @POST("/user/action/favourite")
    Call<JsonObject> changeUserFavourite(@Query("userID") int userID, @Query("postID") int postID, @Query("change") int change);
}
