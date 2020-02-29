package my.bandit.ViewModel;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;
import my.bandit.Api.Api;
import my.bandit.Model.Post;
import my.bandit.Service.MusicService;

public class MainViewModel extends AndroidViewModel {
    final private ExecutorService pool = Executors.newCachedThreadPool();
    @Getter
    private MutableLiveData<Post> currentlyPlayedPost;
    @Getter
    private MutableLiveData<Integer> currentlyPlayedPostIndex, songDuration, barProgress;
    @Getter
    private MutableLiveData<Boolean> playingState;
    @Getter
    private MutableLiveData<ArrayList<Post>> posts;
    @Getter
    private MutableLiveData<MusicService> musicServiceLive;
    @Getter
    private MutableLiveData<Boolean> timerRunning;
    private Handler handler;
    private WeakReference<Context> contextWeakReference;

    public MainViewModel(@NonNull Application application) {
        super(application);
        timerRunning = new MutableLiveData<>();
        handler = new Handler();
        currentlyPlayedPost = new MutableLiveData<>();
        currentlyPlayedPostIndex = new MutableLiveData<>();
        playingState = new MutableLiveData<>();
        posts = new MutableLiveData<>();
        musicServiceLive = new MutableLiveData<>();
        songDuration = new MutableLiveData<>();
        barProgress = new MutableLiveData<>();
        contextWeakReference = new WeakReference<>(application.getBaseContext());
        playingState.setValue(true);
        timerRunning.setValue(false);
        barProgress.setValue(0);
        updateSeekBar();
    }

    public void moveBar(int pos) {
        barProgress.setValue(pos);
        MusicService musicService = musicServiceLive.getValue();
        if (musicService != null)
            musicService.seek(pos);
    }

    public void pauseTimer() {
        timerRunning.postValue(false);
        playingState.postValue(false);
        MusicService musicService = musicServiceLive.getValue();
        if (musicService != null)
            musicService.pausePlaying();
    }

    public void continueTimer() {
        timerRunning.postValue(true);
        playingState.postValue(true);
        MusicService musicService = musicServiceLive.getValue();
        if (musicService != null)
            musicService.continuePlaying();
    }

    public void loadPostImage(final ImageView imageView, Post currentPost) {
        Context mContext = contextWeakReference.get();
        if (mContext != null) {
            Glide.with(mContext).load(Api.getImageSource(currentPost.getPictureDir())).into(imageView);
        }
    }

    private void startSong(String songName) throws IOException {
        MusicService musicService = musicServiceLive.getValue();
        if (musicService != null) {
            musicService.setDataSource(songName);
            musicService.preparePlayer();
            getPlayingState().postValue(true);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        pool.shutdown();
    }

    public void startPostSong(Post post) {
        try {
            startSong(post.getSong().getSongFileDir());
            continueTimer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playPrevious() {
        if (getCurrentlyPlayedPostIndex().getValue() != null
                && getPosts().getValue() != null) {
            int currentPost = getCurrentlyPlayedPostIndex().getValue();
            if (currentPost > 0)
                currentPost--;
            getCurrentlyPlayedPost().setValue(getPosts().getValue().get(currentPost));
        }
    }

    public void playNext() {
        if (getCurrentlyPlayedPostIndex().getValue() != null
                && getPosts().getValue() != null) {
            int currentPost = getCurrentlyPlayedPostIndex().getValue();
            List<Post> list = getPosts().getValue();
            if (currentPost + 1 < list.size())
                currentPost++;
            getCurrentlyPlayedPost().setValue(list.get(currentPost));
        }
    }

    private void updateSeekBar() {
        MusicService musicService = musicServiceLive.getValue();
        if (musicService != null && timerRunning.getValue() != null && timerRunning.getValue()) {
            if (musicService.isPrepared()) {
                barProgress.postValue(musicService.getMediaPlayer().getCurrentPosition());
            }
        }
        handler.postDelayed(this::updateSeekBar, 250);
    }

    public void onPostClick(Post post, ImageView imageView) {
        loadPostImage(imageView, post);
        startPostSong(post);
    }
}
