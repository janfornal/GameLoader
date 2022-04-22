package GameLoader.common.messages;

import GameLoader.common.Connection;

import java.io.Serializable;

public interface Message extends Serializable {
    Connection c = null;
    //transient?
}
