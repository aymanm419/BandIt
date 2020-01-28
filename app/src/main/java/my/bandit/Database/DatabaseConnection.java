package my.bandit.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class DatabaseConnection {
    private static DatabaseConnection databaseConnection;
    private List<Connection> connectionsPool = new LinkedList<>();

    private DatabaseConnection() {

    }

    public static DatabaseConnection getInstance() {
        if (databaseConnection == null)
            databaseConnection = new DatabaseConnection();
        return databaseConnection;
    }

    public Connection getConnection() throws ExecutionException, InterruptedException {
        if (connectionsPool.isEmpty()) {
            Callable<Connection> callable = new DatabaseConnectionCreator();
            FutureTask<Connection> connectionFutureTask = new FutureTask<>(callable);
            Thread thread = new Thread(connectionFutureTask);
            thread.start();
            return connectionFutureTask.get();
        }
        Connection connection = connectionsPool.get(0);
        connectionsPool.remove(0);
        return connection;
    }

    public void releaseConnection(Connection connection) {
        connectionsPool.add(connection);
    }

    private static class DatabaseConnectionCreator implements Callable<Connection> {
        @Override
        public Connection call() throws Exception {
            Connection connection = null;
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                connection = DriverManager.getConnection(DatabaseCredentials.URL, DatabaseCredentials.USER, DatabaseCredentials.PASSWORD);
            } catch (SQLException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return connection;
        }
    }
}
