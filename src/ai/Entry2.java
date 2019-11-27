package ai;

import map.MapS;

public class Entry2 {
    private MapS map;
    private int x;
    private int y;
    private int xe;
    private int ye;
    private int depth;
    private int bestValue;
    private int hashValue;
    private boolean myTurn;
    private int lowerBound;
    private int upperBound;

    public Entry2(MapS m, int x, int y, int xe, int ye, int depth, int bestValue, int hashValue, boolean myTurn, int lowerBound, int upperBound) {
        this.map = new MapS(m);
        this.x = x;
        this.y = y;
        this.xe = xe;
        this.ye = ye;
        this.depth = depth;
        this.bestValue = bestValue;
        this.hashValue = hashValue;
        this.myTurn = myTurn;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
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

    public int getLowerBound() {
        return lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }
}
