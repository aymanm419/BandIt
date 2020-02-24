package my.bandit.Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import my.bandit.Model.Post;
import my.bandit.R;
import my.bandit.ViewAdapter.PostsAdapter;
import my.bandit.ViewModel.FavouriteViewModel;
import my.bandit.ViewModel.MainViewModel;

public class Favourite extends Fragment {
    private MainViewModel mainViewModel;
    private FavouriteViewModel mViewModel;
    private RecyclerView postsView;
    private ArrayList<Post> posts;
    private PostsAdapter postsAdaper;

    public static Favourite newInstance() {
        return new Favourite();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favourite_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FavouriteViewModel.class);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        mViewModel.fetchPosts();
        postsView = getView().findViewById(R.id.favList);
        posts = mViewModel.getPosts().getValue();
        postsAdaper = new PostsAdapter(getContext(), posts, (post, integer) -> {
            mainViewModel.getCurrentlyPlayedPost().setValue(post);
            mainViewModel.getCurrentlyPlayedPostIndex().setValue(integer);
            mainViewModel.getPosts().setValue(mViewModel.getPosts().getValue());
            mainViewModel.downloadPostSong(post);

        });
        mViewModel.getPosts().observe(getViewLifecycleOwner(), updatedList -> {
            postsAdaper.setPosts(updatedList);
            postsAdaper.notifyDataSetChanged();
        });
        postsView.setLayoutManager(new LinearLayoutManager(getView().getContext()));
        postsView.setAdapter(postsAdaper);
    }

}
