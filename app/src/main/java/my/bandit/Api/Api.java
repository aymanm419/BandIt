package my.bandit.Api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Getter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {
    private static Api api;
    @Getter
    private FilesApi filesApi;
    @Getter
    private UsersDataApi usersDataApi;
    @Getter
    private UsersActionApi usersActionApi;
    @Getter
    private Gson gson;
    private static final String apiSource = "http://10.0.2.2:6969/";
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
        return Api.apiSource + "post/file?dir=" + imageName;
    }

    public static String getSongSource(String songName) {
        return Api.apiSource + "post/file?dir=" + songName;
    }
}
