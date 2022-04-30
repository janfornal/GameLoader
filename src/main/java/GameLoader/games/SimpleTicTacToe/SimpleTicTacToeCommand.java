package GameLoader.games.SimpleTicTacToe;

import GameLoader.common.Command;

public class SimpleTicTacToeCommand extends Command {
    private final int row, col;

    public SimpleTicTacToeCommand(int player, int r, int c) {
        super(player);
        if (r < 0 || c < 0)
            throw new IllegalArgumentException("illegal board coordinate: (" + r + ", " + c + ")");
        row = r;
        col = c;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
