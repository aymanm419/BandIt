package my.bandit.Api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Getter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {
    private static Api api;
    private static final String apiSource = "http://127.0.0.1:6969/";
    @Getter
    private final FilesApi filesApi;
    @Getter
    private final UsersDataApi usersDataApi;
    @Getter
    private final UsersActionApi usersActionApi;
    @Getter
    private final Gson gson;
    private Api() {
        gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiSource)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        filesApi = retrofit.create(FilesApi.class);
        usersDataApi = retrofit.create(UsersDataApi.class);
        usersActionApi = retrofit.create(UsersActionApi.class);
    }

    public static Api getInstance() {
        if (api == null)
            api = new Api();
        return api;
    }

    public static String getImageSource(String imageName) {
        return Api.apiSource + "post/img?dir=" + imageName;
    }

    public static String getSongSource(String songName) {
        return Api.apiSource + "post/audio?dir=" + songName;
    }
}
