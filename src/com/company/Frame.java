package com.company;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import map.Map;

public class Frame extends JFrame implements ActionListener {
    Board board;
    Panel panel = new Panel(this);
    PN pn = new PN(this);
    About ab = new About(this);
    CardLayout card = new CardLayout();
    JPanel mainpn = (JPanel)this.getContentPane();

    public Frame(String s) {
        super(s);
        this.mainpn.setLayout(this.card);
        this.mainpn.add("pn", this.pn);
        this.mainpn.add("map", this.panel);
        this.mainpn.add("about", this.ab);
    }

    public void control() {
        this.card.show(this.mainpn, "pn");
    }

    public void map() {
        this.card.show(this.mainpn, "map");
    }

    public void play() throws FileNotFoundException {
        this.board = new Board();
        this.mainpn.add("play", this.board);
        this.addKeyListener(this.board);
        this.setFocusable(true);
        this.card.show(this.mainpn, "play");
    }

    public void play(Map map) {
        this.board = new Board(map);
        this.mainpn.add("play1", this.board);
        this.addKeyListener(this.board);
        this.setFocusable(true);
        this.card.show(this.mainpn, "play1");
    }

    public void about() {
        this.card.show(this.mainpn, "about");
    }

    public void actionPerformed(ActionEvent arg0) {
    }
}

