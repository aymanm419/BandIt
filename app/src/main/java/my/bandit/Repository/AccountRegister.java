package my.bandit.Repository;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.IOException;

import my.bandit.Api.Api;
import my.bandit.Api.ApiException;
import my.bandit.Api.ApiResponse;
import my.bandit.Api.UsersDataApi;
import my.bandit.data.Result;
import my.bandit.data.model.LoggedInUser;
import retrofit2.Response;

public class AccountRegister extends AsyncTask<String, String, Result> {

    private void createUser(String userName, String password) throws IOException, ApiException {
        UsersDataApi usersDataApi = Api.getInstance().getUsersDataApi();
        Response<JsonObject> jsonResult = usersDataApi.createAccount(userName, password).execute();
        if (!ApiResponse.validateJsonResponse(jsonResult))
            throw new ApiException("Error while receiving respond from API regarding user ID, username " + userName);
        Log.i("Account Creation", jsonResult.body().get("data").toString());
    }

    private boolean checkIfUserExists(String userName) throws IOException, ApiException {
        UsersDataApi usersDataApi = Api.getInstance().getUsersDataApi();
        Response<JsonObject> jsonResult = usersDataApi.isUserExists(userName).execute();
        if (!ApiResponse.validateResponse(jsonResult))
            throw new ApiException("Error while receiving respond from API regarding user ID, username " + userName);
        if (!ApiResponse.validateJsonResponse(jsonResult))
            return false;
        Log.i("Account Check", jsonResult.body().get("data").toString());
        return true;
    }

    @Override
    protected Result doInBackground(String... strings) {
        try {
            if (!checkIfUserExists(strings[0])) {
                Log.i("Register", "Attempting to create user");
                createUser(strings[0], strings[1]);
                return new Result.Success<>(new LoggedInUser(0, ""));
            } else {
                Log.i("Register", "Existing user");
                return new Result.Error(new Exception("User already exists"));
            }
        } catch (IOException | ApiException e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error registering", e));
        }

    }
}
