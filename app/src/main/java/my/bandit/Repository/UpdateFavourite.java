package my.bandit.Repository;

import android.os.AsyncTask;

import com.google.gson.JsonObject;

import java.io.IOException;

import my.bandit.Api.Api;
import my.bandit.Api.ApiException;
import my.bandit.Api.ApiResponse;
import my.bandit.Api.UsersActionApi;
import retrofit2.Response;

public class UpdateFavourite extends AsyncTask<Integer, Integer, Void> {

    private void favouritePost(int userID, int postID, int change) throws IOException, ApiException {
        UsersActionApi usersActionApi = Api.getInstance().getUsersActionApi();
        Response<JsonObject> jsonResult = usersActionApi.changeUserFavourite(userID, postID, change).execute();
        if (!ApiResponse.validateResponse(jsonResult))
            throw new ApiException("Error while receiving respond from API regarding user ID, userID " + userID);
        if (!ApiResponse.validateJsonResponse(jsonResult))
            throw new ApiException();
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        try {
            favouritePost(integers[0], integers[1], integers[2]);
        } catch (IOException | ApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}
