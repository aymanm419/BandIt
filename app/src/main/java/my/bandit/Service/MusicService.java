package my.bandit.Service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.devbrackets.android.exomedia.AudioPlayer;

import lombok.Getter;
import lombok.Setter;
import my.bandit.Api.Api;
import my.bandit.Model.Post;

public class MusicService extends Service {
    private final IBinder binder = new LocalBinder();
    @Getter
    private AudioPlayer audioPlayer;
    @Setter
    @Getter
    private boolean isPrepared;
    @Setter
    @Getter
    private MutableLiveData<Boolean> isPlaying;
    @Getter
    private MutableLiveData<Post> currentlyPlayedPost;
    @Getter
    private MutableLiveData<Integer> songDuration, barProgress;
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        audioPlayer = new AudioPlayer(this);
        audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        Init();
        handler = new Handler();
        isPlaying = new MutableLiveData<>();
        currentlyPlayedPost = new MutableLiveData<>();
        songDuration = new MutableLiveData<>();
        barProgress = new MutableLiveData<>();
        updateSeekBar();
    }

    @Override
    public void onDestroy() {
        audioPlayer.release();
        audioPlayer = null;
        super.onDestroy();
    }

    private void Init() {
        audioPlayer.setOnPreparedListener(() -> {
            isPrepared = true;
            audioPlayer.start();
            isPlaying.setValue(true);
            getSongDuration().setValue((int) audioPlayer.getDuration());
        });
    }

    public void setDataSource(String songName) {
        setPrepared(false);
        audioPlayer.reset();
        audioPlayer.setDataSource(Uri.parse(Api.getSongSource(songName)));
    }

    public void preparePlayer() {
        audioPlayer.prepareAsync();
    }

    public void pausePlaying() {
        if (isPrepared() && audioPlayer.isPlaying()) {
            audioPlayer.pause();
            isPlaying.setValue(false);
        }
    }

    public void continuePlaying() {
        if (isPrepared() && !audioPlayer.isPlaying()) {
            audioPlayer.start();
            isPlaying.setValue(true);
        }
    }

    public void seek(int progress) {
        if (isPrepared())
            audioPlayer.seekTo(progress);
    }

    private void updateSeekBar() {
        if (isPrepared() && getIsPlaying().getValue()) {
            barProgress.setValue((int) getAudioPlayer().getCurrentPosition());
        }
        handler.postDelayed(this::updateSeekBar, 250);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}