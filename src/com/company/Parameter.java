package com.company;

import ai.Ai;
import ai.Ai2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import map.Map;
import map.MapS;
import player.GreenPlayer;
import player.Player;
import player.RedPlayer;

public class Parameter {
    public static final int HEIGHT = 800;
    public static final int WIDTH = 780;
    private final int offsetx = 20;
    private final int offsety = 20;
    private Map map;
    private RedPlayer red;
    private GreenPlayer green;
    int x = 0;
    int y = 0;
    private Ai2 ai_red;
    private Ai ai_green;
    private boolean finished = false;
    private int redCount = 0;
    private int count;

    public Parameter() throws FileNotFoundException {
        this.map = new Map("/lib/map.txt");
        this.red = new RedPlayer(14, 14, this.map);
        this.green = new GreenPlayer(0, 0, this.map);
        this.map.setGreen(0, 0);
        this.map.setRed(14, 14);
        if ((new Random()).nextInt(2) == 1) {
            this.setFirstTurn(this.red);
        } else {
            this.setFirstTurn(this.green);
        }

    }

    public Parameter(Map m, int [] para, int count) throws IOException {
        this.ai_green = new Ai();
        this.ai_red = new Ai2();
        this.map = m;
        this.red = new RedPlayer(14, 14, this.map);
        this.green = new GreenPlayer(0, 0, this.map);
        this.map.setGreen(0, 0);
        this.map.setRed(14, 14);
        this.count = count;
        if ((new Random()).nextInt(2) == 1) {
            this.setFirstTurn(this.green);
        } else {
            this.setFirstTurn(this.red);
        }

    }

    private void setFirstTurn(Player p) {
        p.setTurn(true);
    }

    private void changeTurn() {
        if (this.green.getTurn()) {
            this.green.setTurn(false);
            this.red.setTurn(true);
        } else {
            this.green.setTurn(true);
            this.red.setTurn(false);
        }

    }


    public int play() throws IOException {
        while (!this.finished) {
            if (this.green.getTurn()) {
                Long T = System.nanoTime();
                this.green.move(this.ai_green.findDirection(new Map(this.map), this.green.getX(), this.green.getY(), this.red.getX(), this.red.getY()));
                this.map.setGreen(this.green.getX(), this.green.getY());
                if (!this.green.goable(this.map)) {
                    this.redCount++;
                    this.finished = true;
                    this.ai_red.model.save_model();
                    this.ai_red.model.backup_model(count);
                } else {
                    this.changeTurn();
                }
            } else if (this.red.getTurn()) {
                Long T = System.nanoTime();
                this.red.move(this.ai_red.findDirection(new Map(this.map), this.red.getX(), this.red.getY(), this.green.getX(), this.green.getY()));
                this.map.setRed(this.red.getX(), this.red.getY());
                if (!this.red.goable(this.map)) {
                    this.finished = true;
                    this.ai_red.model.save_model();
                    this.ai_red.model.backup_model(count);
                } else {
                    this.changeTurn();
                }
            }
        }
        return redCount;
    }
}
