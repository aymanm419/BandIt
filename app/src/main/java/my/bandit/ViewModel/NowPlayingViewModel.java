package my.bandit.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import lombok.Getter;
import my.bandit.Data.LoginDataSource;
import my.bandit.Model.LoggedInUser;
import my.bandit.Model.Post;
import my.bandit.Repository.LoginRepository;
import my.bandit.Repository.UpdateFavourite;
import my.bandit.Repository.UpdateLikes;

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
        if (post != null) {
            this.post = post;
            liked.setValue(user.getLiked().contains(post));
            disliked.setValue(user.getDisliked().contains(post));
            favourite.setValue(user.getFavourites().contains(post));
        }
    }

    public void like() {
        if (liked.getValue()) {
            liked.setValue(false);
            new UpdateLikes().execute(user.getUserId(), post.getPostID(), -1, 0);
            user.getLiked().remove(post);
        } else {
            liked.setValue(true);
            new UpdateLikes().execute(user.getUserId(), post.getPostID(), 1, 0);
            user.getLiked().add(post);
        }
    }

    public void dislike() {
        if (disliked.getValue()) {
            disliked.setValue(false);
            new UpdateLikes().execute(user.getUserId(), post.getPostID(), -1, 1);
            user.getDisliked().remove(post);
        } else {
            disliked.setValue(true);
            new UpdateLikes().execute(user.getUserId(), post.getPostID(), 1, 1);
            user.getDisliked().add(post);
        }
    }

    public void favourite() {
        if (favourite.getValue()) {
            favourite.setValue(false);
            new UpdateFavourite().execute(user.getUserId(), post.getPostID(), -1);
            user.getFavourites().remove(post);
        } else {
            favourite.setValue(true);
            new UpdateFavourite().execute(user.getUserId(), post.getPostID(), 1);
            user.getFavourites().add(post);
        }
    }
}
