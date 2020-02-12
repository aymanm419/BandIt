package my.bandit.Api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UsersApi {
    @GET("/users/finduser")
    Call<JsonObject> isUserExists(@Query("username") String userName);

    @GET("/users/createaccount")
    Call<JsonObject> createAccount(@Query("username") String userName, @Query("password") String password);

    @GET("/users/login")
    Call<JsonObject> validateUserCredentials(@Query("username") String userName, @Query("password") String password);
}
