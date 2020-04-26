package my.bandit.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import lombok.Getter;
import lombok.Setter;
import my.bandit.Data.LoginDataSource;
import my.bandit.Model.LoggedInUser;
import my.bandit.Model.Post;
import my.bandit.Repository.LoginRepository;
import my.bandit.Repository.UpdateFavourite;
import my.bandit.Repository.UpdateLikes;
import my.bandit.Service.MusicService;

public class NowPlayingViewModel extends ViewModel {
    @Getter
    private MutableLiveData<Boolean> liked = new MutableLiveData<>();
    @Getter
    private MutableLiveData<Boolean> disliked = new MutableLiveData<>();
    @Getter
    private MutableLiveData<Boolean> favourite = new MutableLiveData<>();
    @Getter
    @Setter
    private MusicService musicService;
    LoggedInUser user = LoginRepository.getInstance(new LoginDataSource()).getUser();
    Post post;

    public void moveBar(int pos) {
        musicService.getBarProgress().setValue(pos);
        musicService.seek(pos);
    }

    public void pauseTimer() {
        musicService.pausePlaying();
    }

    public void continueTimer() {
        musicService.continuePlaying();
    }

    private void startSong(String songName) {
        musicService.setDataSource(songName);
        musicService.preparePlayer();
        musicService.getIsPlaying().setValue(true);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void startPostSong(Post post) {
        startSong(post.getSong().getSongFileDir());
        continueTimer();
    }
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
