package my.bandit.Api;

import android.util.Log;

import com.google.gson.JsonObject;

public class ApiResponse {
    public static boolean validateResponse(retrofit2.Response<?> response) {
        if (response.body() == null) {
            Log.d("HTTP ApiResponse", "Received ApiResponse with no body!");
            return false;
        } else if (!response.isSuccessful()) {
            Log.d("Api Server", "Did not receive a reply from api.");
            return false;
        }
        return true;
    }

    private static boolean validateJsonBody(retrofit2.Response<JsonObject> response) {
        StandardResponse standardResponse = Api.getInstance().getGson().fromJson(response.body(), StandardResponse.class);
        if (standardResponse.getStatus().equals(StatusResponse.ERROR)) {
            Log.d("HTTP ApiResponse Body", "Received response with error status");
            Log.d("HTTP ApiResponse Body", "Message details: " + standardResponse.getMessage());
            return false;
        }
        return true;
    }

    public static boolean validateJsonResponse(retrofit2.Response<JsonObject> response) {
        return validateResponse(response) && validateJsonBody(response);
    }
}
