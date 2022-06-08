package GameLoader.client;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;

import java.util.*;

public class ChatManager {
    private final Map<String, Property<String>> mp = new HashMap<>();

    public Property<String> get(String with) {
        if (!mp.containsKey(with))
            mp.put(with, new SimpleStringProperty(""));
        return mp.get(with);
    }

    public void update(String with, String from, String txt) {
        Property<String> p = get(with);
        p.setValue(p.getValue() + from + ": " + txt + "\n");
    }
}
