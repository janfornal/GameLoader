package GameLoader.common;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;

public class Connection {
    public static final String defaultIP = "localhost";
    public static final int defaultPort = 6666;

    private final Service service;
    private final Socket socket;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;

    private int playerId = Service.INT_NULL;
    private boolean closed = false;

    public Connection(Service service, String ip, int port) throws IOException {
        this(service, new Socket(ip, port));
    }

    public Connection(Service service, String ip) throws IOException {
        this(service, ip, defaultPort);
    }

    public Connection(Service service, int port) throws IOException {
        this(service, defaultIP, port);
    }

    public Connection(Service service) throws IOException {
        this(service, defaultIP, defaultPort);
    }

    public Connection(Service service, Socket socket) {
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

                    service.INC_MESSAGE.println(message + "\t\treceived from " + this);

                    Objects.requireNonNull(message);

                    if (message instanceof Message.Ping pm) {
                        sendMessage(new Message.Pong(pm.p()));
                        continue;
                    }
                    if (message instanceof Message.Pong ignored)
                        continue;

                    try {
                        service.processMessage(message, Connection.this);
                    } catch (Exception e) {
                        e.printStackTrace(service.ERROR_STREAM);
                    }

                } catch (EOFException | SocketException e) {
                    close();
                    return;
                } catch (IOException | ClassNotFoundException | ClassCastException | NullPointerException e) {
                    e.printStackTrace(service.ERROR_STREAM);
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
        for (Message.Any message : messages)
            service.SND_MESSAGE.println(message + "\t\tsending to " + this);

        if (closed)
            return;

        service.execDaemon.execute(() -> {
            synchronized (output) {
                for (Message.Any message : messages) {
                    try {
                        output.writeObject(message);

                        service.SNT_MESSAGE.println(message + "\t\tsent to " + this);

                    } catch (IOException e) {
                        e.printStackTrace(service.ERROR_STREAM);
                        close();
                        break;
                    }
                }
            }
        });
    }

    public void authorize(int id) {
        if (playerId != Service.INT_NULL)
            throw new RuntimeException();

        playerId = id;
    }

    public int getId() {
        return playerId;
    }

    public boolean isAuthorized() {
        return playerId != Service.INT_NULL;
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
            e.printStackTrace(service.ERROR_STREAM);
        }
        service.reportConnectionClosed(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("Connection[");
        sb.append("closed=").append(closed);

        if (isAuthorized())
            sb.append(", playerId=").append(playerId);

        return sb.append("]").toString();
    }
}
