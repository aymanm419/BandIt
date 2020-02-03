package my.bandit.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Process;

import androidx.annotation.Nullable;

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
        int actionCmd = intent.getIntExtra("ACTION_CMD", 0);
        if (actionCmd == MUSIC_START) {
            File currentSong = (File) intent.getSerializableExtra("SONG");
            try {
                musicPlayer.setDataSource(currentSong);
                musicPlayer.startPlaying();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private static class MusicPlayer {
        private MediaPlayer mediaPlayer;
        private WeakReference<Context> mContext;
        private Timer timer;
        private boolean isPlaying = false;

        MusicPlayer(MediaPlayer mediaPlayer, Context mContext) {
            this.mediaPlayer = mediaPlayer;
            this.mContext = new WeakReference<>(mContext);
        }

        public void setDataSource(File file) throws IOException {
            Context context = mContext.get();
            if (context != null) {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(context, Uri.fromFile(file));
            }
        }

        public void startPlaying() {
            if (timer != null)
                timer.purge();
            mediaPlayer.setOnPreparedListener(mp -> {
                timer = new Timer();
                isPlaying = true;
                Runnable runnable = () -> {
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (!isPlaying) {
                                stopPlaying();
                                if (mediaPlayer.isPlaying())
                                    mediaPlayer.stop();
                                return;
                            }
                            if (!mediaPlayer.isPlaying())
                                mediaPlayer.start();
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("MEDIA_PLAYER_PROGRESS");
                            broadcastIntent.putExtra("progress", mediaPlayer.getCurrentPosition());
                            broadcastIntent.putExtra("maxProgress", mediaPlayer.getDuration());
                        }
                    }, 0, 200);
                };
                Thread thread = new Thread(runnable);
                thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
                thread.start();
            });
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
