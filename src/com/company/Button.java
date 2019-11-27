package com.company;


import java.awt.Color;
import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;

class Button extends JToggleButton {
    String label;
    public static final String SELECTED = "WALL";
    public static final String UNSELECT = "";
    public final ImageIcon WALLIMAGE = new ImageIcon("G:/Project/java/CCLT/src/lib/3.png");

    public Button() {
        this.setMaximumSize(new Dimension(50, 50));
        this.setMinimumSize(new Dimension(50, 50));
        this.setBackground(Color.decode("#1695a3"));
        this.setBorder(new LineBorder(Color.white));
    }

    public void click() {
        if (this.isSelected()) {
            this.setIcon(this.WALLIMAGE);
        } else {
            this.setIcon((Icon)null);
        }

    }

    public void reset() {
        this.setSelected(false);
        this.setIcon((Icon)null);
    }

    public void setClick() {
        if (this.isSelected()) {
            this.setSelected(false);
            this.setIcon((Icon)null);
        } else {
            this.setSelected(true);
            this.setIcon(this.WALLIMAGE);
        }

    }
}

