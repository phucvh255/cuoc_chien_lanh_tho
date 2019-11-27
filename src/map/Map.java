package map;

import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import javax.swing.ImageIcon;

public class Map {
    public static final int M = 15;
    public static final int N = 15;
    public static final int W = 60;
    public static final int H = 60;
    public static final int SPACE = 0;
    public static final int WALL = 3;
    public static final int GREEN = 1;
    public static final int RED = 2;
    public static final int TEMP = 4;
    int[][] map;
    private int[][] prices;
    private Image imagemap;
    private Image[] image = new Image[4];
    public static long dem = 0L;

    public Map(String s) throws FileNotFoundException {
        this.map = new int[15][15];
        this.prices = new int[15][15];
        FileReader fr = new FileReader(this.getClass().getResource(s).getFile());
        Scanner scan = new Scanner(fr);

        for(int i = 0; i < 15; ++i) {
            for(int j = 0; j < 15; ++j) {
                this.map[i][j] = scan.nextInt();
            }
        }

        this.setPrices();
        this.imagemap = (new ImageIcon("G:/Project/java/CCLT/src/lib/map2.jpg")).getImage();
        this.image[3] = (new ImageIcon("G:/Project/java/CCLT/src/lib/w.png")).getImage();
        this.image[2] = (new ImageIcon("G:/Project/java/CCLT/src/lib/d.png")).getImage();
        this.image[1] = (new ImageIcon("G:/Project/java/CCLT/src/lib/x.png")).getImage();
    }

    public Map(int[][] m) {
        this.map = m;
        this.prices = new int[15][15];
        this.setPrices();
        this.imagemap = (new ImageIcon("G:/Project/java/CCLT/src/lib/map2.jpg")).getImage();
        this.image[3] = (new ImageIcon("G:/Project/java/CCLT/src/lib/w.png")).getImage();
        this.image[2] = (new ImageIcon("G:/Project/java/CCLT/src/lib/d.png")).getImage();
        this.image[1] = (new ImageIcon("G:/Project/java/CCLT/src/lib/x.png")).getImage();
    }

    public Map(Map ma) {
        this.map = new int[15][15];

        for(int i = 0; i < 15; ++i) {
            for(int j = 0; j < 15; ++j) {
                this.map[i][j] = ma.map[i][j];
            }
        }

        this.prices = new int[15][15];
        this.imagemap = ma.imagemap;
        this.image = ma.image;
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

    public int[][] getPrices() {
        return this.prices;
    }

    private void setPrices() {
        for(int i = 0; i < 15; ++i) {
            for(int j = 0; j < 15; ++j) {
                this.prices[i][j] = 3;
            }
        }

    }

    public Image getImageMap() {
        return this.imagemap;
    }

    public Image getImage(int x, int y) {
        if (x < 15 && y < 15) {
            int i = this.map[y][x];
            return i > 0 && i < 4 ? this.image[i] : null;
        } else {
            return null;
        }
    }

    public void printMap() {
        for(int i = 0; i < 15; ++i) {
            for(int j = 0; j < 15; ++j) {
                System.out.print(this.map[i][j]);
            }

            System.out.println();
        }

        System.out.println("***********");
    }

    public void printPrices() {
        for(int i = 0; i < 15; ++i) {
            for(int j = 0; j < 15; ++j) {
                System.out.print(this.prices[i][j]);
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

    public int getPrice(int x, int y) {
        return this.prices[y][x];
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

