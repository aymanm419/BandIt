package my.bandit.Api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UsersDataApi {


    @POST("/user/create")
    @GET("/user/find")
    Call<JsonObject> isUserExists(@Query("username") String userName);

    Call<JsonObject> createAccount(@Query("username") String userName,
            @Query("password") String password);

    @GET("/user/login")
    Call<JsonObject> validateUserCredentials(@Query("username") String userName,
            @Query("password") String password);

    @GET("/user/info/id")
    Call<JsonObject> getUserID(@Query("username") String userName);

    @GET("/user/info/likes")
    Call<JsonObject> getUserLikes(@Query("userID") int userID);

    @GET("/user/info/dislikes")
    Call<JsonObject> getUserDislikes(@Query("userID") int userID);

    @GET("/user/info/favourites")
    Call<JsonObject> getUserFavourites(@Query("userID") int userID);
}
