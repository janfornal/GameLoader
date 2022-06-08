package GameLoader.client;

import GameLoader.common.Game;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.beans.Observable;

import java.io.IOException;
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

    public Node createGameView() {
        return pvm.createView();
    }

    @Override
    public Node createView() {
        InfoView info = new InfoView(this);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/infoView.fxml"));
        loader.setController(info);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return info.getMainNode();
    }

    @Override
    public Observable getObservable() {
        return pvm.getObservable();
    }

    public List<PlayerInfo> getPlayers() {
        return players;
    }
}
