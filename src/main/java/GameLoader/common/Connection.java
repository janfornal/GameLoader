package GameLoader.common;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;

public class Connection {
    public static final String defaultIP = "localhost";
    public static final int defaultPort = 6666;
    private final AbstractService service;
    private final Socket socket;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;
    private String playerName;
    private boolean authorized = false;
    private boolean closed = false;

    public Connection(AbstractService service, String ip, int port) throws IOException {
        this(service, new Socket(ip, port));
    }

    public Connection(AbstractService service, String ip) throws IOException {
        this(service, ip, defaultPort);
    }

    public Connection(AbstractService service, int port) throws IOException {
        this(service, defaultIP, port);
    }

    public Connection(AbstractService service) throws IOException {
        this(service, defaultIP, defaultPort);
    }

    public Connection(AbstractService service, Socket socket) {
        Objects.requireNonNull(service);
        Objects.requireNonNull(socket);

        this.service = service;
        this.socket = socket;

        ObjectOutputStream temp_output = null;
        ObjectInputStream temp_input = null;

        try {
            temp_output = new ObjectOutputStream(socket.getOutputStream());
            temp_input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            close();
        }

        output = temp_output;
        input = temp_input;

        service.execDaemon.execute(() -> {
            while (!closed) {
                try {
                    Message.Any message = (Message.Any) input.readObject();
                    Objects.requireNonNull(message);
                    service.processMessage(message, Connection.this);
                } catch (EOFException | SocketException e) {
                    close();
                    return;
                } catch (IOException | ClassNotFoundException | ClassCastException | NullPointerException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void sendError(String cause) {
        sendMessage(new Message.Error(cause));
    }

    public void sendMessage(Message.Any message) {
        sendMessages(message);
    }

    public void sendMessages(Message.Any... messages) {
        if (closed)
            return;

        service.execDaemon.execute(() -> {
            synchronized (output) {
                for (Message.Any message : messages) {
                    try {
                        output.writeObject(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                        close();
                        break;
                    }
                }
            }
        });
    }

    public void authorize(String pn) {
        authorized = true;
        playerName = pn;
    }

    public String getName() {
        return playerName;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public synchronized void close() {
        if (closed)
            return;
        closed = true;
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        service.reportConnectionClosed(this);
    }
}
