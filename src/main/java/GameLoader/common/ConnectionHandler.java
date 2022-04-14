package GameLoader.common;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionHandler {
    public static int defaultPort = 6666;
    public static String defaultIp = "localhost";

    private final Socket socket;
    private final PrintWriter outStream;
    private final BufferedReader inStream;

    public final ConcurrentLinkedQueue<String> in = new ConcurrentLinkedQueue<>();
    public final ConcurrentLinkedQueue<String> out = new ConcurrentLinkedQueue<>();

    public ConnectionHandler(Socket s) throws IOException {
        Objects.requireNonNull(s);
        socket = s;
        outStream = new PrintWriter(socket.getOutputStream(), true);
        inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        new Thread(new writeSocket()).start();
        new Thread(new readSocket()).start();
    }

    private class writeSocket implements Runnable {
        @Override
        public void run() {
            while (true) {
                String s = out.poll();
                if (s == null)
                    continue;
                outStream.println(s);
            }
        }
    }
    private class readSocket implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    String s = inStream.readLine();
                    if (s == null)
                        continue;
                    in.add(s);
                }
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }
}
