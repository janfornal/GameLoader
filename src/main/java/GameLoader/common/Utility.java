package GameLoader.common;

import java.io.Serializable;
import java.util.List;

public interface Utility {
    static void runtimeAssert(boolean b) {
        if (!b)
            throw new RuntimeException();
    }
}
