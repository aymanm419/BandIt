package my.bandit.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;

import lombok.Getter;
import lombok.Setter;
import my.bandit.ViewModel.HomeViewModel;

public class MusicService extends Service {
    private final IBinder binder = new LocalBinder();
    @Getter
    private MediaPlayer mediaPlayer;
    @Setter
    @Getter
    private boolean isPrepared;
    @Setter
    private HomeViewModel homeViewModel;
    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        Init();
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }

    private void Init() {
        mediaPlayer.setOnPreparedListener(mp -> {
            isPrepared = true;
            mp.start();
            homeViewModel.getSongDuration().postValue(mp.getDuration());
        });
    }

    public void setDataSource(File file) throws IOException {
        setPrepared(false);
        mediaPlayer.reset();
        mediaPlayer.setDataSource(this, Uri.fromFile(file));

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
