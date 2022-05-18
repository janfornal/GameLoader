import GameLoader.common.Service;
import GameLoader.common.Message;
import GameLoader.server.Server;
import org.junit.*;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

@RunWith(Enclosed.class)
public class GameLoaderTests {
    static void wait50() {
        try {
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static final ThreadPoolExecutor execNormal = (ThreadPoolExecutor) Service.execNormal;
    static final ThreadPoolExecutor execDaemon = (ThreadPoolExecutor) Service.execDaemon;

    public static class ServerTests {
        ReentrantLock lock = new ReentrantLock();
        boolean skipTest;

        @Before
        public void before() throws Throwable {
            lock.lock();
            skipTest = execNormal.getActiveCount() > 0 || execDaemon.getActiveCount() > 0;
            assumeFalse(skipTest);
        }

        @After
        public void after() throws Throwable {
            boolean ok = skipTest;
            for (int i = 0; i < 20 && !ok; ++i) {
                if (execNormal.getActiveCount() > 0 || execDaemon.getActiveCount() > 0)
                    wait50();
                else
                    ok = true;
            }

            try {
                assertTrue(ok);
            } finally {
                lock.unlock();
            }
        }

        @Test(timeout = 500)
        public void closeSimpleTest() throws Throwable {
            Server s = new Server();
            s.close();
        }
        @Test(timeout = 500)
        public void closeTest() throws Throwable {
            Server s = new Server();
            MockClient a = new MockClient();
            MockClient b = new MockClient();
            s.close();
        }
        @Test(timeout = 2000)
        public void simpleMessageTest() throws Throwable {
            Server s = new Server();
            wait50();
            MockClient a = new MockClient();
            wait50();
            a.sendMessage(new Message.Error("x"));
            wait50();
            a.sendMessage(new Message.Authorization("y"));
            wait50();
            a.sendMessage(new Message.Error("z"));
            wait50();
            s.close();

            assertEquals(List.of(
                    MockClient.MESSAGE.SENT,        new Message.Error("x"),
                    MockClient.MESSAGE.RECEIVED,    new Message.Error("You are not authorized"),
                    MockClient.MESSAGE.SENT,        new Message.Authorization("y"),
                    MockClient.MESSAGE.SENT,        new Message.Error("z"),
                    MockClient.MESSAGE.RECEIVED,    new Message.Error("Message not recognized")
            ), a.logs);
        }
    }
}