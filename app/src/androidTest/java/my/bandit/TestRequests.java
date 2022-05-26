package my.bandit;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import my.bandit.Api.Api;
import my.bandit.Api.ApiResponse;
import my.bandit.Api.UsersDataApi;
import my.bandit.Model.Post;
import retrofit2.Response;

public class TestRequests {
    @Test
    public void testGetUserIDValid() throws IOException {
        UsersDataApi usersDataApi = Api.getInstance().getUsersDataApi();
        Response<JsonObject> jsonResult = usersDataApi.getUserID("bsla").execute();
        if (!ApiResponse.validateResponse(jsonResult))
            Assert.fail("Server has sent an invalid JSON Response");
        if (!ApiResponse.validateJsonResponse(jsonResult))
            Assert.fail("Client is unable to understand the server's Response");
        Assert.assertEquals(3, jsonResult.body().get("data").getAsInt());
    }

    @Test
    public void testGetUserIDInvalid() throws IOException {
        UsersDataApi usersDataApi = Api.getInstance().getUsersDataApi();
        Response<JsonObject> jsonResult = usersDataApi.getUserID("invalid_user").execute();
        if (!ApiResponse.validateResponse(jsonResult))
            Assert.fail("Server has sent an invalid JSON Response");
        Assert.assertEquals("Username does not exist.",
                jsonResult.body().get("message").getAsString());
    }

    @Test
    public void testGetUserLikes() throws IOException {
        UsersDataApi usersDataApi = Api.getInstance().getUsersDataApi();
        Response<JsonObject> jsonResult = usersDataApi.getUserLikes(3).execute();
        if (!ApiResponse.validateResponse(jsonResult))
            Assert.fail("Server has sent an invalid JSON Response");
        if (!ApiResponse.validateJsonResponse(jsonResult))
            Assert.fail("Client is unable to understand the server's Response");
        List<Post> posts = Api.getInstance().getGson().fromJson(jsonResult.body().get("data"),
                new TypeToken<ArrayList<Post>>() {
                }.getType());
        Assert.assertEquals(1, posts.size());
    }

    @Test
    public void testGetUserDislikes() throws IOException {
        UsersDataApi usersDataApi = Api.getInstance().getUsersDataApi();
        Response<JsonObject> jsonResult = usersDataApi.getUserDislikes(3).execute();
        if (!ApiResponse.validateResponse(jsonResult))
            Assert.fail("Server has sent an invalid JSON Response");
        if (!ApiResponse.validateJsonResponse(jsonResult))
            Assert.fail("Client is unable to understand the server's Response");
        List<Post> posts = Api.getInstance().getGson().fromJson(jsonResult.body().get("data"),
                new TypeToken<ArrayList<Post>>() {
                }.getType());
        Assert.assertEquals(0, posts.size());
    }

    @Test
    public void testGetUserFavourites() throws IOException {
        UsersDataApi usersDataApi = Api.getInstance().getUsersDataApi();
        Response<JsonObject> jsonResult = usersDataApi.getUserFavourites(4).execute();
        if (!ApiResponse.validateResponse(jsonResult))
            Assert.fail("Server has sent an invalid JSON Response");
        if (!ApiResponse.validateJsonResponse(jsonResult))
            Assert.fail("Client is unable to understand the server's Response");
        System.out.println(jsonResult.body().toString());
        List<Post> posts = Api.getInstance().getGson().fromJson(jsonResult.body().get("data"),
                new TypeToken<ArrayList<Post>>() {
                }.getType());
        Assert.assertEquals(1, posts.size());
    }

}
