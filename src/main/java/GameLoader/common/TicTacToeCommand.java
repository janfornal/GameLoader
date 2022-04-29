package GameLoader.common;

import GameLoader.games.Command;

public record TicTacToeCommand(String player, char sym, int row, int col) implements Command {
    @Override
    public String getPlayer() {
        return player;
    }
}
