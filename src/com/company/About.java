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

public class About extends JPanel implements MouseListener {
    private JLabel background;
    private Frame jf;
    private JButton back;

    public About(Frame jf) {
        this.jf = jf;
        this.setLayout((LayoutManager)null);
        this.background = new JLabel();
        this.back = new JButton("");
        this.back.setIcon(new ImageIcon("G:/Project/java/CCLT/src/lib/back.png"));
        this.add(this.back);
        this.background.setIcon(new ImageIcon("G:/Project/java/CCLT/src/lib/bg5.png"));
        this.add(this.background);
        this.back.setBounds(50, 600, 202, 44);
        this.back.setBorder((Border)null);
        this.back.setCursor(new Cursor(12));
        this.background.setBounds(0, -30, 750, 750);
        this.back.addMouseListener(this);
        this.setFocusable(true);
    }

    public void mouseClicked(MouseEvent arg0) {
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void mousePressed(MouseEvent arg0) {
        if (arg0.getSource() == this.back) {
            this.jf.control();
        }

    }

    public void mouseReleased(MouseEvent arg0) {
    }
}

