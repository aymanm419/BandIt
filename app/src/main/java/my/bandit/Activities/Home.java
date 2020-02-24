package my.bandit.Activities;

import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;

import my.bandit.Model.Post;
import my.bandit.R;
import my.bandit.Repository.PostsLoader;
import my.bandit.ViewAdapter.PostsAdapter;
import my.bandit.ViewModel.HomeViewModel;
import my.bandit.ViewModel.MainViewModel;

public class Home extends Fragment {
    private HomeViewModel homeViewModel;
    private MainViewModel mainViewModel;
    private ArrayList<Post> posts;
    private PostsAdapter postsAdapter;
    private RecyclerView postsView;
    private SeekBar seekBar;
    private ImageView stateImage, previousImage, nextImage;
    private ImageView currentSongImage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView songName, bandName;

    public static Home newInstance() {
        return new Home();
    }

    private void InitObservers(final View view) {
        homeViewModel.getPosts().observe(getViewLifecycleOwner(), updatedList -> {
            postsAdapter.setPosts(updatedList);
            postsAdapter.notifyDataSetChanged();
        });
        mainViewModel.getSongDuration().observe(getViewLifecycleOwner(), integer -> {
            seekBar.setMax(integer);
        });
        mainViewModel.getCurrentlyPlayedPost().observe(getViewLifecycleOwner(), post -> {
            Log.i("Home", "Played again");
            mainViewModel.downloadPostImage(currentSongImage, post);
            songName.setText(post.getSong().getSongName());
            bandName.setText(post.getSong().getBandName());
        });
        mainViewModel.getPlayingState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                Glide.with(view.getContext()).load(R.drawable.ic_play_arrow_black_24dp).into(stateImage);
                mainViewModel.continueTimer();
            } else {
                Glide.with(view.getContext()).load(R.drawable.ic_pause_black_24dp).into(stateImage);
                mainViewModel.pauseTimer();
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
            boolean currentValue = mainViewModel.getPlayingState().getValue();
            mainViewModel.getPlayingState().setValue(!currentValue);
        });
        previousImage.setOnClickListener(v -> {
            mainViewModel.playPrevious();
        });
        nextImage.setOnClickListener(v -> {
            mainViewModel.playNext();
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
        mainViewModel.getBarProgress().observe(getViewLifecycleOwner(), integer -> {
            seekBar.setProgress(integer);
        });
        swipeRefreshLayout.setOnRefreshListener(() -> {
            PostsLoader postsLoader = new PostsLoader(homeViewModel, swipeRefreshLayout, view.getContext());
            postsLoader.loadPosts(1, 10);
        });
    }


    private void initVariables() {
        posts = new ArrayList<>();
        homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        postsAdapter = new PostsAdapter(getContext(), posts, (post, position) -> {
            mainViewModel.getCurrentlyPlayedPost().setValue(post);
            mainViewModel.getCurrentlyPlayedPostIndex().setValue(position);
            mainViewModel.getPosts().setValue(homeViewModel.getPosts().getValue());
            mainViewModel.onPostClick(post, currentSongImage);
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

    }

}
