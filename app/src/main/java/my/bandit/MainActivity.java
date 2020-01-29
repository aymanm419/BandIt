package my.bandit;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import my.bandit.Model.Post;
import my.bandit.Repository.PostsLoader;
import my.bandit.ViewAdapter.PostsAdaper;
import my.bandit.ViewModel.PostsViewModel;

public class MainActivity extends AppCompatActivity {
    RecyclerView postsView;
    ArrayList<Post> posts;
    PostsAdaper postsAdaper;
    private PostsViewModel model;

    public void addSong(View view) throws InterruptedException, ExecutionException {
        PostsLoader postsLoader = new PostsLoader();
        model.getPosts().postValue(postsLoader.execute().get());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*posts = new ArrayList<>();
        postsAdaper = new PostsAdaper(getApplicationContext(), posts);
        postsView.setLayoutManager(new LinearLayoutManager(this));
        model = ViewModelProviders.of(this).get(PostsViewModel.class);
        postsView.setAdapter(postsAdaper);
        model.getPosts().observe(this, updatedList -> {
            postsAdaper.setPosts(updatedList);
            postsAdaper.notifyDataSetChanged();
        });*/
    }
}
