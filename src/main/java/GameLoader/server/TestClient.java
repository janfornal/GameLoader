package GameLoader.server;

import GameLoader.client.PlayViewModel;
import GameLoader.common.AbstractService;
import GameLoader.common.Connection;
import GameLoader.common.Game;
import GameLoader.common.messages.AuthorizationMessage;
import GameLoader.common.messages.CreateRoomMessage;
import GameLoader.common.messages.Message;
import com.sun.javafx.print.Units;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TestClient extends AbstractService {
    PlayViewModel currentGame;
    Connection c;

    public TestClient() throws IOException, InterruptedException {
        c = new Connection();
        System.err.println("Connection established");
        c.sendMessage(new CreateRoomMessage());
        TimeUnit.MILLISECONDS.sleep(1000);
        c.sendMessage(new AuthorizationMessage("username"));
        TimeUnit.MILLISECONDS.sleep(1000);
        c.sendMessage(new CreateRoomMessage());
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new TestClient();
        while (true) {

        }
    }

    @Override
    public void processMessage(Message m) {
        System.out.println(m);
    }

    @Override
    public void reportGameEnded(Game.GameInstance gm) {
        currentGame = null;
    }
}
