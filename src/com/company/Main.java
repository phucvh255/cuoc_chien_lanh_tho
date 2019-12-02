package com.company;

import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public Main() {
    }

    public static void main(String[] args) throws IOException {
        int mode = 1;
        if(mode == 1) {
            Frame jf = new Frame("AI");
            jf.add(new PN(jf));
            jf.setVisible(true);
            jf.setSize(780, 800);
            jf.setResizable(false);
            jf.setLocationRelativeTo((Component)null);
            jf.setAlwaysOnTop(false);
            jf.setDefaultCloseOperation(3);
        } else if(mode == 2) {
            int count = 0;
            int redWin = 0;
            int[] parameter = new int[2];
            parameter[0] = 31;
            parameter[1] = 0;
            int max = 0;
            int[][] result = new int[1000][3];
            int index = 0;
            while (parameter[0] > 15) {
                index++;
                count = 0;
                redWin = 0;
                while (count < 10) {
                    NewMap map = new NewMap();
                    Parameter game = new Parameter(map.getMap(), parameter);
                    redWin += game.play();
                    count++;
                    System.out.println(redWin + " - " + (count - redWin));
                }
                result[index][0] = parameter[0];
                result[index][1] = parameter[1];
                result[index][2] = redWin;
                parameter[1] = parameter[1] % 21;
                parameter[0] -= parameter[1] / 21;
                System.out.println(redWin);
            }
        }
        else {
            int count = 0;
            int redWin = 0;
            int[] parameter = new int[2];
            parameter[0] = 31;
            parameter[1] = 11;
            int max = 0;
            while (count < 10) {
                NewMap map = new NewMap();
                Parameter game = new Parameter(map.getMap(), parameter);
                redWin += game.play();
                count++;
                System.out.println(redWin + " - " + (count - redWin));
            }
        }
    }
}
