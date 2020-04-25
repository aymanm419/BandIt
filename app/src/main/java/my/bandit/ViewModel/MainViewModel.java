package my.bandit.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import lombok.Getter;
import lombok.Setter;
import my.bandit.Model.Post;
import my.bandit.Service.MusicService;

public class MainViewModel extends AndroidViewModel {

    @Setter
    @Getter
    private MusicService musicService;


    public MainViewModel(@NonNull Application application) {
        super(application);
    }

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


}
