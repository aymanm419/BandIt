package my.bandit.Api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Getter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiHandler {
    private static ApiHandler apiHandler;
    @Getter
    private FilesApi filesApi;
    @Getter
    private UsersApi usersApi;
    @Getter
    private Gson gson;

    private ApiHandler() {
        gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:6969/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        filesApi = retrofit.create(FilesApi.class);
        usersApi = retrofit.create(UsersApi.class);
    }

    public static ApiHandler getInstance() {
        if (apiHandler == null)
            apiHandler = new ApiHandler();
        return apiHandler;
    }
}
