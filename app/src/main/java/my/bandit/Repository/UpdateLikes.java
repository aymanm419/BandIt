package my.bandit.Repository;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;

import my.bandit.Database.DatabaseConnection;

public class UpdateLikes extends AsyncTask<Integer, Integer, Void> {

    private Void updateLikes(int postID, int userID, int change, int type) throws SQLException, ExecutionException, InterruptedException {
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
        Connection connection = databaseConnection.getConnection();
        if (connection == null) {
            Log.d("Database Connection", "Can not connect to db.");
            return null;
        }
        Statement statement = connection.createStatement();

        String col = type == 0 ? "post_upvote" : "post_downvote";
        Log.d("Post", "Updating post data");
        statement.execute("UPDATE posts SET " + col + " = " + col + " + " + change + " WHERE post_id = " + postID + ";");
        String table = type == 0 ? "likes" : "dislikes";
        Log.d("Post", "Updating tables");
        if (change == 1)
            statement.execute("INSERT INTO " + table + " VALUES (" + userID + "," + postID + ");");
        else
            statement.execute("DELETE FROM " + table + " WHERE ID = " + userID + " AND postID = " + postID + ";");
        return null;
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        try {
            return updateLikes(integers[1], integers[0], integers[2], integers[3]);
        } catch (SQLException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
