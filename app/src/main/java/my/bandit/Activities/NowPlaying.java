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
    private ImageView currentSongImage, stateImage, heartImage, upVoteImage, downVoteImage,
            nextImage, previousImage;
    private TextView songName, bandName;
    private SeekBar seekBar;
    private NowPlayingViewModel mViewModel;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            mViewModel.setMusicService(binder.getService());
            mViewModel.fetchNewData(binder.getService().getPlayingPost().getValue());
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
        heartImage = findViewById(R.id.heartImageView);
        upVoteImage = findViewById(R.id.upVoteImageView);
        downVoteImage = findViewById(R.id.downVoteImageView);
        nextImage = findViewById(R.id.nextImageView);
        previousImage = findViewById(R.id.previousImageView);
    }

    private void initObservers() {
        mViewModel.getLiked().observe(this, aBoolean -> {
            if (aBoolean)
                Glide.with(getApplicationContext()).load(R.drawable.android_like_image).into(upVoteImage);
            else
                Glide.with(getApplicationContext()).load(R.drawable.ic_thumb_up_black_24dp).into(upVoteImage);
        });
        mViewModel.getDisliked().observe(this, aBoolean -> {
            if (aBoolean)
                Glide.with(getApplicationContext()).load(R.drawable.android_unlike_image).into(downVoteImage);
            else
                Glide.with(getApplicationContext()).load(R.drawable.ic_thumb_down_black_24dp).into(downVoteImage);
        });
        mViewModel.getFavourite().observe(this, aBoolean -> {
            if (aBoolean)
                Glide.with(getApplicationContext()).load(R.drawable.ic_favorite_red_24dp).into(heartImage);
            else
                Glide.with(getApplicationContext()).load(R.drawable.ic_favorite_black_24dp).into(heartImage);
        });
        mViewModel.getMusicService().getPlayingPost().observe(this, post -> {
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
    }

    private void initListeners() {
        nextImage.setOnClickListener(v -> mViewModel.getMusicService().playNext());
        previousImage.setOnClickListener(v -> mViewModel.getMusicService().playPrevious());
        heartImage.setOnClickListener(v -> mViewModel.favourite());
        upVoteImage.setOnClickListener(v -> mViewModel.like());
        downVoteImage.setOnClickListener(v -> mViewModel.dislike());
        stateImage.setOnClickListener(v -> {
            boolean currentValue = !mViewModel.getMusicService().getIsPlaying().getValue();
            mViewModel.getMusicService().getIsPlaying().setValue(currentValue);
            if (currentValue)
                mViewModel.getMusicService().continueTimer();
            else
                mViewModel.getMusicService().pauseTimer();
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mViewModel.getMusicService().moveBar(progress);
                    mViewModel.getMusicService().continueTimer();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mViewModel.getMusicService().pauseTimer();
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
        initListeners();
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
