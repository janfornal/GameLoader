package GameLoader.games.PaperSoccer;

import GameLoader.client.Client;
import GameLoader.common.Command;
import GameLoader.common.Game;
import GameLoader.common.IntPair;
import GameLoader.common.ResignationCommand;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.*;

import static java.lang.Math.abs;

public class PaperSoccer implements Game {
    private static final Map<String, IntPair> settingsMap = Map.of(
            "Small", new IntPair(2, 2),
            "Medium", new IntPair(4, 5),
            "Large", new IntPair(6, 8)
    );

    public static final List<IntPair> dirList = List.of(
            new IntPair(0, 1), new IntPair(1, 1), new IntPair(1, 0), new IntPair(1, -1),
            new IntPair(0, -1), new IntPair(-1, -1), new IntPair(-1, 0), new IntPair(-1, 1)
    );
    public static final Map<IntPair, Integer> dirMap;
    static {
        HashMap<IntPair, Integer> mp = new HashMap<>();
        for (int i = 0; i < dirList.size(); ++i)
            mp.put(dirList.get(i), i);
        dirMap = Map.copyOf(mp);
    }

    private IntPair sz;
    private final int goalSz = 1;
    private Field currField;

    private String settings;
    private state currState = state.UNFINISHED;
    private int moveCount = 0, turn;
    private SimpleIntegerProperty moveCountProperty;

    private List<Edge> allEdges;
    private List<Field> allFields;

    @Override
    public void makeMove(Command cmd) {
        if (cmd instanceof ResignationCommand res)
            currState = res.getPlayer() == 0 ? state.P1_WON : state.P0_WON;
        if (cmd instanceof PaperSoccerCommand psCmd) {
            int dir = psCmd.getDir();

            Edge e = currField.edges[dir];
            boolean changePlayer = !e.play(currField);
            currField = e.other(currField);

            if (currField.isGoal())
                currState = currField.whoseGoal() == 0 ? state.P1_WON : state.P0_WON;
            else if (changePlayer)
                turn = 1 - turn;
        }
        moveCount++;
        if (moveCountProperty != null)
            moveCountProperty.set(moveCount);
    }

    @Override
    public boolean isMoveLegal(Command cmd) {
        if (cmd instanceof ResignationCommand)
            return getState() == state.UNFINISHED;
        if (cmd instanceof PaperSoccerCommand psCmd) {
            int pl = psCmd.getPlayer();
            int dir = psCmd.getDir();
            return settings != null && getState() == state.UNFINISHED
                    && turn == pl && currField.edges[dir] != null && currField.edges[dir].active;
        }
        return false;
    }

    @Override
    public void start(String settings, int seed) {
        sz = settingsMap.get(settings);
        if (sz == null)
            throw new IllegalArgumentException("these settings are not permitted");
        this.settings = settings;
        turn = seed & 1;

        Map<IntPair, Field> mp = new HashMap<>();

        for (int i = -sz.x() - 2; i <= sz.x() + 2; ++i)
            for (int j = -sz.y() - 2; j <= sz.y() + 2; ++j)
                mp.put(new IntPair(i, j), new Field(i, j));

        currField = mp.get(new IntPair(0, 0));

        allEdges = new ArrayList<>();
        allFields = new ArrayList<>(mp.values());

        for (Field f : allFields) {
            if (!f.isPlayable())
                continue;
            for (int d = 0; d < dirList.size() / 2; ++d) {
                IntPair coordOther = new IntPair(f.pos.x() + dirList.get(d).x(), f.pos.y() + dirList.get(d).y());
                Field other = mp.get(coordOther);

                if (other != null)
                    allEdges.add(new Edge(f, other));
            }
        }
    }

    @Override
    public String getSettings() {
        return settings;
    }

    @Override
    public state getState() {
        return currState;
    }

    public int getTurn() {
        return turn;
    }

    public IntPair getSz() {
        return sz;
    }

    public Field getCurrField() {
        return currField;
    }

    public List<Edge> getAllEdges() {
        return allEdges;
    }

    public List<Field> getAllFields() {
        return allFields;
    }

    @Override
    public PaperSoccerViewModel createViewModel(Client user, int id) {
        return new PaperSoccerViewModel(user, id, this);
    }

    @Override
    public String getName() {
        return "Paper soccer";
    }

    @Override
    public Set<String> possibleSettings() {
        return settingsMap.keySet();
    }

    public ReadOnlyIntegerProperty getMoveCountProperty() {
        if (moveCountProperty == null)
            moveCountProperty = new SimpleIntegerProperty(moveCount);
        return moveCountProperty;
    }

    @Override
    public String toString() {
        return "PaperSoccer{" +
                "sz=" + sz +
                ", goalSz=" + goalSz +
                ", currField=" + currField +
                ", settings='" + settings + '\'' +
                ", currState=" + currState +
                ", moveCount=" + moveCount +
                ", turn=" + turn +
                ", allEdges=" + allEdges +
                ", allFields=" + allFields +
                '}';
    }

    public class Field {
        public final IntPair pos, apos;
        public final Edge[] edges = new Edge[8];
        public int used = 0, active = 0;

        public Field(int i, int j) {
            pos = new IntPair(i, j);
            apos = new IntPair(abs(i), abs(j));
        }

        public boolean isGoal() {
            return apos.x() <= goalSz && apos.y() == sz.y() / 2 + 1;
        }
        public int whoseGoal() {
            assert isGoal();
            return pos.y() > 0 ? 1 : 0;
        }
        public boolean finished() {
            return active == 0;
        }
        public boolean jumpy() {
            return used > 0;
        }
        public boolean isBorder() {
            if (isGoal())
                return true;
            if (!isPlayable())
                return false;
            return apos.x() == sz.x() / 2 || (apos.y() == sz.y() / 2 && apos.x() >= goalSz);
        }
        public boolean isPlayable() {
            if (isGoal())
                return true;
            return apos.x() <= sz.x() / 2 && apos.y() <= sz.y() / 2;
        }
        public void registerEdge(Edge e, int i) {
            assert isPlayable();
            assert edges[i] == null;
            edges[i] = e;

            if (e.border)
                ++used;
            else
                ++active;
        }
        public void play() {
            assert !finished();
            ++used;
            --active;
        }

        @Override
        public String toString() {
            return "Field{" +
                    "pos=" + pos +
                    ", apos=" + apos +
                    ", edges=" + Arrays.toString(edges) +
                    ", used=" + used +
                    ", active=" + active +
                    '}';
        }
    }
    public class Edge {
        public final Field f, g;
        public final boolean border;
        public boolean active;

        public Edge(Field f1, Field f2) {
            f = f1;
            g = f2;

            border = f.isBorder() && g.isBorder();
            active = !border;

            int jmp = dirMap.get(new IntPair(g.pos.x() - f.pos.x(), g.pos.y() - f.pos.y()));

            f.registerEdge(this, jmp);
            g.registerEdge(this, jmp ^ 4);
        }

        /**
         * @return true if the other field has active edge, false otherwise
         */
        public boolean play(Field played) {
            assert active;
            boolean ret = other(played).jumpy();
            f.play();
            g.play();
            active = false;
            return ret;
        }
        public Field other(Field o) {
            assert o == f || o == g;
            return o == f ? g : f;
        }

        @Override
        public String toString() {
            return "Edge{" +
                    "f=" + f.pos +
                    ", g=" + g.pos +
                    ", border=" + border +
                    ", active=" + active +
                    '}';
        }
    }
}