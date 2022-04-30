package GameLoader.games.SimpleTicTacToe;

import GameLoader.common.Command;
import GameLoader.common.Game;

import java.util.Set;

public class SimpleTicTacToe implements Game {
    private final int sz = 3;
    private final int[][] T = new int[sz][sz]; // -1 EMPTY, 0 x, 1 o
    private int turn = 0;
    private String settings;

    @Override
    public void makeMove(Command move) { // assumes that isMoveLegal(move) returns true
        SimpleTicTacToeCommand tttMove = (SimpleTicTacToeCommand) move;
        int pl = tttMove.getPlayer();
        int row = tttMove.getRow();
        int col = tttMove.getCol();
        T[row][col] = pl;
    }

    @Override
    public boolean isMoveLegal(Command move) {
        if (move instanceof SimpleTicTacToeCommand tttMove) {
            int pl = tttMove.getPlayer();
            int row = tttMove.getRow();
            int col = tttMove.getCol();
            return getState() == state.UNFINISHED && turn == pl && row < sz && col < sz && T[row][col] == -1;
        }
        return false;
    }

    @Override
    public void start(String sett, int seed) {
        if (!possibleSettings().contains(settings))
            throw new IllegalArgumentException("these settings are not permitted");
        settings = sett;
    }

    @Override
    public String getSettings() {
        return settings;
    }

    @Override
    public state getState() {
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
        return "3x3 Tic Tac Toe";
    }

    @Override
    public Set<String> possibleSettings() {
        return Set.of("3 x 3");
    }
}
