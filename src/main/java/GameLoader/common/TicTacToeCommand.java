package GameLoader.common;

public record TicTacToeCommand(String player, char sym, int row, int col) implements Game.Command {
    @Override
    public String getPlayer() {
        return player;
    }
}
