package my.bandit.Repository;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.IOException;

import my.bandit.Api.ApiHandler;
import my.bandit.Api.ResponseHandler;
import my.bandit.Api.UsersApi;
import retrofit2.Response;

public class AccountVerifier extends AsyncTask<String, String, Boolean> {

    private boolean verifyAccount(String username, String password) throws IOException {
        UsersApi usersApi = ApiHandler.getInstance().getUsersApi();
        Response<JsonObject> jsonResult = usersApi.validateUserCredentials(username, password).execute();
        if (!ResponseHandler.validateJsonResponse(jsonResult))
            return false;
        Log.i("Account Validation", jsonResult.body().get("message").toString());
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
