package my.bandit.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import lombok.Getter;

public class MusicService extends Service {
    public final static int MUSIC_START = 1;
    private final IBinder binder = new LocalBinder();
    @Getter
    private MediaPlayer mediaPlayer;
    @Getter
    private MusicHandler musicHandler;

    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer();
        musicHandler = new MusicHandler(mediaPlayer, this);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        mediaPlayer = null;
        musicHandler = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        int actionCmd = intent.getIntExtra("ACTION_CMD", 0);
        if (actionCmd == MUSIC_START) {
            File currentSong = (File) intent.getSerializableExtra("SONG");
            try {
                musicHandler.setDataSource(currentSong);
                musicHandler.startPlaying();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return START_STICKY;
    }

    public static class MusicHandler {
        private MediaPlayer mediaPlayer;
        private WeakReference<Context> mContext;
        private Timer timer;
        @Getter
        private boolean timerRunning;
        @Getter
        private boolean isPlaying = false;

        MusicHandler(MediaPlayer mediaPlayer, Context mContext) {
            this.mediaPlayer = mediaPlayer;
            this.mContext = new WeakReference<>(mContext);
            this.timerRunning = false;
        }

        private void Init() {
            mediaPlayer.setOnPreparedListener(mp -> {
                isPlaying = true;
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!isPlaying) {
                            if (mediaPlayer.isPlaying())
                                mediaPlayer.pause();
                            return;
                        }
                        if (!mediaPlayer.isPlaying())
                            mediaPlayer.start();
                    }
                }, 0, 250);
                timerRunning = true;
            });
        }

        void setDataSource(File file) throws IOException {
            Context context = mContext.get();
            if (context != null) {
                if (timerRunning) {
                    timer.cancel();
                    timer.purge();
                    timerRunning = false;
                }
                Init();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(context, Uri.fromFile(file));
            }
        }

        public void startPlaying() {
            mediaPlayer.prepareAsync();
        }

        public void stopPlaying() {
            isPlaying = false;
        }

        public void continuePlaying() {
            isPlaying = true;
        }

        public void seek(int progress) {
            if (isTimerRunning())
                mediaPlayer.seekTo(progress);
        }

        public int getMax() {
            if (isTimerRunning())
                return mediaPlayer.getDuration();
            return 100;
        }
    }

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
