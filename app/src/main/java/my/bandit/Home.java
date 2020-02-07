package my.bandit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    final private ExecutorService pool = Executors.newCachedThreadPool();
    public static Home home;
    private SeekBar seekBar;
    private ImageView stateImage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MusicService musicService;
    private Handler handler;
    private boolean mBound;
    private boolean timerRunning;
    private Runnable updateSeekBar;
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

    private void startSong(final View view, final File song) {
        Intent intent = new Intent(view.getContext(), MusicService.class);
        intent.putExtra("ACTION_CMD", MusicService.MUSIC_START);
        intent.putExtra("SONG", song);
        view.getContext().startService(intent);
    }

    private void InitPostsView(final View view) {
        posts = new ArrayList<>();
        postsAdaper = new PostsAdapter(getContext(), posts, post -> {
            PostsCache.getInstance().setLastPlayed(post);
            Glide.with(view).load(R.drawable.ic_play_arrow_black_24dp).into(stateImage);
            stateImage.setTag("playing");
            Runnable runnable = () -> {
                DownloadSongTask downloadSongTask = new DownloadSongTask();
                try {
                    File file = downloadSongTask.downloadFile(post.getSong().getSongFileDir(),
                            view.getContext().getFilesDir() + post.getSong().getSongName());
                    PostsCache postsCache = PostsCache.getInstance();
                    postsCache.cacheSong(file.getAbsolutePath(), file);
                    startSong(view, file);
                    continueTimer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            pool.execute(runnable);
        });
        mViewModel = new ViewModelProvider(this).get(PostsViewModel.class);
        mViewModel.getPosts().observe(getViewLifecycleOwner(), updatedList -> {
            postsAdaper.setPosts(updatedList);
            postsAdaper.notifyDataSetChanged();
        });
        postsView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        postsView.setAdapter(postsAdaper);
    }

    private void AttachViews(final View view) {
        postsView = view.findViewById(R.id.recyclerView);
        seekBar = view.findViewById(R.id.seekBar);
        stateImage = view.findViewById(R.id.stateImage);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
    }

    private void attachViewListeners(final View view) {
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

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseTimer();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                continueTimer();
                if (musicService.getMusicHandler().isTimerRunning())
                    musicService.getMusicHandler().seek(seekBar.getProgress());
            }
        });
        swipeRefreshLayout.setOnRefreshListener(() -> {
            PostsLoader postsLoader = new PostsLoader(mViewModel, swipeRefreshLayout);
            postsLoader.execute();
        });
    }

    private void pauseTimer() {
        timerRunning = false;
    }

    private void continueTimer() {
        timerRunning = true;
    }

    private void updateSeekBar() {
        if (mBound && timerRunning) {
            if (musicService.getMusicHandler().isTimerRunning()) {
                if (seekBar.getMax() != musicService.getMusicHandler().getMax())
                    seekBar.setMax(musicService.getMediaPlayer().getDuration());
                seekBar.setProgress(musicService.getMediaPlayer().getCurrentPosition());
            }
        }
        handler.postDelayed(updateSeekBar, 250);
    }

    private void initVariables() {
        continueTimer();
        updateSeekBar = this::updateSeekBar;
        handler = new Handler();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pool.shutdown();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initVariables();
        AttachViews(getView());
        InitPostsView(getView());
        attachViewListeners(getView());
        updateSeekBar();
        Intent intent = new Intent(getActivity().getApplicationContext(), MusicService.class);
        getView().getContext().startService(intent);
        getView().getContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);


    }

}
