package my.bandit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import my.bandit.Model.Post;
import my.bandit.Repository.PostsLoader;
import my.bandit.ViewAdapter.PostsAdaper;
import my.bandit.ViewModel.PostsViewModel;

public class Home extends Fragment {

    private PostsViewModel mViewModel;
    private ArrayList<Post> posts;
    private PostsAdaper postsAdaper;
    private RecyclerView postsView;
    private Button refreshButton;
    public static Home newInstance() {
        return new Home();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        postsView = view.findViewById(R.id.recyclerView);
        refreshButton = view.findViewById(R.id.refreshButton);
        posts = new ArrayList<>();
        postsAdaper = new PostsAdaper(getContext(), posts);
        postsView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mViewModel = new ViewModelProvider(this).get(PostsViewModel.class);
        postsView.setAdapter(postsAdaper);
        mViewModel.getPosts().observe(getViewLifecycleOwner(), updatedList -> {
            postsAdaper.setPosts(updatedList);
            postsAdaper.notifyDataSetChanged();
        });
        refreshButton.setOnClickListener(v -> {
            PostsLoader postsLoader = new PostsLoader(mViewModel);
            postsLoader.execute();
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}
