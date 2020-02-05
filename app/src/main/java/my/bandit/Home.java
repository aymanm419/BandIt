package my.bandit;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import my.bandit.FilesDownloader.DownloadSongTask;
import my.bandit.Model.Post;
import my.bandit.Repository.PostsLoader;
import my.bandit.Service.MusicService;
import my.bandit.ViewAdapter.PostsAdapter;
import my.bandit.ViewModel.PostsViewModel;

public class Home extends Fragment {

    private PostsViewModel mViewModel;
    private ArrayList<Post> posts;
    private PostsAdapter postsAdaper;
    private RecyclerView postsView;
    public static Home home;
    private boolean mSeeking = false;
    private SeekBar seekBar;
    private ImageView stateImage;
    private BroadcastReceiver receiver;
    private boolean mBound = false;
    private MusicService musicService;
    private Timer timer;
    private boolean timerRunning = false;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public static Home newInstance() {
        if (home == null)
            home = new Home();
        return home;
    }

    private void InitPostsView(final View view) {
        posts = new ArrayList<>();
        postsAdaper = new PostsAdapter(getContext(), posts, post -> {
            DownloadSongTask downloadSongTask = new DownloadSongTask(getContext());
            downloadSongTask.execute(post.getSong().getSongFileDir(),
                    view.getContext().getFilesDir() + post.getSong().getSongName());
            Glide.with(view).load(R.drawable.ic_play_arrow_black_24dp).into(stateImage);
            stateImage.setTag("playing");
        });
        mViewModel = new ViewModelProvider(this).get(PostsViewModel.class);
        mViewModel.getPosts().observe(getViewLifecycleOwner(), updatedList -> {
            postsAdaper.setPosts(updatedList);
            postsAdaper.notifyDataSetChanged();
        });
        postsView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        postsView.setAdapter(postsAdaper);
        PostsLoader postsLoader = new PostsLoader(mViewModel);
        postsLoader.execute();
    }

    private void AttachViews(final View view) {
        postsView = view.findViewById(R.id.recyclerView);
        seekBar = view.findViewById(R.id.seekBar);
        stateImage = view.findViewById(R.id.stateImage);
        stateImage.setOnClickListener(v -> {
            if (stateImage.getTag().toString().equals("playing")) {
                Glide.with(view).load(R.drawable.ic_pause_black_24dp).into(stateImage);
                musicService.getMusicHandler().stopPlaying();
                stateImage.setTag("stopped");
            } else {
                Glide.with(view).load(R.drawable.ic_play_arrow_black_24dp).into(stateImage);
                musicService.getMusicHandler().continuePlaying();
                stateImage.setTag("playing");
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && musicService.getMusicHandler().isTimerRunning())
                    musicService.getMusicHandler().seek(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                cancelTimer();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                attachMusicRefresher();
            }
        });

    }

    private void cancelTimer() {
        if (timerRunning) {
            timer.cancel();
            timer.purge();
            timerRunning = false;
        }
    }

    private void attachMusicRefresher() {
        timerRunning = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mBound) {
                    if (musicService.getMusicHandler().isTimerRunning()) {
                        if (seekBar.getMax() != musicService.getMusicHandler().getMax())
                            seekBar.setMax(musicService.getMediaPlayer().getDuration());
                        seekBar.setProgress(musicService.getMediaPlayer().getCurrentPosition());
                    }
                }
            }
        }, 0, 250);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AttachViews(getView());
        InitPostsView(getView());
        Intent intent = new Intent(getActivity().getApplicationContext(), MusicService.class);
        getView().getContext().startService(intent);
        getView().getContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        attachMusicRefresher();
    }

}
