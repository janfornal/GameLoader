package GameLoader.games.SimpleTicTacToe;

import GameLoader.client.Client;
import GameLoader.common.Command;
import GameLoader.common.Game;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Arrays;
import java.util.Set;

public class SimpleTicTacToe implements Game {
    private final int sz = 3;
    private final int[][] T = new int[sz][sz]; // -1 EMPTY, 0 x, 1 o
    {
        for (int i = 0; i < sz; ++i)
            for (int j = 0; j < sz; ++j)
                T[i][j] = -1;
    }
    private String settings;
    private state currState = state.UNFINISHED;
    private int moveCount = 0, turn;
    private SimpleIntegerProperty moveCountProperty;
    private SimpleObjectProperty<state> gameStateProperty;

    @Override
    public void makeMove(Command move) { // assumes that isMoveLegal(move) returns true
        SimpleTicTacToeCommand tttMove = (SimpleTicTacToeCommand) move;
        int pl = tttMove.getPlayer();
        int row = tttMove.getRow();
        int col = tttMove.getCol();

        T[row][col] = pl;
        turn = 1 - turn;
        currState = calcState();

        moveCount++;
        if (moveCountProperty != null)
            moveCountProperty.set(moveCount);
        if (gameStateProperty != null)
            gameStateProperty.set(getState());
    }

    @Override
    public boolean isMoveLegal(Command move) {
        if (move instanceof SimpleTicTacToeCommand tttMove) {
            int pl = tttMove.getPlayer();
            int row = tttMove.getRow();
            int col = tttMove.getCol();
            return settings != null && getState() == state.UNFINISHED
                    && turn == pl && row < sz && col < sz && T[row][col] == -1;
        }
        return false;
    }

    @Override
    public void start(String sett, int seed) {
        if (!possibleSettings().contains(sett))
            throw new IllegalArgumentException("these settings are not permitted");
        settings = sett;
        turn = seed & 1;
    }

    @Override
    public String getSettings() {
        return settings;
    }

    @Override
    public state getState() {
        return currState;
    }

    private state calcState() {
        int winner = -1;
        for (int i = 0; i < sz; ++i) {
            if (T[i][0] == T[i][1] && T[i][1] == T[i][2] && T[i][2] != -1)
                winner = T[i][0];
            if (T[0][i] == T[1][i] && T[1][i] == T[2][i] && T[2][i] != -1)
                winner = T[0][i];
        }
        if (T[0][0] == T[1][1] && T[1][1] == T[2][2] && T[2][2] != -1)
            winner = T[0][0];
        if (T[0][2] == T[1][1] && T[1][1] == T[2][0] && T[2][0] != -1)
            winner = T[0][2];

        if (winner != -1)
            return winner == 0 ? state.P0_WON : state.P1_WON;

        int e = 0;
        for (int i = 0; i < sz; ++i)
            for (int j = 0; j < sz; ++j)
                if (T[i][j] == -1)
                    ++e;

        return e > 0 ? state.UNFINISHED : state.DRAW;
    }

    @Override
    public String getName() {
        return "Tic tac toe";
    }

    @Override
    public Set<String> possibleSettings() {
        return Set.of("Small");
    }

    @Override
    public SimpleTicTacToeViewModel createViewModel(Client cl, int id) {
        return new SimpleTicTacToeViewModel(cl, id, this);
    }

    public int getSize() {
        return sz;
    }

    public int getFieldAt(int i, int j) {
        return T[i][j];
    }

    public int getTurn() {
        return turn;
    }

    public ReadOnlyIntegerProperty getMoveCountProperty() {
        if (moveCountProperty == null)
            moveCountProperty = new SimpleIntegerProperty(moveCount);
        return moveCountProperty;
    }

    public SimpleObjectProperty<state> getGameStateProperty() {
        if (gameStateProperty == null)
            gameStateProperty = new SimpleObjectProperty<state>(getState());
        return gameStateProperty;
    }

    @Override
    public String toString() {
        return "SimpleTicTacToe{" +
                "sz=" + sz +
                ", T=" + Arrays.deepToString(T) +
                ", settings='" + settings + '\'' +
                ", currState=" + currState +
                ", moveCount=" + moveCount +
                ", turn=" + turn +
                ", moveCountProperty=" + moveCountProperty +
                '}';
    }
}
