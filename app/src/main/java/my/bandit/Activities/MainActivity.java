package my.bandit.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import my.bandit.Data.LoginDataSource;
import my.bandit.Login.LoginActivity;
import my.bandit.R;
import my.bandit.Repository.LoginRepository;
import my.bandit.Service.MusicService;
import my.bandit.ViewModel.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            mainViewModel.getMusicServiceLive().setValue(binder.getService());
            binder.getService().setViewModelRef(mainViewModel);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mainViewModel.getMusicServiceLive().setValue(null);
        }
    };

    private MainViewModel mainViewModel;
    private SeekBar seekBar;
    private ImageView stateImage;
    private ImageView currentSongImage;
    private TextView songName, bandName;

    private void InitObservers() {
        mainViewModel.getSongDuration().observe(this, integer -> {
            seekBar.setMax(integer);
        });
        mainViewModel.getCurrentlyPlayedPost().observe(this, post -> {
            mainViewModel.loadPostImage(currentSongImage, post);
            mainViewModel.onPostClick(post, currentSongImage);
            songName.setText(post.getSong().getSongName());
            bandName.setText(post.getSong().getBandName());
        });
        mainViewModel.getBarProgress().observe(this, integer -> {
            seekBar.setProgress(integer);
        });
        mainViewModel.getPlayingState().observe(this, aBoolean -> {
            if (aBoolean) {
                Glide.with(getApplicationContext()).load(R.drawable.ic_play_arrow_white_24dp).into(stateImage);
            } else {
                Glide.with(getApplicationContext()).load(R.drawable.ic_pause_white_24dp).into(stateImage);
            }
        });

    }

    private void AttachViews() {
        seekBar = findViewById(R.id.seekBar);
        stateImage = findViewById(R.id.stateImage);
        currentSongImage = findViewById(R.id.currentPlayingImage);
        songName = findViewById(R.id.currentPlayingSong);
        bandName = findViewById(R.id.currentPlayingBand);
    }

    private void attachViewListeners() {
        currentSongImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NowPlaying.class);
                startActivity(intent);
            }
        });
        stateImage.setOnClickListener(v -> {
            boolean currentValue = !mainViewModel.getPlayingState().getValue();
            mainViewModel.getPlayingState().setValue(currentValue);
            if (currentValue)
                mainViewModel.continueTimer();
            else
                mainViewModel.pauseTimer();
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mainViewModel.moveBar(progress);
                    mainViewModel.continueTimer();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mainViewModel.pauseTimer();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.NavView);
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        getApplicationContext().startService(intent);
        getApplicationContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        AttachViews();
        InitObservers();
        attachViewListeners();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    private void logout() {
        SharedPreferences preferences = getSharedPreferences("Login data", Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
        LoginRepository.getInstance(new LoginDataSource()).logout();
        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(login);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOut:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
