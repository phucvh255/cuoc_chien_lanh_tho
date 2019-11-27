package com.company;

import map.Map;
import java.awt.event.MouseEvent;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.GridLayout;
import javax.swing.border.Border;
import java.awt.Cursor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.MouseListener;
import java.util.Random;
import javax.swing.JPanel;

public class Panel extends JPanel implements MouseListener
{
//    private int [][] button = new int[15][15];
    Button[][] button;
    JButton butok;
    Frame jf;
    Font font;

    public Panel(final Frame jf) {
        this.button = new Button[15][15];
        this.butok = new JButton("OK");
        this.font = new Font("MV Boli", 1, 14);
        this.jf = jf;
        this.butok.setBackground(Color.decode("#f1f1f1"));
        this.butok.setFont(this.font);
        this.butok.setIcon(new ImageIcon("G:/Project/java/CCLT/src/lib/ok.PNG"));
        this.butok.setText(null);
        this.butok.setCursor(new Cursor(12));
        this.butok.setBorder(null);
        this.addMouseListener(this);
        this.setFocusable(true);
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                this.button[i][j] = new Button();
            }
        }
        this.setLayout(new GridLayout(15, 15));
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                if (i == 7 && j == 7) {
                    this.add(this.butok);
                    this.butok.addMouseListener(this);
                }
            }
        }
        Random rd = new Random();
        int amountOfWall = 0;
        while (amountOfWall < 5){
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
            button[m][n].setClick();
            button[14 - m][14 - n].setClick();
            amountOfWall++;
        }
    }
//
//    @Override
//    public void mouseClicked(final MouseEvent arg0) {
//    }
//
//    @Override
//    public void mouseEntered(final MouseEvent arg0) {
//    }
//
//    @Override
//    public void mouseExited(final MouseEvent arg0) {
//    }
//
    @Override
    public void mousePressed(final MouseEvent arg0) {
        if (arg0.getSource() == this.butok) {
            final Map map = new Map(this.getMap());
            this.jf.play(map);
        }
    }
//
//    @Override
//    public void mouseReleased(final MouseEvent arg0) {
//        for (int i = 0; i < 15; ++i) {
//            for (int j = 0; j < 15; ++j) {
//                if (arg0.getSource() == this.buttons[i][j]) {
//                    this.buttons[i][j].click();
//                    this.buttons[14 - i][14 - j].setClick();
//                }
//            }
//        }
//    }

    public Map getMap() {
        final int[][] map = new int[15][15];
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                if (this.button[i][j].isSelected()) {
                    map[i][j] = 3;
                }
                else {
                    map[i][j] = 0;
                }
            }
        }
        return new Map(map);
    }

    public void check() {
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                if (this.button[i][j].isSelected() && !(this.button[14 - i][14 - j].isSelected())) {
                    this.button[i][j].reset();
                    this.button[14 - i][14 - j].reset();
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }


    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
