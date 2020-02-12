package my.bandit.Api;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface DataApi {

    @GET("/posts/hot/{limitstart}/{limitend}")
    Call<JsonObject> getPosts(@Path("limitstart") Integer postStart, @Path("limitend") Integer postEnd);

    @Streaming
    @GET("/posts/file/{dir}")
    Call<ResponseBody> getImage(@Path("dir") String remotePath);
}
