package my.bandit.Api;

import android.util.Log;

import com.google.gson.JsonObject;

import retrofit2.Response;

public class ResponseHandler {
    public static boolean validateResponse(Response<?> response) {
        if (response.body() == null) {
            Log.d("HTTP Response", "Received Response with no body!");
            return false;
        } else if (!response.isSuccessful()) {
            Log.d("Api Server", "Did not receive a reply from api.");
            return false;
        }
        return true;
    }

    private static boolean validateJsonBody(Response<JsonObject> response) {
        StandardResponse standardResponse = ApiHandler.getInstance().getGson().fromJson(response.body(), StandardResponse.class);
        if (standardResponse.getStatus().equals(StatusResponse.ERROR)) {
            Log.d("HTTP Response Body", "Received response with error status");
            Log.d("HTTP Response Body", "Message details: " + standardResponse.getMessage());
            return false;
        }
        return true;
    }

    public static boolean validateJsonResponse(Response<JsonObject> response) {
        return validateResponse(response) && validateJsonBody(response);
    }
}
