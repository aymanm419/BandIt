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
import android.widget.TextView;

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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import my.bandit.FilesDownloader.DownloadImageTask;
import my.bandit.FilesDownloader.DownloadSongTask;
import my.bandit.Model.Post;
import my.bandit.Repository.PostsLoader;
import my.bandit.Service.MusicService;
import my.bandit.ViewAdapter.PostsAdapter;
import my.bandit.ViewModel.HomeViewModel;

public class Home extends Fragment {

    private HomeViewModel homeViewModel;

    private ArrayList<Post> posts;
    private PostsAdapter postsAdapter;

    private RecyclerView postsView;
    private SeekBar seekBar;
    private ImageView stateImage, previousImage, nextImage;
    private ImageView currentSongImage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView songName, bandName;

    private MusicService musicService;

    final private ExecutorService pool = Executors.newCachedThreadPool();

    public static Home home;

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
            musicService.setHomeViewModel(homeViewModel);
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

    private void startSong(final View view, final File song) throws IOException {
        musicService.setDataSource(song);
        musicService.preparePlayer();
        homeViewModel.getPlayingState().postValue(true);
    }

    private void InitObservers(final View view) {
        homeViewModel.getPosts().observe(getViewLifecycleOwner(), updatedList -> {
            postsAdapter.setPosts(updatedList);
            postsAdapter.notifyDataSetChanged();
        });
        homeViewModel.getSongDuration().observe(getViewLifecycleOwner(), integer -> {
            seekBar.setMax(integer);
        });
        homeViewModel.getCurrentlyPlayedPost().observe(getViewLifecycleOwner(), post -> {
            PostsCache.getInstance().setLastPlayed(post);
            new DownloadImageTask(view.getContext(), currentSongImage).execute(post.getPictureDir(),
                    view.getContext().getFilesDir() + post.getSong().getBandName());
            songName.setText(post.getSong().getSongName());
            bandName.setText(post.getSong().getBandName());
            Runnable runnable = () -> {
                DownloadSongTask downloadSongTask = new DownloadSongTask();
                try {
                    File file = downloadSongTask.downloadFile(post.getSong().getSongFileDir(),
                            view.getContext().getFilesDir() + post.getSong().getSongName());
                    PostsCache postsCache = PostsCache.getInstance();
                    postsCache.cacheSong(post.getSong().getSongFileDir(), file);
                    startSong(view, file);
                    continueTimer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            pool.execute(runnable);
        });
        homeViewModel.getPlayingState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                Glide.with(view.getContext()).load(R.drawable.ic_play_arrow_black_24dp).into(stateImage);
                if (mBound)
                    musicService.continuePlaying();
            } else {
                Glide.with(view.getContext()).load(R.drawable.ic_pause_black_24dp).into(stateImage);
                if (mBound)
                    musicService.pausePlaying();
            }
        });

    }

    private void AttachViews(final View view) {
        postsView = view.findViewById(R.id.recyclerView);
        seekBar = view.findViewById(R.id.seekBar);
        stateImage = view.findViewById(R.id.stateImage);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        currentSongImage = view.findViewById(R.id.currentPlayingImage);
        songName = view.findViewById(R.id.currentPlayingSong);
        bandName = view.findViewById(R.id.currentPlayingBand);
        previousImage = view.findViewById(R.id.previousImage);
        nextImage = view.findViewById(R.id.nextImage);
    }

    private void attachViewListeners(final View view) {
        stateImage.setOnClickListener(v -> {
            boolean currentValue = homeViewModel.getPlayingState().getValue();
            homeViewModel.getPlayingState().setValue(!currentValue);
        });
        previousImage.setOnClickListener(v -> {
            if (homeViewModel.getCurrentlyPlayedPostIndex().getValue() != null
                    && homeViewModel.getPosts().getValue() != null) {
                int currentPost = homeViewModel.getCurrentlyPlayedPostIndex().getValue();
                if (currentPost > 0)
                    currentPost--;
                homeViewModel.getCurrentlyPlayedPost().setValue(homeViewModel.getPosts().getValue().get(currentPost));
            }
        });
        nextImage.setOnClickListener(v -> {
            if (homeViewModel.getCurrentlyPlayedPostIndex().getValue() != null
                    && homeViewModel.getPosts().getValue() != null) {
                int currentPost = homeViewModel.getCurrentlyPlayedPostIndex().getValue();
                List<Post> list = homeViewModel.getPosts().getValue();
                if (currentPost + 1 < list.size())
                    currentPost++;
                homeViewModel.getCurrentlyPlayedPost().setValue(list.get(currentPost));
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && !musicService.getMediaPlayer().isPlaying())
                    musicService.continuePlaying();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseTimer();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                continueTimer();
                if (musicService.isPrepared())
                    musicService.seek(seekBar.getProgress());
            }
        });
        swipeRefreshLayout.setOnRefreshListener(() -> {
            PostsLoader postsLoader = new PostsLoader(homeViewModel, swipeRefreshLayout, view.getContext());
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
            if (musicService.isPrepared()) {
                seekBar.setProgress(musicService.getMediaPlayer().getCurrentPosition());
            }
        }
        handler.postDelayed(updateSeekBar, 250);
    }

    private void initVariables() {
        continueTimer();
        updateSeekBar = this::updateSeekBar;
        handler = new Handler();
        posts = new ArrayList<>();
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        postsAdapter = new PostsAdapter(getContext(), posts, (post, position) -> {
            homeViewModel.getCurrentlyPlayedPost().setValue(post);
            homeViewModel.getCurrentlyPlayedPostIndex().setValue(position);
        });
        postsView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsView.setAdapter(postsAdapter);
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
        AttachViews(getView());
        initVariables();

        InitObservers(getView());
        attachViewListeners(getView());
        updateSeekBar();
        Intent intent = new Intent(getActivity().getApplicationContext(), MusicService.class);
        getView().getContext().startService(intent);
        getView().getContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);

    }

}
