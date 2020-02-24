package my.bandit.Repository;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;

import my.bandit.Api.Api;
import my.bandit.Api.ApiException;
import my.bandit.Api.ApiResponse;
import my.bandit.Api.UsersDataApi;
import my.bandit.Data.model.LoggedInUser;
import my.bandit.Model.Post;
import retrofit2.Response;

public class AccountLoader extends AsyncTask<String, String, LoggedInUser> {

    private int getUserID(String userName) throws IOException, ApiException {
        UsersDataApi usersDataApi = Api.getInstance().getUsersDataApi();
        Response<JsonObject> jsonResult = usersDataApi.getUserID(userName).execute();
        if (!ApiResponse.validateResponse(jsonResult))
            throw new ApiException("Error while receiving respond from API regarding user ID, userName " + userName);
        if (!ApiResponse.validateJsonResponse(jsonResult))
            throw new ApiException();
        Log.i("User ID", jsonResult.body().toString());
        return jsonResult.body().get("data").getAsInt();
    }

    private ArrayList<Post> getUserLikes(int userID) throws IOException, ApiException {
        UsersDataApi usersDataApi = Api.getInstance().getUsersDataApi();
        Response<JsonObject> jsonResult = usersDataApi.getUserLikes(userID).execute();
        if (!ApiResponse.validateResponse(jsonResult))
            throw new ApiException("Error while receiving respond from API regarding user ID, username " + userID);
        if (!ApiResponse.validateJsonResponse(jsonResult))
            throw new ApiException();

        return Api.getInstance().getGson().fromJson(jsonResult.body().get("data"), new TypeToken<ArrayList<Post>>() {
        }.getType());
    }

    private ArrayList<Post> getUserDislikes(int userID) throws IOException, ApiException {
        UsersDataApi usersDataApi = Api.getInstance().getUsersDataApi();
        Response<JsonObject> jsonResult = usersDataApi.getUserDislikes(userID).execute();
        if (!ApiResponse.validateResponse(jsonResult))
            throw new ApiException("Error while receiving respond from API regarding user, username " + userID);
        if (!ApiResponse.validateJsonResponse(jsonResult))
            throw new ApiException();
        return Api.getInstance().getGson().fromJson(jsonResult.body().get("data"), new TypeToken<ArrayList<Post>>() {
        }.getType());
    }

    private ArrayList<Post> getUserFavourites(int userID) throws IOException, ApiException {
        UsersDataApi usersDataApi = Api.getInstance().getUsersDataApi();
        Response<JsonObject> jsonResult = usersDataApi.getUserFavourites(userID).execute();
        if (!ApiResponse.validateResponse(jsonResult))
            throw new ApiException("Error while receiving respond from API regarding user ID, username " + userID);
        if (!ApiResponse.validateJsonResponse(jsonResult))
            throw new ApiException();
        return Api.getInstance().getGson().fromJson(jsonResult.body().get("data"), new TypeToken<ArrayList<Post>>() {
        }.getType());
    }

    private LoggedInUser getUserData(String userName) throws IOException, ApiException {
        LoggedInUser user = new LoggedInUser(getUserID(userName), userName);
        user.setLiked(getUserLikes(user.getUserId()));
        user.setDisliked(getUserDislikes(user.getUserId()));
        user.setFavourites(getUserFavourites(user.getUserId()));
        return user;
    }

    @Override
    protected LoggedInUser doInBackground(String... strings) {
        try {
            return getUserData(strings[0]);
        } catch (IOException | ApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}
