package ai;

import map.Map;
import map.MapS;

public class Entry {
    private Map map;
    private int x;
    private int y;
    private int xe;
    private int ye;
    private int depth;
    private int alpha;
    private int beta;
    private int bestValue;
    private int hashValue;
    private boolean myTurn;

    public Entry(Map m, int x, int y, int xe, int ye, int depth, int alpha, int beta, int bestValue, int hashValue, boolean myTurn) {
        this.map = new Map(m);
        this.x = x;
        this.y = y;
        this.xe = xe;
        this.ye = ye;
        this.depth = depth;
        this.alpha = alpha;
        this.beta = beta;
        this.bestValue = bestValue;
        this.hashValue = hashValue;
        this.myTurn = myTurn;
    }

    public int getBestValue() {
        return bestValue;
    }

    public int getHashValue() {
        return hashValue;
    }

    public boolean getMyTurn() {
        return myTurn;
    }
}
