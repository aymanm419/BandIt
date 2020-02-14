package my.bandit.Api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UsersApi {
    @GET("/users/find")
    Call<JsonObject> isUserExists(@Query("username") String userName);

    @GET("/users/create")
    Call<JsonObject> createAccount(@Query("username") String userName, @Query("password") String password);

    @GET("/users/login")
    Call<JsonObject> validateUserCredentials(@Query("username") String userName, @Query("password") String password);

    @GET("/users/{username}/info/id")
    Call<JsonObject> getUserID(@Path("username") String userName);

    @GET("/users/{username}/info/likes")
    Call<JsonObject> getUserLikes(@Path("username") String userName);

    @GET("/users/{username}/info/dislikes")
    Call<JsonObject> getUserDislikes(@Path("username") String userName);

    @GET("/users/{username}/info/favourites")
    Call<JsonObject> getUserFavourites(@Path("username") String userName);
}
