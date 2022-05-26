package GameLoader.common;

import java.io.Serializable;
import java.util.List;

public interface Utility {
    static void runtimeAssert(boolean b) {
        if (!b)
            throw new RuntimeException();
    }

    record IntPair(int first, int second) {
        public int x() {
            return first;
        }
        public int y() {
            return second;
        }
    }

    record Pair<U, V>(U first, V second) {
        public U x() {
            return first;
        }
        public V y() {
            return second;
        }
    }
}
