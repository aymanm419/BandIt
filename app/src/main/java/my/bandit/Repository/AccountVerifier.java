package my.bandit.Repository;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.IOException;

import my.bandit.Api.Api;
import my.bandit.Api.ApiException;
import my.bandit.Api.ApiResponse;
import my.bandit.Api.UsersDataApi;
import retrofit2.Response;

public class AccountVerifier extends AsyncTask<String, String, Boolean> {

    private boolean verifyAccount(String userName, String password) throws IOException, ApiException {
        UsersDataApi usersDataApi = Api.getInstance().getUsersDataApi();
        Response<JsonObject> jsonResult = usersDataApi.validateUserCredentials(userName, password).execute();
        if (!ApiResponse.validateResponse(jsonResult))
            throw new ApiException("Error while receiving respond from API regarding user ID, username " + userName);
        if (!ApiResponse.validateJsonResponse(jsonResult))
            return false;
        Log.i("Account Validation", jsonResult.body().get("data").toString());
        return true;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            return verifyAccount(strings[0], strings[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
