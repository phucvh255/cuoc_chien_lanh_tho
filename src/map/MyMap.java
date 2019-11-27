package map;

public class MyMap {
    public static final int SPACE = 0;
    public static final int WALL = 3;
    public static final int GREEN = 1;
    public static final int RED = 2;
    public static final int TEMP = 4;
    public static final int N = 15;
    public int[] map = new int[225];

    public MyMap(Map mm) {
        int[][] m = mm.map;

        for(int i = 0; i < 15; ++i) {
            for(int j = 0; j < 15; ++j) {
                this.map[i * 15 + j] = m[i][j];
            }
        }

    }

    public MyMap(MyMap mm) {
        int[] m = mm.map;

        for(int i = 0; i < 225; ++i) {
            this.map[i] = m[i];
        }

    }

    public boolean isSpace(int x, int y) {
        if (x < 15 && y < 15 && x >= 0 && y >= 0) {
            return this.map[y * 15 + x] == 0 || this.map[y * 15 + x] == 4;
        } else {
            return false;
        }
    }

    public int amountSpacesAround(int x, int y) {
        int count = 0;
        if (this.isSpace(x - 1, y)) {
            ++count;
        }

        if (this.isSpace(x + 1, y)) {
            ++count;
        }

        if (this.isSpace(x, y - 1)) {
            ++count;
        }

        if (this.isSpace(x, y + 1)) {
            ++count;
        }

        return count;
    }

    public boolean isSpaceAndNotTemp(int x, int y) {
        if (x < 15 && y < 15 && x >= 0 && y >= 0) {
            return this.map[y * 15 + x] == 0;
        } else {
            return false;
        }
    }

    public void setTemp(int x, int y) {
        this.map[y * 15 + x] = 4;
    }

    public void setRed(int x, int y) {
        this.map[y * 15 + x] = 2;
    }

    public void setGreen(int x, int y) {
        this.map[y * 15 + x] = 1;
    }

    public void setMap(int x, int y) {
        this.map[y * 15 + x] = 10;
    }

    public boolean isReachable(int x, int y) {
        if (x < 15 && y < 15 && x >= 0 && y >= 0) {
            return this.map[y * 15 + x] == 10;
        } else {
            return false;
        }
    }

    public void setSpace(int x, int y) {
        this.map[y * 15 + x] = 0;
    }
}

