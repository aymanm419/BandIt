package my.bandit.ViewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import my.bandit.Model.Post;
import my.bandit.data.LoginDataSource;
import my.bandit.data.LoginRepository;
import my.bandit.data.model.LoggedInUser;

public class FavouriteViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Post>> posts = new MutableLiveData<>();
    private LoggedInUser user = LoginRepository.getInstance(new LoginDataSource()).getUser();

    public void fetchPosts() {
        Log.i("Database", "Fetching favourite posts");
        posts.setValue(new ArrayList<>());
    }

    public MutableLiveData<ArrayList<Post>> getPosts() {
        return posts;
    }
}
