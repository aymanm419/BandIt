package my.bandit.Api;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface FilesApi {

    @GET("/post/hot")
    Call<JsonObject> getPosts(@Query("limitstart") Integer postStart, @Query("limitend") Integer postEnd);

    @Streaming
    @GET("/post/file")
    Call<ResponseBody> getFile(@Query("dir") String remotePath);
}
