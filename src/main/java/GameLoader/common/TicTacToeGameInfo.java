package GameLoader.common;

import GameLoader.games.Game;

public record TicTacToeGameInfo(String player, char sym) implements Game.GameInfo {
    @Override
    public String getInfo() {
        return "" + sym;
    }

    @Override
    public String getName() {
        return "TicTacToe";
    }

    @Override
    public PlayerInfo getPlayer() {
        return new PlayerInfo(player);
    }
}
