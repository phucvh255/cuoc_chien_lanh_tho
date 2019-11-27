package com.company;

import map.Map;
import java.util.Random;

public class NewMap {
    private int[][] button;

    public NewMap() {
        this.button = new int[15][15];
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                this.button[i][j] = 0;
            }
        }
        Random rd = new Random();
        for (int i = 0; i < 5; i++) {
            int m = -1;
            int n = -1;
            int count = 0;
            while (count < 5) {
                while (m < 0 || n < 0 || (m == 14 && n == 14) || (m == 0 && n == 0)) {
                    m = rd.nextInt(14);
                    n = rd.nextInt(14);
                }
                count++;
            }
            button[m][n] = 1;
            button[14 - m][14 - n] = 1;
        }
    }

    public Map getMap() {
        final int[][] map = new int[15][15];
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                if (this.button[i][j] == 1) {
                    map[i][j] = 3;
                } else {
                    map[i][j] = 0;
                }
            }
        }
        return new Map(map);
    }

    public void check() {
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                if (this.button[i][j] == 1 && !(this.button[14 - i][14 - j] == 1)) {
                    this.button[i][j] = 0;
                    this.button[14 - i][14 - j] = 0;
                }
            }
        }
    }
}

