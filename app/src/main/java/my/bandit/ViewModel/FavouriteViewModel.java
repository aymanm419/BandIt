package my.bandit.ViewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import my.bandit.Model.Post;

public class FavouriteViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Post>> posts = new MutableLiveData<>();

    public MutableLiveData<ArrayList<Post>> fetchPosts() {
        Log.i("Database", "Fetching favourite posts");
        //TODO fetch user info from database
        return null;
    }

    public MutableLiveData<ArrayList<Post>> getPosts() {
        return posts;
    }

    public void addFav(Post post) {
        Log.i ("Favourite", "Adding new favourite");
        posts.getValue().add(post);
    }

    public void removeFav(Post post) {
        Log.i ("Favourite", "Removing favourite");
        posts.getValue().remove(post);
    }
}
