package my.bandit.Repository;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import my.bandit.Database.DatabaseConnection;
import my.bandit.Model.Post;
import my.bandit.Model.Song;

public class PostsLoader extends AsyncTask<Void, Void, ArrayList<Post>> {
    public ArrayList<Post> LoadPosts() throws ExecutionException, InterruptedException, SQLException {
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
        Connection connection = databaseConnection.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from posts");
        ArrayList<Post> postsLoaded = new ArrayList<>();
        while (resultSet.next()) {
            System.out.print("Reading Database");
            Song song = new Song(resultSet.getString("post_song_name"),
                    resultSet.getString("post_album_name"),
                    new File(resultSet.getString("post_song_dir")));
            Post post = new Post(song, BitmapFactory.decodeFile(resultSet.getString("post_picture_dir")));
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
}
