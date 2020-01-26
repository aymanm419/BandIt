package my.bandit;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import my.bandit.ViewAdapter.PostsAdaper;
import my.bandit.ViewModel.PostsViewModel;

public class MainActivity extends AppCompatActivity {
    RecyclerView PostsView;
    ArrayList<Song> Songs;
    PostsAdaper postsAdaper;
    private PostsViewModel model;

    public void addSong(View view) {
        Song song = new Song("Name", "Album", null);
        ArrayList<Song> Temp = new ArrayList<>();
        Temp.add(song);
        model.getSongs().setValue(Temp);
        System.out.println("Hey");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Songs = new ArrayList<>();
        postsAdaper = new PostsAdaper(Songs);
        PostsView = findViewById(R.id.RecyclerView);
        PostsView.setLayoutManager(new LinearLayoutManager(this));
        model = ViewModelProviders.of(this).get(PostsViewModel.class);
        PostsView.setAdapter(postsAdaper);
        model.getSongs().observe(this, new Observer<ArrayList<Song>>() {
            @Override
            public void onChanged(ArrayList<Song> updatedList) {
                postsAdaper.setSongs(updatedList);
                postsAdaper.notifyDataSetChanged();

            }
        });
    }
}
