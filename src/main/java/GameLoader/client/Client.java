package GameLoader.client;

import GameLoader.common.*;
import GameLoader.games.DotsAndBoxes.*;
import GameLoader.games.SimpleTicTacToe.*;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Client implements AbstractService {
    private ViewModel currentModel;
    private final Connection activeConnection;
    private RoomInfo chosenGame;
    public PlayerInfo username;
    private String availableGames;
    private final Map<String, GameClasses> gameMap = new HashMap<>() {{
        put(new DotsAndBoxes().getName(), new GameClasses(DotsAndBoxes.class, DotsAndBoxesView.class, DotsAndBoxesViewModel.class));
        put(new SimpleTicTacToe().getName(), new GameClasses(SimpleTicTacToe.class, SimpleTicTacToeView.class, SimpleTicTacToeViewModel.class));
    }};

    private record GameClasses(Class<? extends Game> gameClass,
                               Class<? extends PlayView> gameViewClass,
                               Class<? extends PlayViewModel> gameModelClass) {}

    Client() throws IOException {
        ClientGUI.user = this;
        activeConnection = new Connection(Client.this);
        ClientGUI.launch(ClientGUI.class);
    }

    public void setCurrentModel(ViewModel Model) {
        currentModel = Model;
    }

    public void setChosenGame(RoomInfo game) {
        chosenGame = game;
    }

    void gameEnded() {
    }

    @Override
    public void processMessage(Message.Any message, Connection c) {
        Objects.requireNonNull(message);
        Objects.requireNonNull(c);

        if(message instanceof Message.Authorization)
            ClientGUI.authorizationLockNotify();
        else if(message instanceof Message.RoomList messageCast && currentModel instanceof MenuViewModel currentModelCast) {
            System.out.println(FXCollections.observableArrayList(messageCast.rooms()));
            currentModelCast.getElements().roomTableView().setItems(FXCollections.observableArrayList(messageCast.rooms()));
        }
        else if(message instanceof Message.StartGame messageCast && currentModel instanceof MenuViewModel) {
            GameClasses gamePackage = gameMap.get(chosenGame.game());
            Game starterInstance;
            PlayViewModel currentModel;
            try {
                starterInstance = gamePackage.gameClass().getConstructor().newInstance();
                currentModel = starterInstance.createViewModel(this, 0);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                sendMessage(new Message.Error("Constructor of game cannot be called"));
                return;
            }
            starterInstance.start(chosenGame.settings(), messageCast.seed());
            ClientGUI.switchStage(currentModel);
        }
        else if(message instanceof Message.Move messageCast && currentModel instanceof PlayViewModel currentModelCast) {
            currentModelCast.processMoveMessage(messageCast);
        }
        else if(message instanceof Message.Error messageCast) {
            if(messageCast.cause().equals("Unsuccessful authorization")) {
                username = null;
                ClientGUI.authorizationLockNotify();
            }
        }
        else c.sendError("Message not recognized");
    }

    @Override
    public void reportGameEnded(Game gm) {

    }

    @Override
    public void reportConnectionClosed(Connection connection) {

    }

    public void sendError(String cause) {
        sendMessage(new Message.Error(cause));
    }

    public void sendMessage(Message.Any message) {
        if(message instanceof Message.Authorization authMessage) {
            username = new PlayerInfo(authMessage.name());
        }
        activeConnection.sendMessage(message);
    }
}
