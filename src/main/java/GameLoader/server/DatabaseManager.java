package GameLoader.server;

import GameLoader.common.AbstractService;

import java.sql.*;
import java.util.function.Supplier;

/**
 * This class is not thread-safe
 */

public class DatabaseManager implements DataManager {
    private final AbstractService service;
    private final Supplier<Connection> connectionSupplier;
    public DatabaseManager(AbstractService s, Supplier<Connection> connectionFactory) {
        service = s;
        connectionSupplier = connectionFactory;
        openConnection();
    }
    public DatabaseManager(AbstractService s) {
        this(s, new ConnectionFactory(s));
    }

    // these variables should either be all nulls are all non-nulls
    private Connection connection;
    private PreparedStatement getPlayerName, getPlayerId, getGameName, getGameId;

    public void close() {
        if (connection == null)
            return;
        try {
            getPlayerName.close();
            getPlayerId.close();
            getGameName.close();
            getGameId.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection = null;
            getPlayerName = getPlayerId = getGameName = getGameId = null;
        }
    }

    private void openConnection() {
        close();

        try {
            connection      = connectionSupplier.get();
            initSchema();
            getPlayerName   = connection.prepareStatement("SELECT name FROM USERS WHERE id = ?");
            getPlayerId     = connection.prepareStatement("SELECT id FROM USERS WHERE name = ?");
            getGameName     = connection.prepareStatement("SELECT name FROM GAMES WHERE id = ?");
            getGameId       = connection.prepareStatement("SELECT id FROM GAMES WHERE name = ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initSchema() throws SQLException {
        final int schemaVersion = 1;

        //

        connection.prepareStatement("DROP TABLE IF EXISTS USERS CASCADE").executeUpdate();
        connection.prepareStatement("CREATE TABLE USERS(ID INT PRIMARY KEY, NAME VARCHAR(40) UNIQUE)").executeUpdate();

        connection.prepareStatement("DROP TABLE IF EXISTS GAMES CASCADE").executeUpdate();
        connection.prepareStatement("CREATE TABLE GAMES(ID INT PRIMARY KEY, NAME VARCHAR(40) UNIQUE)").executeUpdate();
    }

    private int executeIntQuery(PreparedStatement st) throws SQLException {
        try (ResultSet rs = st.executeQuery()) {
            if (!rs.next())
                return -1;
            return rs.getInt(1);
        }
    }

    private String executeStringQuery(PreparedStatement st) throws SQLException {
        try (ResultSet rs = st.executeQuery()) {
            if (!rs.next())
                return null;
            return rs.getString(1);
        }
    }

    @Override
    public int getPlayerId(String name) {
        try {
            getPlayerId.setString(1, name);
            return executeIntQuery(getPlayerId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPlayerName(int i) {
        try {
            getPlayerName.setInt(1, i);
            return executeStringQuery(getPlayerName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getPlayerPassword(int i) { // TODO
        return 0;
    }

    @Override
    public int registerPlayer(String name, long password) { // TODO
        return 0;
    }

    @Override
    public int getGameId(String name) {
        try {
            getGameId.setString(1, name);
            return executeIntQuery(getGameId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getGameName(int i) {
        try {
            getGameName.setInt(1, i);
            return executeStringQuery(getGameName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int registerGame(String name) { // TODO
        return 0;
    }

    @Override
    public int getElo(int player, int game) { // TODO
        return 0;
    }

    @Override
    public void setElo(int player, int game, int elo) { // TODO

    }


}
