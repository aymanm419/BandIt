package my.bandit;

import android.os.Bundle;
import android.util.Log;
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
import my.bandit.ViewAdapter.PostsAdaper;
import my.bandit.ViewModel.FavouriteViewModel;

public class Favourite extends Fragment {

    private FavouriteViewModel mViewModel;
    private RecyclerView postsView;
    private ArrayList<Post> posts;
    private PostsAdaper postsAdaper;

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
        Log.i("Favourite", "Attaching Model View");
        posts = new ArrayList<>();
        postsView = getView().findViewById(R.id.favList);
        postsAdaper = new PostsAdaper(getActivity().getApplicationContext(), posts);
        postsView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsView.setAdapter(postsAdaper);
        mViewModel.getPosts().observe(getViewLifecycleOwner(), updatedList -> {
            Log.i("Database", "Fetching new favourites");
            postsAdaper.setPosts(updatedList);
            postsAdaper.notifyDataSetChanged();
        });
    }

}
