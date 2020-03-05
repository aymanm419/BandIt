package my.bandit.Service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.devbrackets.android.exomedia.AudioPlayer;

import java.lang.ref.WeakReference;

import lombok.Getter;
import lombok.Setter;
import my.bandit.Api.Api;
import my.bandit.ViewModel.MainViewModel;

public class MusicService extends Service {
    private final IBinder binder = new LocalBinder();
    @Getter
    private AudioPlayer audioPlayer;
    @Setter
    @Getter
    private boolean isPrepared;
    private WeakReference<MainViewModel> viewModelRef;
    @Override
    public void onCreate() {
        super.onCreate();
        audioPlayer = new AudioPlayer(this);
        audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        Init();
    }

    @Override
    public void onDestroy() {
        audioPlayer.release();
        audioPlayer = null;
        super.onDestroy();
    }

    public void setViewModelRef(MainViewModel viewModelRef) {
        this.viewModelRef = new WeakReference<>(viewModelRef);
    }

    private void Init() {
        audioPlayer.setOnPreparedListener(() -> {
            isPrepared = true;
            audioPlayer.start();
            MainViewModel mainViewModel = viewModelRef.get();
            if (mainViewModel != null)
                mainViewModel.getSongDuration().postValue((int) audioPlayer.getDuration());
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
        if (isPrepared() && audioPlayer.isPlaying())
            audioPlayer.pause();
    }

    public void continuePlaying() {
        if (isPrepared() && !audioPlayer.isPlaying())
            audioPlayer.start();
    }

    public void seek(int progress) {
        if (isPrepared())
            audioPlayer.seekTo(progress);
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