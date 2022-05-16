package GameLoader.server;

import GameLoader.common.AbstractService;
import java.sql.*;

/**
 * This class is not thread-safe
 */

public class H2DatabaseManager extends DatabaseManager {
    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:./GameLoader";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    static {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Connection initializeConnection() throws SQLException {
        return DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD));
    }

    public H2DatabaseManager(AbstractService ignored) {
        super(ignored);
    }
}
