package my.bandit.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import lombok.Getter;
import my.bandit.Model.Post;
import my.bandit.Repository.UpdateFavourite;
import my.bandit.Repository.UpdateLikes;
import my.bandit.data.LoginDataSource;
import my.bandit.data.LoginRepository;
import my.bandit.data.model.LoggedInUser;

public class NowPlayingViewModel extends ViewModel {
    @Getter
    private MutableLiveData<Boolean> liked = new MutableLiveData<>();
    @Getter
    private MutableLiveData<Boolean> disliked = new MutableLiveData<>();
    @Getter
    private MutableLiveData<Boolean> favourite = new MutableLiveData<>();

    LoggedInUser user = LoginRepository.getInstance(new LoginDataSource()).getUser();
    Post post;

    public void fetchNewData(Post post) {
        this.post = post;
        liked.setValue(user.getLiked().contains(post.getPostID()));
        disliked.setValue(user.getDisliked().contains(post.getPostID()));
        favourite.setValue(user.getFavourites().contains(post.getPostID()));
    }

    public void like() {
        if (liked.getValue()) {
            liked.setValue(false);
            new UpdateLikes().execute(user.getUserId(), post.getPostID(), -1, 0);
            user.getLiked().remove((Integer)post.getPostID());
        } else {
            liked.setValue(true);
            new UpdateLikes().execute(user.getUserId(), post.getPostID(), 1, 0);
            user.getLiked().add(post.getPostID());
        }
    }

    public void dislike() {
        if (disliked.getValue()) {
            disliked.setValue(false);
            new UpdateLikes().execute(user.getUserId(), post.getPostID(), -1, 1);
            user.getDisliked().remove((Integer)post.getPostID());
        } else {
            disliked.setValue(true);
            new UpdateLikes().execute(user.getUserId(), post.getPostID(), 1, 1);
            user.getDisliked().add(post.getPostID());
        }
    }

    public void favourite() {
        if (favourite.getValue()) {
            favourite.setValue(false);
            new UpdateFavourite().execute(user.getUserId(), post.getPostID(), -1);
            user.getFavourites().remove((Integer)post.getPostID());
        } else {
            favourite.setValue(true);
            new UpdateFavourite().execute(user.getUserId(), post.getPostID(), 1);
            user.getFavourites().add(post.getPostID());
        }
    }
}
