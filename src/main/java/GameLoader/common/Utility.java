package GameLoader.common;

public class Utility {
    public static void runtimeAssert(boolean b) {
        if (!b)
            throw new RuntimeException();
    }
}
