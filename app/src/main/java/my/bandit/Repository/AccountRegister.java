package my.bandit.Repository;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.IOException;

import my.bandit.Api.ApiHandler;
import my.bandit.Api.ResponseHandler;
import my.bandit.Api.UsersApi;
import my.bandit.data.Result;
import my.bandit.data.model.LoggedInUser;
import retrofit2.Response;

public class AccountRegister extends AsyncTask<String, String, Result> {

    private void createUser(String username, String password) throws IOException {
        UsersApi usersApi = ApiHandler.getInstance().getUsersApi();
        Response<JsonObject> jsonResult = usersApi.createAccount(username, password).execute();
        if (!ResponseHandler.validateJsonResponse(jsonResult))
            return;
        Log.i("Account Creation", jsonResult.body().get("message").toString());
    }

    private boolean checkIfUserExists(String username) throws IOException {
        UsersApi usersApi = ApiHandler.getInstance().getUsersApi();
        Response<JsonObject> jsonResult = usersApi.isUserExists(username).execute();
        if (!ResponseHandler.validateJsonResponse(jsonResult))
            return false;
        Log.i("Account Check", jsonResult.body().get("message").toString());
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
        } catch (IOException e) {
            return new Result.Error(new IOException("Error registering", e));
        }
    }
}
