package my.bandit.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import lombok.Getter;
import my.bandit.Song;

public class PostsViewModel extends AndroidViewModel {
    @Getter
    private MutableLiveData<ArrayList<Song>> Songs;


    public PostsViewModel(@NonNull Application application) {
        super(application);
        Songs = new MutableLiveData<>();
    }
}
