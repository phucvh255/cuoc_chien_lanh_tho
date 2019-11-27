package com.company;

import ai.Ai;
import ai.Ai3;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import ai.Ai4;
import map.Map;
import map.MapS;
import player.GreenPlayer;
import player.Player;
import player.RedPlayer;

public class Board extends JPanel implements KeyListener, ActionListener {
    public static final int HEIGHT = 800;
    public static final int WIDTH = 780;
    private final int offsetx = 20;
    private final int offsety = 20;
    Map map;
    RedPlayer red;
    GreenPlayer green;
    Timer timer;
    int x = 0;
    int y = 0;
    Ai3 ai_red = new Ai3();
    Ai ai_green = new Ai();
    boolean finished = false;

    public Board() throws FileNotFoundException {
        this.setFocusable(true);
        this.addKeyListener(this);
        this.map = new Map("/lib/map.txt");
        this.red = new RedPlayer(14, 14, this.map);
        this.green = new GreenPlayer(0, 0, this.map);
        this.map.setGreen(0, 0);
        this.map.setRed(14, 14);
        this.timer = new Timer(27, this);
        this.timer.start();
        if ((new Random()).nextInt(2) == 1) {
            this.setFirstTurn(this.red);
        } else {
            this.setFirstTurn(this.green);
        }

    }

    public Board(Map m) {
        this.setFocusable(true);
        this.addKeyListener(this);
        this.map = m;
        this.red = new RedPlayer(14, 14, this.map);
        this.green = new GreenPlayer(0, 0, this.map);
        this.map.setGreen(0, 0);
        this.map.setRed(14, 14);
        this.timer = new Timer(27, this);
        this.timer.start();
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

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(this.map.getImageMap(), 0, 0, this);

        for(int x = 0; x < 15; ++x) {
            for(int y = 0; y < 15; ++y) {
                if (!this.map.isSpace(x, y)) {
                    g.drawImage(this.map.getImage(x, y), x * 50, y * 50, this);
                }
            }
        }

        g.drawImage(this.red.getImage(), this.red.xp, this.red.yp, this);
        g.drawImage(this.green.getImage(), this.green.xp, this.green.yp, this);
        if (this.finished) {
            if (this.red.goable(this.map)) {
                g.drawImage((new ImageIcon("G:/Project/java/CCLT/src/lib/rw.png")).getImage(), 0, 0, this);
            } else {
                g.drawImage((new ImageIcon("G:/Project/java/CCLT/src/lib/gw.png")).getImage(), 0, 0, this);
            }
        }

        this.repaint();
    }

    public void keyTyped(KeyEvent ke) {
    }

    public void keyPressed(KeyEvent ke) {
    }

    public void keyReleased(KeyEvent ke) {
    }

    public void actionPerformed(ActionEvent ae) {
        if (!this.finished) {
            if (this.green.getTurn()) {
                Long T = System.nanoTime();
                this.green.move(this.ai_green.findDirection(new MapS(this.map), this.green.getX(), this.green.getY(), this.red.getX(), this.red.getY()));
                this.map.setGreen(this.green.getX(), this.green.getY());
                if (!this.green.goable(this.map)) {
                    System.out.println("Do Thang");
                    this.finished = true;
                } else {
                    this.changeTurn();
                }
            } else if (this.red.getTurn()) {
                Long T = System.nanoTime();
                this.red.move(this.ai_red.findDirection(new MapS(this.map), this.red.getX(), this.red.getY(), this.green.getX(), this.green.getY()));
                this.map.setRed(this.red.getX(), this.red.getY());
                if (!this.red.goable(this.map)) {
                    System.out.println("Xanh Thang");
                    this.finished = true;
                } else {
                    this.changeTurn();
                }
            }
        }
    }
}
