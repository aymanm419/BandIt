package my.bandit.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import my.bandit.Model.Post;
import my.bandit.R;
import my.bandit.Service.MusicService;
import my.bandit.ViewAdapter.PostsAdapter;
import my.bandit.ViewModel.FavouriteViewModel;
import my.bandit.ViewModel.MainViewModel;

public class Favourite extends Fragment {
    private MainViewModel mainViewModel;
    private FavouriteViewModel mViewModel;
    private RecyclerView postsView;
    private ArrayList<Post> posts;
    private PostsAdapter postsAdaper;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            mainViewModel.setMusicService(binder.getService());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    public static Favourite newInstance() {
        return new Favourite();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favourite_fragment, container, false);
    }

    public void Init() {
        postsView = getView().findViewById(R.id.favList);
        posts = mViewModel.getPosts().getValue();
        postsAdaper = new PostsAdapter(getContext(), posts, (post, integer) -> {
            mainViewModel.getMusicService().setPlayingPostIndex(integer);
            mainViewModel.getMusicService().setPlayingPostsQueue(mViewModel.getPosts().getValue());
            mainViewModel.getMusicService().startPostSong(post);

        });
        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(postsView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(getView().getContext(), R.drawable.horizontal_divider);
        if (horizontalDivider != null) {
            horizontalDecoration.setDrawable(horizontalDivider);
            postsView.addItemDecoration(horizontalDecoration);
        }
        mViewModel.getPosts().observe(getViewLifecycleOwner(), updatedList -> {
            postsAdaper.setPosts(updatedList);
            postsAdaper.notifyDataSetChanged();
        });
        postsView.setLayoutManager(new LinearLayoutManager(getView().getContext()));
        postsView.setAdapter(postsAdaper);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FavouriteViewModel.class);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        Intent intent = new Intent(getContext(), MusicService.class);
        getContext().startService(intent);
        getContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        mViewModel.fetchPosts();
        Init();
    }

}
