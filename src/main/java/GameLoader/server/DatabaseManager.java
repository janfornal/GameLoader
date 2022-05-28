package GameLoader.server;

import GameLoader.common.Service;

import java.sql.*;
import java.util.function.Supplier;

/**
 * This class is not thread-safe
 */
public class DatabaseManager implements DataManager {
    private final Server server;
    private final Supplier<Connection> connectionSupplier;

    public DatabaseManager(Server s, Supplier<Connection> connectionFactory) {
        server = s;
        connectionSupplier = connectionFactory;

        openConnection();

        for (String game : server.gameTypeManager.getGameNames())
            registerGame(game);
    }

    // these variables should either be all nulls or all non-nulls
    private Connection conn;
    private PreparedStatement getPlayerName, getPlayerPassword, getPlayerId, insertPlayers;
    private PreparedStatement getGameName, getGameId, insertGames;
    private PreparedStatement getElo, modifyElo, insertElo;
    private PreparedStatement nextId;

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
            nextId.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace(server.ERROR_STREAM);
        } finally {
            conn = null;
            getPlayerName = getPlayerPassword = getPlayerId = insertPlayers = null;
            getGameName = getGameId = null;
            getElo = modifyElo = insertElo = null;
            nextId = null;
        }
    }

    private void openConnection() {
        close();

        try {
            conn = connectionSupplier.get();
            initSchema();

            getPlayerName       = conn.prepareStatement("SELECT NAME FROM USERS WHERE ID = ?");
            getPlayerPassword   = conn.prepareStatement("SELECT PASSWORD FROM USERS WHERE ID = ?");
            getPlayerId         = conn.prepareStatement("SELECT ID FROM USERS WHERE NAME = ?");
            insertPlayers       = conn.prepareStatement("INSERT INTO USERS VALUES (?, ?, ?)");

            getGameName         = conn.prepareStatement("SELECT NAME FROM GAMES WHERE ID = ?");
            getGameId           = conn.prepareStatement("SELECT ID FROM GAMES WHERE NAME = ?");
            insertGames         = conn.prepareStatement("INSERT INTO GAMES VALUES (?, ?)");

            getElo              = conn.prepareStatement("SELECT VAL FROM ELO WHERE PLAYER = ? AND GAME = ?");
            modifyElo           = conn.prepareStatement("UPDATE ELO SET VAL = ? WHERE PLAYER = ? AND GAME = ?");
            insertElo           = conn.prepareStatement("INSERT INTO ELO VALUES (?, ?, ?)");

            nextId              = conn.prepareStatement("SELECT NEXTVAL('SEQ_ID')");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initSchema() throws SQLException {
        final int VERSION = 9; // increase this to reset database;

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
                        "PASSWORD VARCHAR(50) NOT NULL)"
        )); // FIXME how long should be password field?

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

        updateOnce(conn.prepareStatement("DROP SEQUENCE IF EXISTS SEQ_ID"));
        updateOnce(conn.prepareStatement("CREATE SEQUENCE SEQ_ID"));

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

    private Integer queryInt(PreparedStatement st) throws SQLException {
        try (ResultSet rs = st.executeQuery()) {
            Service.DB_QUERY_CALL_STREAM.println(st);
            Integer res = rs.next() ? rs.getInt(1) : null;
            Service.DB_QUERY_RESULT_STREAM.println(st + "\tresult: " + res);
            return res;
        }
    }

    private Long queryLong(PreparedStatement st) throws SQLException {
        try (ResultSet rs = st.executeQuery()) {
            Service.DB_QUERY_CALL_STREAM.println(st);
            Long res = rs.next() ? rs.getLong(1) : null;
            Service.DB_QUERY_RESULT_STREAM.println(st + "\tresult: " + res);
            return res;
        }
    }

    private String queryString(PreparedStatement st) throws SQLException {
        try (ResultSet rs = st.executeQuery()) {
            Service.DB_QUERY_CALL_STREAM.println(st);
            String res = rs.next() ? rs.getString(1) : null;
            Service.DB_QUERY_RESULT_STREAM.println(st + "\tresult: " + res);
            return res;
        }
    }

    @Override
    public int nextId() {
        try {
            return queryInt(nextId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer getPlayerId(String name) {
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
    public String getPlayerPassword(int i) {
        try {
            getPlayerPassword.setInt(1, i);
            return queryString(getPlayerPassword);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer registerPlayer(String name, String password) {
        if (playerExists(name))
            return null;
        try {
            int id = nextId();
            insertPlayers.setInt(1, id);
            insertPlayers.setString(2, name);
            insertPlayers.setString(3, password);
            return update(insertPlayers) > 0 ? id : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer getGameId(String name) {
        try {
            getGameId.setString(1, name);
            return queryInt(getGameId);
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
    public Integer registerGame(String name) {
        if (gameExists(name))
            return null;
        try {
            int id = nextId();
            insertGames.setInt(1, id);
            insertGames.setString(2, name);
            return update(insertGames) > 0 ? id : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getElo(int player, int game) {
        try {
            getElo.setInt(1, player);
            getElo.setInt(2, game);
            Integer q = queryInt(getElo);
            return q != null ? q : Service.DEFAULT_ELO;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setElo(int player, int game, int elo) {
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
