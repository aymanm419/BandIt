package my.bandit.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.transition.Slide;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import my.bandit.Api.Api;
import my.bandit.R;
import my.bandit.Service.MusicService;
import my.bandit.ViewModel.NowPlayingViewModel;

public class NowPlaying extends AppCompatActivity {
    private ImageView currentSongImage, stateImage;
    private TextView songName, bandName;
    private SeekBar seekBar;
    private NowPlayingViewModel mViewModel;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            mViewModel.setMusicService(binder.getService());
            initObservers();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };

    private void initViews() {
        currentSongImage = findViewById(R.id.songImage);
        songName = findViewById(R.id.songName);
        bandName = findViewById(R.id.bandName);
        seekBar = findViewById(R.id.songProgressBar);
        stateImage = findViewById(R.id.stateImage);
    }

    private void initObservers() {
        mViewModel.getMusicService().getCurrentlyPlayedPost().observe(this, post -> {
            Glide.with(getApplicationContext())
                    .load(Api.getImageSource(post.getPictureDir()))
                    .into(currentSongImage);
            songName.setText(post.getSong().getSongName());
            bandName.setText(post.getSong().getBandName());
        });
        mViewModel.getMusicService().getSongDuration().observe(this, integer -> {
            seekBar.setMax(integer);
        });
        mViewModel.getMusicService().getBarProgress().observe(this, integer -> seekBar.setProgress(integer));
        mViewModel.getMusicService().getIsPlaying().observe(this, aBoolean -> {
            if (aBoolean) {
                Glide.with(getApplicationContext()).load(R.drawable.ic_play_arrow_white_24dp).into(stateImage);
            } else {
                Glide.with(getApplicationContext()).load(R.drawable.ic_pause_white_24dp).into(stateImage);
            }
        });
        stateImage.setOnClickListener(v -> {
            boolean currentValue = !mViewModel.getMusicService().getIsPlaying().getValue();
            mViewModel.getMusicService().getIsPlaying().setValue(currentValue);
            if (currentValue)
                mViewModel.continueTimer();
            else
                mViewModel.pauseTimer();
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mViewModel.moveBar(progress);
                    mViewModel.continueTimer();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mViewModel.pauseTimer();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAnimation();
        setContentView(R.layout.activity_now_playing);
        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        getApplicationContext().startService(intent);
        getApplicationContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        mViewModel = new ViewModelProvider(this).get(NowPlayingViewModel.class);
        initViews();
    }

    public void setAnimation() {
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.TOP);
        slide.setDuration(400);
        slide.setInterpolator(new AccelerateDecelerateInterpolator());
        getWindow().setExitTransition(slide);
        getWindow().setEnterTransition(slide);
    }
}
