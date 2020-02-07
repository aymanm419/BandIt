package my.bandit.Repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import my.bandit.Database.DatabaseConnection;
import my.bandit.Model.Post;
import my.bandit.Model.Song;
import my.bandit.ViewModel.HomeViewModel;

public class PostsLoader extends AsyncTask<Void, Void, ArrayList<Post>> {
    private HomeViewModel postsViewModel;
    private WeakReference<SwipeRefreshLayout> swipeRefreshLayoutRef;
    private WeakReference<Context> contextWeakReference;

    public PostsLoader(HomeViewModel postsViewModel, SwipeRefreshLayout swipeRefreshLayout, Context context) {
        this.postsViewModel = postsViewModel;
        this.swipeRefreshLayoutRef = new WeakReference<>(swipeRefreshLayout);
        this.contextWeakReference = new WeakReference<>(context);
    }

    private ArrayList<Post> LoadPosts() {
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
        Connection connection = null;
        try {
            connection = databaseConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from posts");
            ArrayList<Post> postsLoaded = new ArrayList<>();
            while (resultSet.next()) {
                Song song = new Song(resultSet.getString("post_song_name"),
                        resultSet.getString("post_album_name"),
                        resultSet.getString("post_song_dir"));
                Post post = new Post(song, resultSet.getString("post_picture_dir"));
                postsLoaded.add(post);
            }
            resultSet.close();
            databaseConnection.releaseConnection(connection);
            return postsLoaded;
        } catch (ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected ArrayList<Post> doInBackground(Void... voids) {
        return LoadPosts();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.i("Cancel", "Cancel");
    }

    @Override
    protected void onPostExecute(ArrayList<Post> posts) {
        super.onPostExecute(posts);
        SwipeRefreshLayout swipeRefreshLayout = swipeRefreshLayoutRef.get();
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        if (posts == null) {
            Context context = contextWeakReference.get();
            if (context != null)
                Toast.makeText(context, "Failed to connect to server.", Toast.LENGTH_SHORT).show();
            return;
        }
        postsViewModel.getPosts().postValue(posts);
    }
}
