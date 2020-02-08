package my.bandit.Repository;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import my.bandit.Database.DatabaseConnection;
import my.bandit.Model.Post;
import my.bandit.Model.Song;
import my.bandit.ViewModel.FavouriteViewModel;

// الإسم دا عقابا للهبد ف ال "single responsibility" بتاع ال تاني
public class PostsLoader2 extends AsyncTask<ArrayList<Integer>, Void, ArrayList<Post>> {

    private FavouriteViewModel postsViewModel;
    //private WeakReference<SwipeRefreshLayout> swipeRefreshLayoutRef;

    public PostsLoader2(FavouriteViewModel postsViewModel) {
        this.postsViewModel = postsViewModel;
        //this.swipeRefreshLayoutRef = new WeakReference<>(swipeRefreshLayout);
    }

    private ArrayList<Post> LoadPosts(ArrayList<Integer> favourites) throws SQLException {
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
        Connection connection = null;
        try {
            connection = databaseConnection.getConnection();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (connection == null) {
            Log.d("Database Connection", "Can not connect to db.");
            return new ArrayList<>();
        }
        Statement statement = connection.createStatement();
        StringBuilder query = new StringBuilder("select * from posts WHERE ");
        for (int i = 0; i < favourites.size() - 1; i++) {
            query.append("post_id = ").append(favourites.get(i)).append(" OR ");
        }
        query.append("post_id = ").append(favourites.get(favourites.size() - 1));
        Log.i("Database Connection", "Attempting " + query.toString());
        ResultSet resultSet = statement.executeQuery(query.toString());
        ArrayList<Post> postsLoaded = new ArrayList<>();
        while (resultSet.next()) {
            Song song = new Song(resultSet.getString("post_song_name"),
                    resultSet.getString("post_album_name"),
                    resultSet.getString("post_song_dir"));
            Post post = new Post(song, resultSet.getString("post_picture_dir"));
            postsLoaded.add(post);
            post.setPostID(resultSet.getInt("post_id"));
        }
        resultSet.close();
        databaseConnection.releaseConnection(connection);
        return postsLoaded;
    }

    @Override
    protected ArrayList<Post> doInBackground(ArrayList<Integer>... arrayLists) {
        if (arrayLists[0].isEmpty()) {
            Log.d("Favourites", "Favourites are empty");
            return new ArrayList<>();
        }
        try {
            return LoadPosts(arrayLists[0]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    protected void onPostExecute(ArrayList<Post> posts) {
        super.onPostExecute(posts);
        postsViewModel.getPosts().postValue(posts);
        /*SwipeRefreshLayout swipeRefreshLayout = swipeRefreshLayoutRef.get();
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);*/
    }
}
