package my.bandit.Activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Objects;

import my.bandit.R;
import my.bandit.Repository.PostsLoader;
import my.bandit.ViewAdapter.PostsAdapter;
import my.bandit.ViewModel.HomeViewModel;
import my.bandit.ViewModel.MainViewModel;

public class Home extends Fragment {
    private HomeViewModel homeViewModel;
    private MainViewModel mainViewModel;
    private PostsAdapter postsAdapter;
    private RecyclerView postsView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PostsLoader postsLoader;

    private void InitObservers() {
        homeViewModel.getPosts().observe(getViewLifecycleOwner(), updatedList -> {
            postsAdapter.setPosts(updatedList);
            postsAdapter.notifyDataSetChanged();
        });
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadPosts(1, 10);
        });
    }

    private void AttachViews(final View view) {
        postsView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
    }

    private void loadPosts(int startPost, int endPost) {
        postsLoader.loadPosts(startPost, endPost);
    }

    private void initVariables() {
        homeViewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(HomeViewModel.class);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        postsLoader = new PostsLoader(homeViewModel, swipeRefreshLayout, getContext(), mainViewModel);
        postsAdapter = new PostsAdapter(getContext(), new ArrayList<>(), (post, position) ->
                mainViewModel.getCurrentlyPlayedPost().setValue(post));
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
        AttachViews(Objects.requireNonNull(getView()));
        initVariables();
        InitObservers();
        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(postsView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(getView().getContext(), R.drawable.horizontal_divider);
        if (horizontalDivider != null) {
            horizontalDecoration.setDrawable(horizontalDivider);
            postsView.addItemDecoration(horizontalDecoration);
        }
        loadPosts(1, 10);
    }

}
