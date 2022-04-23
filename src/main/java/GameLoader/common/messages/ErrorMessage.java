package GameLoader.common.messages;

public class ErrorMessage extends Message {
    public String info;

    public ErrorMessage(String s) {
        info = s;
    }

    public ErrorMessage() {
        this("Error encountered");
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "info='" + info + '\'' +
                '}';
    }
}
