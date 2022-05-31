import GameLoader.common.Service;
import GameLoader.common.Connection;
import static GameLoader.common.Messages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MockClient implements Service {
    private final Connection connection;

    public enum MESSAGE { SENT, RECEIVED };
    public final List<Object> logs = new ArrayList<>();

    public MockClient() throws IOException {
        connection = new Connection(this);
    }

    public void sendMessage(Message message) {
        logs.add(MESSAGE.SENT);
        logs.add(message);
        connection.sendMessage(message);
    }

    @Override
    public void processMessage(Message message, Connection connection) {
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