package GameLoader.common;

public record Pair<U, V>(U first, V second) {
    public U x() {
        return first;
    }
    public V y() {
        return second;
    }
}
