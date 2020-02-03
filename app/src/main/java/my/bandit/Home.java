package my.bandit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

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
    private ProgressBar progressBar;
    private ImageView stateImage;
    private BroadcastReceiver receiver;

    public static Home newInstance() {
        if (home == null)
            home = new Home();
        return home;
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
        Log.i("Service", "Unregistering music broadcast serivce");
        super.onStop();
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

    private void InitBroadcastListener(final View view) {
        IntentFilter filter = new IntentFilter("MEDIA_PLAYER_PROGRESS");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int progress = intent.getIntExtra("progress", 100);
                int maxBar = intent.getIntExtra("maxProgress", 100);
                if (progressBar.getMax() != maxBar)
                    progressBar.setMax(maxBar);
                progressBar.setProgress(progress);
            }
        };
        Log.i("Service", "registering music broadcast serivce");
        LocalBroadcastManager.getInstance(view.getContext()).registerReceiver(receiver, filter);
    }

    private void AttachViews(View view) {
        postsView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        stateImage = view.findViewById(R.id.stateImage);
        stateImage.setOnClickListener(v -> {
            if (stateImage.getTag().toString().equals("playing")) {
                Glide.with(view).load(R.drawable.ic_pause_black_24dp).into(stateImage);
                Intent intent = new Intent(getContext(), MusicService.class);
                intent.putExtra("ACTION_CMD", MusicService.MUSIC_STOP);
                getContext().startService(intent);
                stateImage.setTag("stopped");
            } else {
                Glide.with(view).load(R.drawable.ic_play_arrow_black_24dp).into(stateImage);
                Intent intent = new Intent(getContext(), MusicService.class);
                intent.putExtra("ACTION_CMD", MusicService.MUSIC_CONTINUE);
                getContext().startService(intent);
                stateImage.setTag("playing");
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        AttachViews(view);
        InitPostsView(view);
        InitBroadcastListener(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}
