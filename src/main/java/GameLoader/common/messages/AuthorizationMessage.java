package GameLoader.common.messages;

public class AuthorizationMessage extends Message {
    public String name;

    public AuthorizationMessage(String n) {
        name = n;
    }

    @Override
    public String toString() {
        return "AuthorizationMessage{" +
                "name='" + name + '\'' +
                '}';
    }
}