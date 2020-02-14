package my.bandit.Repository;

import android.os.AsyncTask;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import my.bandit.Api.ApiHandler;
import my.bandit.Api.ResponseHandler;
import my.bandit.Api.UsersApi;
import my.bandit.data.model.LoggedInUser;
import retrofit2.Response;

public class AccountLoader extends AsyncTask<String, String, LoggedInUser> {

    private int getUserID(String userName) throws IOException {
        UsersApi usersApi = ApiHandler.getInstance().getUsersApi();
        Response<JsonObject> jsonResult = usersApi.getUserID(userName).execute();
        if (!ResponseHandler.validateJsonResponse(jsonResult))
            throw new RuntimeException("Error while receiving respond from API regarding user ID, userName " + userName);
        return jsonResult.body().get("data").getAsInt();
    }

    private ArrayList<Integer> getUserLikes(String userName) throws IOException {
        UsersApi usersApi = ApiHandler.getInstance().getUsersApi();
        Response<JsonObject> jsonResult = usersApi.getUserLikes(userName).execute();
        if (!ResponseHandler.validateJsonResponse(jsonResult))
            throw new RuntimeException("Error while receiving respond from API regarding user ID, username " + userName);
        return ApiHandler.getInstance().getGson().fromJson(jsonResult.body().get("data"), new TypeToken<List<Integer>>() {
        }.getType());
    }

    private ArrayList<Integer> getUserDislikes(String userName) throws IOException {
        UsersApi usersApi = ApiHandler.getInstance().getUsersApi();
        Response<JsonObject> jsonResult = usersApi.getUserDislikes(userName).execute();
        if (!ResponseHandler.validateJsonResponse(jsonResult))
            throw new RuntimeException("Error while receiving respond from API regarding user ID, username " + userName);
        return ApiHandler.getInstance().getGson().fromJson(jsonResult.body().get("data"), new TypeToken<List<Integer>>() {
        }.getType());
    }

    private ArrayList<Integer> getUserFavourites(String userName) throws IOException {
        UsersApi usersApi = ApiHandler.getInstance().getUsersApi();
        Response<JsonObject> jsonResult = usersApi.getUserFavourites(userName).execute();
        if (!ResponseHandler.validateJsonResponse(jsonResult))
            throw new RuntimeException("Error while receiving respond from API regarding user ID, username " + userName);
        return ApiHandler.getInstance().getGson().fromJson(jsonResult.body().get("data"), new TypeToken<List<Integer>>() {
        }.getType());
    }

    private LoggedInUser getUserData(String userName) throws IOException {
        LoggedInUser user = new LoggedInUser(getUserID(userName), userName);
        user.setDisliked(getUserDislikes(userName));
        user.setFavourites(getUserFavourites(userName));
        user.setLiked(getUserLikes(userName));
        return user;
    }

    @Override
    protected LoggedInUser doInBackground(String... strings) {
        try {
            return getUserData(strings[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
