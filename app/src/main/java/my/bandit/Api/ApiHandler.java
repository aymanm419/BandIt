package my.bandit.Api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Getter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiHandler {
    private static ApiHandler apiHandler;
    @Getter
    private DataApi dataApi;
    @Getter
    private Retrofit retrofit;
    @Getter
    private Gson gson;

    private ApiHandler() {
        gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:6969/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        dataApi = retrofit.create(DataApi.class);
    }

    public static ApiHandler getInstance() {
        if (apiHandler == null)
            apiHandler = new ApiHandler();
        return apiHandler;
    }
}
