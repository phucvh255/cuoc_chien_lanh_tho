package player;

import javax.swing.ImageIcon;
import map.Map;

public class GreenPlayer extends Player {
    public GreenPlayer(int x, int y, Map map) {
        super(x, y, map);
        this.arrImage[0] = (new ImageIcon("G:/Project/java/CCLT/src/lib/xex_4.png")).getImage();
        this.arrImage[1] = (new ImageIcon("G:/Project/java/CCLT/src/lib/xex_2.png")).getImage();
        this.arrImage[2] = (new ImageIcon("G:/Project/java/CCLT/src/lib/xex.png")).getImage();
        this.arrImage[3] = (new ImageIcon("G:/Project/java/CCLT/src/lib/xex_3.png")).getImage();
        this.image = this.arrImage[0];
    }
}
