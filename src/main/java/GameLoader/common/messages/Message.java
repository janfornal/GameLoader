package GameLoader.common.messages;

import GameLoader.common.Connection;

import java.io.Serializable;

abstract public class Message implements Serializable {
    public transient Connection c = null;
}
