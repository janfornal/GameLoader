import GameLoader.common.AbstractService;
import GameLoader.common.Connection;
import GameLoader.common.Game;
import GameLoader.common.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MockClient implements AbstractService {
    private final Connection connection;

    public enum MESSAGE { SENT, RECEIVED };
    public final List<Object> logs = new ArrayList<>();

    public MockClient() throws IOException {
        connection = new Connection(this);
    }

    public void sendMessage(Message.Any message) {
        logs.add(MESSAGE.SENT);
        logs.add(message);
        connection.sendMessage(message);
    }

    @Override
    public void processMessage(Message.Any message, Connection connection) {
        logs.add(MESSAGE.RECEIVED);
        logs.add(message);
    }

    @Override
    public void reportConnectionClosed(Connection connection) {

    }

    public void close() {
        connection.close();
    }
}