package my.bandit.Repository;

import android.os.AsyncTask;

import com.google.gson.JsonObject;

import java.io.IOException;

import my.bandit.Api.Api;
import my.bandit.Api.ApiException;
import my.bandit.Api.ApiResponse;
import my.bandit.Api.UsersActionApi;
import retrofit2.Response;

public class UpdateLikes extends AsyncTask<Integer, Integer, Void> {

    private Void updateLikes(int postID, int userID, int change, int type) throws IOException, ApiException {
        UsersActionApi usersActionApi = Api.getInstance().getUsersActionApi();
        if (type == 0) {
            Response<JsonObject> jsonResult = usersActionApi.changeUserLike(userID, postID, change).execute();
            if (!ApiResponse.validateResponse(jsonResult))
                throw new ApiException("Error while receiving respond from API regarding user ID, userID " + userID);
            if (!ApiResponse.validateJsonResponse(jsonResult))
                throw new ApiException();
        } else {
            Response<JsonObject> jsonResult = usersActionApi.changeUserDislike(userID, postID, change).execute();
            if (!ApiResponse.validateResponse(jsonResult))
                throw new ApiException("Error while receiving respond from API regarding user ID, userID " + userID);
            if (!ApiResponse.validateJsonResponse(jsonResult))
                throw new ApiException();
        }
        return null;
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        try {
            return updateLikes(integers[1], integers[0], integers[2], integers[3]);
        } catch (IOException | ApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}
