package GameLoader.client;

import GameLoader.common.AbstractService;
import GameLoader.common.Connection;
import GameLoader.common.Game;
import GameLoader.common.Message;

import java.io.IOException;
import java.net.Socket;

public class Client implements AbstractService {
    ViewModel currentGame;

    Client() {
    }

    void gameEnded() {

    }

    @Override
    public void processMessage(Message.Any m, Connection c) {

    }

    @Override
    public void reportGameEnded(Game gm) {

    }

    @Override
    public void reportConnectionClosed(Connection connection) {

    }
}
