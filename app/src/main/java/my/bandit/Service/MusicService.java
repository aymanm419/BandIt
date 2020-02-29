package my.bandit.Service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.lang.ref.WeakReference;

import lombok.Getter;
import lombok.Setter;
import my.bandit.Api.Api;
import my.bandit.ViewModel.MainViewModel;

public class MusicService extends Service {
    private final IBinder binder = new LocalBinder();
    @Getter
    private MediaPlayer mediaPlayer;
    @Setter
    @Getter
    private boolean isPrepared;
    private WeakReference<MainViewModel> viewModelRef;
    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        Init();
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }

    public void setViewModelRef(MainViewModel viewModelRef) {
        this.viewModelRef = new WeakReference<>(viewModelRef);
    }

    private void Init() {
        mediaPlayer.setOnPreparedListener(mp -> {
            isPrepared = true;
            mp.start();
            MainViewModel mainViewModel = viewModelRef.get();
            if (mainViewModel != null)
                mainViewModel.getSongDuration().postValue(mp.getDuration());
        });
    }

    public void setDataSource(String songName) throws IOException {
        setPrepared(false);
        mediaPlayer.reset();
        mediaPlayer.setDataSource(Api.getSongSource(songName));

    }

    public void preparePlayer() {
        mediaPlayer.prepareAsync();
    }

    public void pausePlaying() {
        if (isPrepared() && mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    public void continuePlaying() {
        if (isPrepared() && !mediaPlayer.isPlaying())
            mediaPlayer.start();
    }

    public void seek(int progress) {
        if (isPrepared())
            mediaPlayer.seekTo(progress);
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
