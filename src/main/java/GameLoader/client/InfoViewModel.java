package GameLoader.client;

import GameLoader.common.Game;

import java.util.List;

import static GameLoader.common.Messages.*;
import static GameLoader.common.Serializables.*;

public class InfoViewModel implements PlayViewModel {
    private final PlayViewModel pvm;
    private final List<PlayerInfo> players;
    public InfoViewModel(PlayViewModel playViewModel, PlayerInfo p0, PlayerInfo p1) {
        pvm = playViewModel;
        players = List.of(p0, p1);
    }

    @Override
    public Game getGame() {
        return pvm.getGame();
    }

    @Override
    public int playingAs() {
        return pvm.playingAs();
    }

    @Override
    public void setElements(GuiElements fooElements) {
        pvm.setElements(fooElements);
    }

    @Override
    public void processMoveMessage(MoveMessage msg) {
        pvm.processMoveMessage(msg);
    }

    @Override
    public void userCmd(Command cmd) {
        pvm.userCmd(cmd);
    }

    @Override
    public Client getModelUser() {
        return pvm.getModelUser();
    }

    public GeneralView createGameView() {
        return pvm.createView();
    }

    @Override
    public InfoView createView() {
        return new InfoView(this);
    }

    public List<PlayerInfo> getPlayers() {
        return players;
    }
}
