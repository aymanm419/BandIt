package my.bandit.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import my.bandit.Data.LoginDataSource;
import my.bandit.Model.LoggedInUser;
import my.bandit.Model.Post;
import my.bandit.Repository.LoginRepository;

public class FavouriteViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Post>> posts = new MutableLiveData<>();
    private LoggedInUser user = LoginRepository.getInstance(new LoginDataSource()).getUser();

    public void fetchPosts() {
        posts.setValue(user.getFavourites());

    }

    public MutableLiveData<ArrayList<Post>> getPosts() {
        return posts;
    }
}
