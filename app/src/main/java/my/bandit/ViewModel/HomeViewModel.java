package my.bandit.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import lombok.Getter;
import my.bandit.Model.Post;

public class HomeViewModel extends ViewModel {
    @Getter
    private MutableLiveData<ArrayList<Post>> posts;

    public HomeViewModel() {
        posts = new MutableLiveData<>();
    }
}
