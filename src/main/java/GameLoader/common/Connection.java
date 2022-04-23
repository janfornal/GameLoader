package GameLoader.common;

import GameLoader.common.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class Connection {
    public static final ExecutorService execDaemon = AbstractService.execDaemon;
    public static final String defaultIP = "localhost";
    public static final int defaultPort = 6666;
    private static final Consumer<Message> processMessage = AbstractService.getInstance()::processMessage;
    private final Socket socket;
    private final ObjectInputStream inp;
    private final ObjectOutputStream out;
    private String name;
    private boolean authorized = false;

    public Connection(String ip, int port) throws IOException {
        this(new Socket(ip, port));
    }

    public Connection(String ip) throws IOException {
        this(ip, defaultPort);
    }

    public Connection(int port) throws IOException {
        this(defaultIP, port);
    }

    public Connection() throws IOException {
        this(defaultIP, defaultPort);
    }

    public Connection(Socket s) throws IOException {
        socket = s;

        inp = new ObjectInputStream(s.getInputStream());
        out = new ObjectOutputStream(s.getOutputStream());

        execDaemon.execute(new ListeningConnection());
    }

    public void sendMessage(Message m) {
        execDaemon.execute(() -> {
            synchronized (out) {
                try {
                    out.writeObject(m);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void authorize(String n) {
        authorized = true;
        name = n;
    }

    public String getName() {
        return name;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    class ListeningConnection implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Message m = (Message) inp.readObject();
                    Objects.requireNonNull(m);
                    m.c = Connection.this;
                    processMessage.accept(m);
                } catch (IOException | ClassNotFoundException | ClassCastException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
