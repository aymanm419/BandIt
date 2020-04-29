package my.bandit.Repository;

import android.content.Context;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import my.bandit.Api.Api;
import my.bandit.Api.ApiResponse;
import my.bandit.Model.Post;
import my.bandit.ViewModel.HomeViewModel;
import my.bandit.ViewModel.MainViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsLoader implements Callback<JsonObject> {
    private HomeViewModel homeViewModel;
    private WeakReference<SwipeRefreshLayout> swipeRefreshLayoutRef;
    private WeakReference<Context> contextWeakReference;
    private MainViewModel mainViewModel;
    @Setter
    private boolean isUpdate = false;

    public PostsLoader(HomeViewModel homeViewModel, SwipeRefreshLayout swipeRefreshLayout, Context context,
                       MainViewModel mainViewModel) {
        this.homeViewModel = homeViewModel;
        this.swipeRefreshLayoutRef = new WeakReference<>(swipeRefreshLayout);
        this.contextWeakReference = new WeakReference<>(context);
        this.mainViewModel = mainViewModel;
    }

    private void refreshLayout() {
        SwipeRefreshLayout swipeRefreshLayout = swipeRefreshLayoutRef.get();
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);

    }

    private void postValue(ArrayList<Post> posts) {
        homeViewModel.getPosts().postValue(posts);
    }

    public void loadPosts(int startPost, int endPost) {
        Call<JsonObject> call = Api.getInstance().getFilesApi().getPosts(startPost, endPost);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
        refreshLayout();
        Context context = contextWeakReference.get();
        if (!ApiResponse.validateJsonResponse(response)) {
            if (context != null)
                Toast.makeText(context, "Failed to connect to server.", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<Post> posts = Api.getInstance().
                getGson().fromJson(response.body().get("data"), new TypeToken<List<Post>>() {
        }.getType());
        if (posts.size() > 0 && isUpdate) {
            mainViewModel.getMusicService().getPlayingPost().setValue(posts.get(0));
        }
        postValue(posts);
    }

    @Override
    public void onFailure(Call<JsonObject> call, Throwable t) {
        t.printStackTrace();
        refreshLayout();
    }
}
