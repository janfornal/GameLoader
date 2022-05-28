package GameLoader.server;

import GameLoader.common.Service;

import java.sql.*;
import java.util.function.Supplier;

/**
 * This class is not thread-safe
 */
public class DatabaseConnectionFactory implements Supplier<Connection> {
    private static final String H2_DB_DRIVER = "org.h2.Driver";
    private static final String H2_DB_CONNECTION = "jdbc:h2:./GameLoader";
    private static final String H2_DB_USER = "";
    private static final String H2_DB_PASSWORD = "";

    private static final String PSQL_DB_DRIVER = "org.postgresql.Driver";
    private static final String PSQL_DB_CONNECTION = "jdbc:postgresql:gameloader";
    private static final String PSQL_DB_USER = "gameloader";
    private static final String PSQL_DB_PASSWORD = "gameloader";

    static {
        try {
            Class.forName(H2_DB_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace(Service.DB_DRIVER_ERROR_STREAM);
        }

        try {
            Class.forName(PSQL_DB_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace(Service.DB_DRIVER_ERROR_STREAM);
        }
    }

    private Connection get(String url, String user, String password) {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace(Service.DB_CONNECTION_ERROR_STREAM);
            return null;
        }
    }

    @Override
    public Connection get() {
        Connection psql = get(PSQL_DB_CONNECTION, PSQL_DB_USER, PSQL_DB_PASSWORD);

        if (psql != null) {
            Service.DB_CONNECTION_INFO_STREAM.println("connected to postgres server");
            return psql;
        }

        Connection h2 = get(H2_DB_CONNECTION, H2_DB_USER, H2_DB_PASSWORD);

        if (h2 != null) {
            Service.DB_CONNECTION_INFO_STREAM.println("connected to h2 embedded database");
            return h2;
        }

        throw new RuntimeException("unable to connect to database");
    }
}
