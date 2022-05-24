package GameLoader.utility;

public record IntPair(int first, int second) {
    public int x() {
        return first;
    }
    public int y() {
        return second;
    }
}