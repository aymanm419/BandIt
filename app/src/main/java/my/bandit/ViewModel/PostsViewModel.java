package my.bandit.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import lombok.Getter;
import my.bandit.Model.Post;

public class PostsViewModel extends AndroidViewModel {
    @Getter
    private MutableLiveData<ArrayList<Post>> posts;

    public PostsViewModel(@NonNull Application application) {
        super(application);
        posts = new MutableLiveData<>();
    }
}
