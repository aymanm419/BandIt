package my.bandit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import my.bandit.Model.Post;
import my.bandit.ViewAdapter.PostsAdapter;
import my.bandit.ViewModel.FavouriteViewModel;

public class Favourite extends Fragment {

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
    }

}
