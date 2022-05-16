package GameLoader.server;

import GameLoader.common.AbstractService;

import java.sql.*;

/**
 * This class is not thread-safe
 */

public abstract class DatabaseManager implements DataManager {
    protected DatabaseManager(AbstractService ignored) {
        openConnection();
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

    abstract protected Connection initializeConnection() throws SQLException;

    private void openConnection() {
        close();

        try {
            connection      = initializeConnection();
            getPlayerName   = connection.prepareStatement("SELECT name FROM USERS WHERE id = ?");
            getPlayerId     = connection.prepareStatement("SELECT id FROM USERS WHERE name = ?");
            getGameName     = connection.prepareStatement("SELECT name FROM GAMES WHERE id = ?");
            getGameId       = connection.prepareStatement("SELECT id FROM GAMES WHERE name = ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int executeStringToIntQuery(PreparedStatement st, String val) throws SQLException {
        st.setString(1, val);
        try (ResultSet rs = st.executeQuery()) {
            if (!rs.next())
                return -1;
            return rs.getInt(1);
        }
    }

    private String executeIntToStringQuery(PreparedStatement st, int val) throws SQLException {
        st.setInt(1, val);
        try (ResultSet rs = st.executeQuery()) {
            if (!rs.next())
                return null;
            return rs.getString(1);
        }
    }

    @Override
    public int getPlayerId(String name) {
        try {
            return executeStringToIntQuery(getPlayerId, name);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                openConnection();
                return executeStringToIntQuery(getPlayerId, name);
            } catch (SQLException exc) {
                throw new RuntimeException(exc);
            }
        }
    }

    @Override
    public String getPlayerName(int i) {
        try {
            return executeIntToStringQuery(getPlayerName, i);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                openConnection();
                return executeIntToStringQuery(getPlayerName, i);
            } catch (SQLException exc) {
                throw new RuntimeException(exc);
            }
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
            return executeStringToIntQuery(getGameId, name);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                openConnection();
                return executeStringToIntQuery(getGameId, name);
            } catch (SQLException exc) {
                throw new RuntimeException(exc);
            }
        }
    }

    @Override
    public String getGameName(int i) {
        try {
            return executeIntToStringQuery(getGameName, i);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                openConnection();
                return executeIntToStringQuery(getGameName, i);
            } catch (SQLException exc) {
                throw new RuntimeException(exc);
            }
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
