package GameLoader.server;

import GameLoader.common.Service;

import java.sql.*;
import java.util.function.Supplier;

/**
 * This class is not thread-safe
 */

public class DatabaseManager implements DataManager {
    private final Service service;
    private final Supplier<Connection> connectionSupplier;

    public DatabaseManager(Service s, Supplier<Connection> connectionFactory) {
        service = s;
        connectionSupplier = connectionFactory;
        openConnection();
    }
    public DatabaseManager(Service s) {
        this(s, new ConnectionFactory(s));
    }

    // these variables should either be all nulls are all non-nulls
    private Connection conn;
    private PreparedStatement getPlayerName, getPlayerPassword, getPlayerId, insertPlayers;
    private PreparedStatement getGameName, getGameId, insertGames;
    private PreparedStatement getElo, modifyElo, insertElo;

    public void close() {
        if (conn == null)
            return;
        try {
            getPlayerName.close();
            getPlayerPassword.close();
            getPlayerId.close();
            insertPlayers.close();
            getGameName.close();
            getGameId.close();
            insertGames.close();
            getElo.close();
            modifyElo.close();
            insertElo.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace(service.ERROR_STREAM);
        } finally {
            conn = null;
            getPlayerName = getPlayerPassword = getPlayerId = insertPlayers = null;
            getGameName = getGameId = null;
            getElo = modifyElo = insertElo = null;
        }
    }

    private void openConnection() {
        close();

        try {
            conn = connectionSupplier.get();
            initSchema();

            getPlayerName       = conn.prepareStatement("SELECT NAME FROM USERS WHERE id = ?");
            getPlayerPassword   = conn.prepareStatement("SELECT PASSWORD FROM USERS WHERE id = ?");
            getPlayerId         = conn.prepareStatement("SELECT ID FROM USERS WHERE name = ?");
            insertPlayers       = conn.prepareStatement("INSERT INTO USERS VALUES (?, ?, ?)");

            getGameName         = conn.prepareStatement("SELECT NAME FROM GAMES WHERE id = ?");
            getGameId           = conn.prepareStatement("SELECT ID FROM GAMES WHERE name = ?");
            insertGames         = conn.prepareStatement("INSERT INTO GAMES VALUES (?, ?)");

            getElo              = conn.prepareStatement("SELECT VAL FROM ELO WHERE PLAYER = ? AND GAME = ?");
            modifyElo           = conn.prepareStatement("UPDATE ELO SET VAL = ? WHERE PLAYER = ? AND GAME = ?");
            insertElo           = conn.prepareStatement("INSERT INTO ELO VALUES (?, ?, ?)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initSchema() throws SQLException {
        final int VERSION = 4;

        try (PreparedStatement st = conn.prepareStatement("SELECT * FROM VERSION")) {
            if (queryInt(st) == VERSION)
                return;
        } catch (SQLException ignored) {

        }

        Service.DB_CONNECTION_INFO_STREAM.println("creating tables...");

        updateOnce(conn.prepareStatement("DROP TABLE IF EXISTS USERS CASCADE"));
        updateOnce(conn.prepareStatement(
                "CREATE TABLE USERS(" +
                        "ID INT PRIMARY KEY, " +
                        "NAME VARCHAR(40) UNIQUE NOT NULL, " +
                        "PASSWORD INT NOT NULL)"
        ));

        updateOnce(conn.prepareStatement("DROP TABLE IF EXISTS GAMES CASCADE"));
        updateOnce(conn.prepareStatement("" +
                "CREATE TABLE GAMES(" +
                        "ID INT PRIMARY KEY, " +
                        "NAME VARCHAR(40) UNIQUE NOT NULL)"
        ));

        updateOnce(conn.prepareStatement("DROP TABLE IF EXISTS ELO CASCADE"));
        updateOnce(conn.prepareStatement("" +
                "CREATE TABLE ELO(" +
                "VAL INT NOT NULL, " +
                "PLAYER INT NOT NULL REFERENCES USERS(ID), " +
                "GAME INT NOT NULL REFERENCES GAMES(ID), " +
                "UNIQUE (PLAYER, GAME))"
        ));

        // FIXME add option to register users and delete this

        for (int i = 0; i < 100; ++i)
            updateOnce(conn.prepareStatement("INSERT INTO USERS VALUES("+i+", 'user"+i+"', 0)"));

        updateOnce(conn.prepareStatement("DROP TABLE IF EXISTS VERSION CASCADE"));
        updateOnce(conn.prepareStatement("CREATE TABLE VERSION(VER INT)"));
        updateOnce(conn.prepareStatement("INSERT INTO VERSION VALUES("+VERSION+")"));
    }

    private int update(PreparedStatement st) throws SQLException {
        Service.DB_QUERY_CALL_STREAM.println(st);
        int res = st.executeUpdate();
        Service.DB_QUERY_RESULT_STREAM.println(st + "\tresult: " + res);
        return res;
    }

    private int updateOnce(PreparedStatement st) throws SQLException {
        int res = update(st);
        st.close();
        return res;
    }

    private int queryInt(PreparedStatement st) throws SQLException {
        try (ResultSet rs = st.executeQuery()) {
            Service.DB_QUERY_CALL_STREAM.println(st);
            int res = rs.next() ? rs.getInt(1) : Service.INT_NULL;
            Service.DB_QUERY_RESULT_STREAM.println(st + "\tresult: " + res);
            return res;
        }
    }

    private String queryString(PreparedStatement st) throws SQLException {
        try (ResultSet rs = st.executeQuery()) {
            Service.DB_QUERY_CALL_STREAM.println(st);
            String res = rs.next() ? rs.getString(1) : null;
            Service.DB_QUERY_RESULT_STREAM.println("\tresult: " + res);
            return res;
        }
    }

    @Override
    public int getPlayerId(String name) {
        try {
            getPlayerId.setString(1, name);
            return queryInt(getPlayerId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPlayerName(int i) {
        try {
            getPlayerName.setInt(1, i);
            return queryString(getPlayerName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getPlayerPassword(int i) {
        try {
            getPlayerPassword.setInt(1, i);
            return queryInt(getPlayerPassword);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int registerPlayer(String name, int password) {
        int id = name.hashCode(); // FIXME assign indices properly
        try {
            insertPlayers.setInt(1, id);
            insertPlayers.setString(2, name);
            insertPlayers.setInt(3, password);
            return update(insertPlayers) > 0 ? id : service.INT_NULL;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getGameId(String name) {
        try {
            getGameId.setString(1, name);
            int r = queryInt(getGameId);
            return r != service.INT_NULL ? r : registerGame(name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getGameName(int i) {
        try {
            getGameName.setInt(1, i);
            return queryString(getGameName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int registerGame(String name) {
        int id = name.hashCode();
        try {
            insertGames.setInt(1, id);
            insertGames.setString(2, name);
            return update(insertGames) > 0 ? id : service.INT_NULL;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getElo(int player, int game) { // TODO
        try {
            getElo.setInt(1, player);
            getElo.setInt(2, game);
            int q = queryInt(getElo);
            return q != service.INT_NULL ? q : service.DEFAULT_ELO;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setElo(int player, int game, int elo) { // TODO
        try {
            modifyElo.setInt(1, elo);
            modifyElo.setInt(2, player);
            modifyElo.setInt(3, game);
            if (update(modifyElo) == 0) {
                insertElo.setInt(1, elo);
                insertElo.setInt(2, player);
                insertElo.setInt(3, game);
                update(insertElo);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
