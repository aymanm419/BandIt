package my.bandit.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

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

    public Connection getConnection() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        if (connectionsPool.isEmpty()) {
            DatabaseConnectionCreator databaseConnectionCreator = new DatabaseConnectionCreator();
            return databaseConnectionCreator.createConnection();
        }
        Connection connection = connectionsPool.get(0);
        connectionsPool.remove(0);
        if (connection.isClosed())
            return getConnection();
        return connection;
    }

    public void releaseConnection(Connection connection) {
        connectionsPool.add(connection);
    }

    private static class DatabaseConnectionCreator {
        Connection createConnection() throws ClassNotFoundException, SQLException,
                InstantiationException, IllegalAccessException {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            DriverManager.setLoginTimeout(3);
            return DriverManager.getConnection(DatabaseCredentials.URL, DatabaseCredentials.USER, DatabaseCredentials.PASSWORD);
        }
    }
}
