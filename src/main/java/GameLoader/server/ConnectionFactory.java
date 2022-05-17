package GameLoader.server;

import GameLoader.common.AbstractService;
import java.sql.*;
import java.util.function.Supplier;

/**
 * This class is not thread-safe
 */

public class ConnectionFactory implements Supplier<Connection> {
    private static final String H2_DB_DRIVER = "org.h2.Driver";
    private static final String H2_DB_CONNECTION = "jdbc:h2:./GameLoader";

    private static final String PSQL_DB_DRIVER = "org.postgresql.Driver";
    private static final String PSQL_DB_CONNECTION = "jdbc:postgresql:gameloader";
    private static final String PSQL_DB_USER = "gameloader";
    private static final String PSQL_DB_PASSWORD = "gameloader";

    static {
        try {
            Class.forName(H2_DB_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Class.forName(PSQL_DB_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private final AbstractService service;
    public ConnectionFactory(AbstractService s) {
        service = s;
    }

    @Override
    public Connection get() {
        try {
            Connection psql = DriverManager.getConnection(PSQL_DB_CONNECTION, PSQL_DB_USER, PSQL_DB_PASSWORD);
            service.INFO_STREAM.println("connected to postgres server");
            return psql;
        } catch (SQLException e) {
            try {
                Connection h2 = DriverManager.getConnection(H2_DB_CONNECTION);
                service.INFO_STREAM.println("connected to h2 embedded database");
                return h2;
            } catch (SQLException ex) {
                e.printStackTrace();
                ex.printStackTrace();
                throw new RuntimeException();
            }
        }
    }
}
