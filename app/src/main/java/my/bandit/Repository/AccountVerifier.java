package my.bandit.Repository;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import my.bandit.Database.DatabaseConnection;

public class AccountVerifier extends AsyncTask<String, String, Boolean> {

    private boolean verifyAccount(String username, String password) throws Exception {
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
        Connection connection = databaseConnection.getConnection();
        if (connection == null) {
            Log.d("Database Connection", "Can not connect to db.");
            return false;
        }
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from accounts where username='" + username + "'");
        boolean success = false;
        while (resultSet.next()) {
            if (resultSet.getString("password").equals(password)) {
                Log.d("Login", "Successful authentication");
                success = true;
            } else {
                Log.d("Login", "Invalid password");
                success = false;
            }
        }
        if (resultSet.getRow() == 0)
            Log.d("Login", "Username not found");
        resultSet.close();
        databaseConnection.releaseConnection(connection);
        return success;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            return verifyAccount(strings[0], strings[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
