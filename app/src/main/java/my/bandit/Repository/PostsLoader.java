package my.bandit.Repository;

import android.os.AsyncTask;
import android.util.Log;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import my.bandit.Database.DatabaseConnection;
import my.bandit.Model.Post;
import my.bandit.Model.Song;
import my.bandit.ViewModel.PostsViewModel;

public class PostsLoader extends AsyncTask<Void, Void, ArrayList<Post>> {
    private PostsViewModel postsViewModel;
    private WeakReference<SwipeRefreshLayout> swipeRefreshLayoutRef;

    public PostsLoader(PostsViewModel postsViewModel, SwipeRefreshLayout swipeRefreshLayout) {
        this.postsViewModel = postsViewModel;
        this.swipeRefreshLayoutRef = new WeakReference<>(swipeRefreshLayout);
    }

    public ArrayList<Post> LoadPosts() throws ExecutionException, InterruptedException, SQLException {
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
        Connection connection = databaseConnection.getConnection();
        if (connection == null) {
            Log.d("Database Connection", "Can not connect to db.");
            return new ArrayList<>();
        }
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
    }

    @Override
    protected ArrayList<Post> doInBackground(Void... voids) {
        try {
            return LoadPosts();
        } catch (ExecutionException | InterruptedException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Post> posts) {
        super.onPostExecute(posts);
        postsViewModel.getPosts().postValue(posts);
        SwipeRefreshLayout swipeRefreshLayout = swipeRefreshLayoutRef.get();
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }
}
