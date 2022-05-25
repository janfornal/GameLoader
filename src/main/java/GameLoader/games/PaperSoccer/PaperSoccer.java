package GameLoader.games.PaperSoccer;

import GameLoader.client.Client;
import GameLoader.client.PlayViewModel;
import GameLoader.common.Command;
import GameLoader.common.Game;

import java.util.Set;

public class PaperSoccer implements Game {
    @Override
    public void makeMove(Command move) {

    }

    @Override
    public boolean isMoveLegal(Command move) {
        return false;
    }

    @Override
    public void start(String settings, int seed) {

    }

    @Override
    public String getSettings() {
        return null;
    }

    @Override
    public state getState() {
        return null;
    }

    @Override
    public PlayViewModel createViewModel(Client user, int id) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Set<String> possibleSettings() {
        return null;
    }
}