package map;

public class MapS {
    public static final int SPACE = 0;
    public static final int WALL = 3;
    public static final int GREEN = 1;
    public static final int RED = 2;
    public static final int TEMP = 4;
    public static final int N = 15;
    public int[] map = new int[225];
    public static final int BLOCK_OUT_OF_BOARD = -1;
    public static final int BLOCK_EMPTY = 0;
    public static final int BLOCK_PLAYER_1 = 1;
    public static final int BLOCK_PLAYER_1_TRAIL = 2;
    public static final int BLOCK_PLAYER_2 = 3;
    public static final int BLOCK_PLAYER_2_TRAIL = 4;
    public static final int BLOCK_OBSTACLE = 5;

    public MapS(int[] mm) {
        for(int i = 0; i < 225; ++i) {
            if (mm[i] == 0) {
                this.map[i] = 0;
            } else if (mm[i] != 3 && mm[i] != 1) {
                if (mm[i] != 2 && mm[i] != 4) {
                    if (mm[i] == 5) {
                        this.map[i] = 3;
                    }
                } else {
                    this.map[i] = 2;
                }
            } else {
                this.map[i] = 1;
            }
        }

    }

    public MapS(Map mm) {
        int[][] m = mm.map;

        for(int i = 0; i < 15; ++i) {
            for(int j = 0; j < 15; ++j) {
                this.map[i * 15 + j] = m[i][j];
            }
        }

    }

    public MapS(MapS mm) {
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
