package my.bandit.Repository;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import my.bandit.Database.DatabaseConnection;
import my.bandit.data.model.LoggedInUser;

public class AccountLoader extends AsyncTask<String, String, LoggedInUser> {

    LoggedInUser user;
    private ArrayList<Integer> favourites = new ArrayList<>();
    private ArrayList<Integer> like = new ArrayList<>();
    private ArrayList<Integer> dislike = new ArrayList<>();

    private void fetchLists(Statement statement) throws SQLException {
        Log.d("Login", "Fetching likes");
        ResultSet resultSet = statement.executeQuery("select * from likes where ID = " + user.getUserId());
        while (resultSet.next()) {
            like.add(resultSet.getInt("postID"));
        }
        Log.d("Login", "Fetching dislikes");
        resultSet = statement.executeQuery("select * from dislikes where ID = " + user.getUserId());
        while (resultSet.next()) {
            dislike.add(resultSet.getInt("postID"));
        }
        Log.d("Login", "Fetching favourites");
        resultSet = statement.executeQuery("select * from favourites where ID = " + user.getUserId());
        while (resultSet.next()) {
            favourites.add(resultSet.getInt("postID"));
        }
    }

    private LoggedInUser getUserData(String username) throws ExecutionException, InterruptedException, SQLException {
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
        Connection connection = databaseConnection.getConnection();
        if (connection == null) {
            Log.d("Database Connection", "Can not connect to db.");
            return null;
        }
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from accounts where username='" + username + "'");
        int id = 0;
        while (resultSet.next()) {
            id = resultSet.getInt("ID");
        }
        user = new LoggedInUser(id, username);
        fetchLists(statement);
        user.setDisliked(dislike);
        user.setFavourites(favourites);
        user.setLiked(like);
        resultSet.close();
        databaseConnection.releaseConnection(connection);
        return user;
    }

    @Override
    protected LoggedInUser doInBackground(String... strings) {
        try {
            return getUserData(strings[0]);
        } catch (ExecutionException | InterruptedException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
