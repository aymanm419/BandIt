package my.bandit.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import lombok.Getter;
import my.bandit.Model.Post;

public class HomeViewModel extends AndroidViewModel {
    @Getter
    private MutableLiveData<Post> currentlyPlayedPost;
    @Getter
    private MutableLiveData<Integer> currentlyPlayedPostIndex;
    @Getter
    private MutableLiveData<Boolean> playingState;
    @Getter
    private MutableLiveData<ArrayList<Post>> posts;
    @Getter
    private MutableLiveData<Integer> songDuration;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        currentlyPlayedPost = new MutableLiveData<>();
        currentlyPlayedPostIndex = new MutableLiveData<>();
        playingState = new MutableLiveData<>();
        posts = new MutableLiveData<>();
        songDuration = new MutableLiveData<>();
        playingState.setValue(true);
    }
}
