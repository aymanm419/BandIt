package my.bandit.Repository;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;

import my.bandit.Database.DatabaseConnection;
import my.bandit.data.Result;
import my.bandit.data.model.LoggedInUser;

public class AccountRegister extends AsyncTask<String, String, Result> {
    Connection connection;
    DatabaseConnection databaseConnection = DatabaseConnection.getInstance();

    private void createUser(String username, String password) throws SQLException {
        Statement statement = connection.createStatement();
        Log.d("Register", "Creating table entry" + "INSERT INTO accounts(ID, username, password) VALUES (NULL, '" + username + "','" + password + "')");
        statement.execute("INSERT INTO accounts(ID, username, password) VALUES (NULL, '" + username + "','" + password + "')");
    }

    private boolean checkIfUserExists(String username) throws SQLException, ExecutionException, InterruptedException {
        connection = databaseConnection.getConnection();
        if (connection == null) {
            Log.d("Database Connection", "Can not connect to db.");
            return false;
        }
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from accounts where username='" + username + "'");
        return resultSet.next();
    }

    @Override
    protected Result doInBackground(String... strings) {
        try {
            if (!checkIfUserExists(strings[0])) {
                Log.i("Register", "Attempting to create user");
                createUser(strings[0], strings[1]);
                databaseConnection.releaseConnection(connection);
                return new Result.Success<>(new LoggedInUser(0, ""));
            } else {
                Log.i("Register", "Existing user");
                databaseConnection.releaseConnection(connection);
                return new Result.Error(new Exception("User already exists"));
            }
        } catch (SQLException | InterruptedException | ExecutionException e) {
            return new Result.Error(new IOException("Error registering", e));
        }
    }
}
