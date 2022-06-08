package GameLoader.games.PaperSoccer;

import GameLoader.client.Client;
import GameLoader.common.Game;
import static GameLoader.common.Utility.IntPair;
import static GameLoader.common.Serializables.*;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.*;

import static java.lang.Math.abs;
import static GameLoader.common.Utility.runtimeAssert;

public class PaperSoccer implements Game {
    private static final List<String> settingsList;
    private static final Map<String, IntPair> settingsMap;
    static {
        Map<String, IntPair> mp = new LinkedHashMap<>();
        mp.put("Small", new IntPair(4, 4));
        mp.put("Medium", new IntPair(8, 10));
        mp.put("Large", new IntPair(12, 16));
        settingsMap = Collections.unmodifiableMap(mp);
        settingsList = List.copyOf(mp.keySet());
    }

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
            boolean changePlayer = !e.other(currField).jumpy();

            e.play();
            currField = e.other(currField);

            if (currField.isGoal())
                currState = currField.whoseGoal() == 0 ? state.P1_WON : state.P0_WON;
            else if (currField.finished())
                currState = turn == 0 ? state.P1_WON : state.P0_WON;
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
            return 0 <= dir && dir < dirList.size() && settings != null && getState() == state.UNFINISHED
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

        for (int i = -sz.x() / 2; i <= sz.x() / 2; ++i)
            for (int j = -sz.y() / 2 - 1; j <= sz.y() / 2 + 1; ++j)
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

                if (other == null || !other.isPlayable())
                    continue;

                // weird edge case ; d % 2 == 1 is a diagonal move
                if (f.isCornerGoal() && other.isBorder() && d % 2 == 1)
                    continue;
                if (f.isBorder() && other.isCornerGoal() && d % 2 == 1)
                    continue;

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

    @Override
    public int getTurn() {
        return turn;
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
    public List<String> possibleSettings() {
        return settingsList;
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
                '}';
    }

    public static Integer calcDir(Field from, Field to) {
        return dirMap.get(new IntPair(to.pos.x() - from.pos.x(), to.pos.y() - from.pos.y()));
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
        public boolean isCornerGoal() {
            return apos.x() == goalSz && apos.y() == sz.y() / 2 + 1;
        }
        public int whoseGoal() {
            runtimeAssert(isGoal());
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
            runtimeAssert(isPlayable());
            runtimeAssert(edges[i] == null);
            edges[i] = e;

            if (e.border)
                ++used;
            else
                ++active;
        }
        public void play() {
            runtimeAssert(!finished());
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
        public final boolean border, diagonal;
        public final int goalBorder;
        public boolean active;

        public Edge(Field f1, Field f2) {
            f = f1;
            g = f2;

            int jmp = calcDir(f, g);
            diagonal = jmp % 2 == 1;

            border = f.isBorder() && g.isBorder() && !diagonal;
            active = !border;

            int goalBorderVal;
            if (!border)
                goalBorderVal = -1;
            else if (f.isGoal())
                goalBorderVal = f.whoseGoal();
            else if (g.isGoal())
                goalBorderVal = g.whoseGoal();
            else
                goalBorderVal = -1;
            goalBorder = goalBorderVal;

            f.registerEdge(this, jmp);
            g.registerEdge(this, jmp ^ 4);
        }

        public void play() {
            runtimeAssert(active);
            f.play();
            g.play();
            active = false;
        }
        public Field other(Field o) {
            runtimeAssert(o == f || o == g);
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