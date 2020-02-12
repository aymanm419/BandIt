package my.bandit.Repository;

import android.content.Context;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import my.bandit.Api.ApiHandler;
import my.bandit.Api.ResponseHandler;
import my.bandit.Model.Post;
import my.bandit.ViewModel.HomeViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsLoader implements Callback<JsonObject> {
    private HomeViewModel postsViewModel;
    private WeakReference<SwipeRefreshLayout> swipeRefreshLayoutRef;
    private WeakReference<Context> contextWeakReference;

    public PostsLoader(HomeViewModel postsViewModel, SwipeRefreshLayout swipeRefreshLayout, Context context) {
        this.postsViewModel = postsViewModel;
        this.swipeRefreshLayoutRef = new WeakReference<>(swipeRefreshLayout);
        this.contextWeakReference = new WeakReference<>(context);
    }

    private void refreshLayout() {
        SwipeRefreshLayout swipeRefreshLayout = swipeRefreshLayoutRef.get();
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);

    }

    private void postValue(ArrayList<Post> posts) {
        postsViewModel.getPosts().postValue(posts);
    }

    public void loadPosts(int startPost, int endPost) {
        Call<JsonObject> call = ApiHandler.getInstance().getDataApi().getPosts(startPost, endPost);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
        refreshLayout();
        Context context = contextWeakReference.get();
        if (!ResponseHandler.validateJsonResponse(response)) {
            if (context != null)
                Toast.makeText(context, "Failed to connect to server.", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<Post> posts = ApiHandler.getInstance().
                getGson().fromJson(response.body().get("data"), new TypeToken<List<Post>>() {
        }.getType());
        postValue(posts);
    }

    @Override
    public void onFailure(Call<JsonObject> call, Throwable t) {
        t.printStackTrace();
        refreshLayout();
    }
}
