package map;

public class Mapn {
    public static final int M = 15;
    public static final int N = 15;
    public static final int SPACE = 0;
    public static final int WALL = 3;
    public static final int GREEN = 1;
    public static final int RED = 2;
    public static final int TEMP = 4;
    private int[][] map = new int[15][15];
    public static long dem = 0L;

    public Mapn(Map ma) {
        for(int i = 0; i < 15; ++i) {
            for(int j = 0; j < 15; ++j) {
                this.map[i][j] = ma.map[i][j];
            }
        }

        ++dem;
    }

    public Mapn(Mapn ma) {
        for(int i = 0; i < 15; ++i) {
            for(int j = 0; j < 15; ++j) {
                this.map[i][j] = ma.map[i][j];
            }
        }

        ++dem;
    }

    public boolean hasSpaceAround(int x, int y) {
        return this.isSpace(x - 1, y) || this.isSpace(x + 1, y) || this.isSpace(x, y - 1) || this.isSpace(x, y + 1);
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

    public int amountSpacesNotTempAround(int x, int y) {
        int count = 0;
        if (this.isSpaceAndNotTemp(x - 1, y)) {
            ++count;
        }

        if (this.isSpaceAndNotTemp(x + 1, y)) {
            ++count;
        }

        if (this.isSpaceAndNotTemp(x, y - 1)) {
            ++count;
        }

        if (this.isSpaceAndNotTemp(x, y + 1)) {
            ++count;
        }

        return count;
    }

    public void printMap() {
        for(int i = 0; i < 15; ++i) {
            for(int j = 0; j < 15; ++j) {
                System.out.print(this.map[i][j]);
            }

            System.out.println();
        }

    }

    public boolean isSpace(int x, int y) {
        if (x < 15 && y < 15 && x >= 0 && y >= 0) {
            return this.map[y][x] == 0 || this.map[y][x] == 4;
        } else {
            return false;
        }
    }

    public boolean isSpaceAndNotTemp(int x, int y) {
        if (x < 15 && y < 15 && x >= 0 && y >= 0) {
            return this.map[y][x] == 0;
        } else {
            return false;
        }
    }

    public void setTemp(int x, int y) {
        this.map[y][x] = 4;
    }

    public boolean isWall(int x, int y) {
        if (x < 15 && y < 15 && x >= 0 && y >= 0) {
            return this.map[y][x] == 3;
        } else {
            return true;
        }
    }

    public void setRed(int x, int y) {
        this.map[y][x] = 2;
    }

    public void setGreen(int x, int y) {
        this.map[y][x] = 1;
    }

    public void setMap(int x, int y) {
        this.map[y][x] = 10;
    }

    public boolean isReachable(int x, int y) {
        if (x < 15 && y < 15 && x >= 0 && y >= 0) {
            return this.map[y][x] == 10;
        } else {
            return false;
        }
    }

    public void setSpace(int x, int y) {
        this.map[y][x] = 0;
    }
}

