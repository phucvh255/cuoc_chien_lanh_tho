package com.company;

import java.awt.Cursor;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class PN extends JPanel implements MouseListener {
    private JButton play;
    private JButton map;
    private JButton about;
    private JButton exit;
    private JLabel background;
    private Frame jf;

    public PN(Frame jf) {
        this.initComponents();
        this.jf = jf;
        this.play.addMouseListener(this);
        this.map.addMouseListener(this);
        this.about.addMouseListener(this);
        this.exit.addMouseListener(this);
    }

    private void initComponents() {
        this.play = new JButton();
        this.map = new JButton();
        this.about = new JButton();
        this.exit = new JButton();
        this.background = new JLabel();
        this.setLayout((LayoutManager)null);
        this.play.setBorder((Border)null);
        this.map.setBorder((Border)null);
        this.about.setBorder((Border)null);
        this.exit.setBorder((Border)null);
        Cursor cur = new Cursor(12);
        this.play.setCursor(cur);
        this.map.setCursor(cur);
        this.about.setCursor(cur);
        this.exit.setCursor(cur);
        this.play.setIcon(new ImageIcon("G:/Project/java/CCLT/src/lib/play.png"));
        this.map.setIcon(new ImageIcon("G:/Project/java/CCLT/src/lib/createmap.png"));
        this.about.setIcon(new ImageIcon("G:/Project/java/CCLT/src/lib/about.png"));
        this.exit.setIcon(new ImageIcon("G:/Project/java/CCLT/src/lib/exit.png"));
        int n = 50;
        this.play.setBounds(450, 230 + n, 202, 44);
        this.map.setBounds(450, 300 + n, 202, 44);
        this.about.setBounds(450, 370 + n, 202, 44);
        this.exit.setBounds(450, 440 + n, 202, 44);
        this.add(this.play);
        this.add(this.map);
        this.add(this.about);
        this.add(this.exit);
        this.background.setIcon(new ImageIcon("G:/Project/java/CCLT/src/lib/bg4.png"));
        this.add(this.background);
        this.background.setBounds(0, -30, 750, 750);
    }

    public void mouseClicked(MouseEvent arg0) {
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void mousePressed(MouseEvent arg0) {
    }

    public void mouseReleased(MouseEvent arg0) {
        if (arg0.getSource() == this.play) {
            this.jf.map();
        }

        if (arg0.getSource() == this.map) {
            this.jf.map();
        }

        if (arg0.getSource() == this.about) {
            this.jf.about();
        }

        if (arg0.getSource() == this.exit) {
            System.exit(0);
        }

    }
}

