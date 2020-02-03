package my.bandit.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    public final static int MUSIC_CONTINUE = 2;
    public final static int MUSIC_START = 1;
    public final static int MUSIC_STOP = 0;
    private MediaPlayer mediaPlayer;
    private MusicPlayer musicPlayer;

    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer();
        musicPlayer = new MusicPlayer(mediaPlayer, this);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        int actionCmd = intent.getIntExtra("ACTION_CMD", 0);
        if (actionCmd == MUSIC_START) {
            File currentSong = (File) intent.getSerializableExtra("SONG");
            try {
                musicPlayer.setDataSource(currentSong);
                musicPlayer.startPlaying();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (actionCmd == MUSIC_CONTINUE) {
            musicPlayer.continuePlaying();
        } else if (actionCmd == MUSIC_STOP) {
            musicPlayer.stopPlaying();
        }
        return START_STICKY;
    }

    private static class MusicPlayer {
        private MediaPlayer mediaPlayer;
        private WeakReference<Context> mContext;
        private Timer timer;
        private boolean timerRunning;
        private boolean isPlaying = false;

        MusicPlayer(MediaPlayer mediaPlayer, Context mContext) {
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
                        Context context = mContext.get();
                        if (context != null) {
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("MEDIA_PLAYER_PROGRESS");
                            broadcastIntent.putExtra("progress", mediaPlayer.getCurrentPosition());
                            broadcastIntent.putExtra("maxProgress", mediaPlayer.getDuration());
                            LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
                        }
                    }
                }, 0, 100);
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
    }
}
