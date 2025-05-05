import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Spike extends Rectangle {
    
    static final String UP = "UP";
    static final String DOWN = "DOWN";
    static final String LEFT = "LEFT";
    static final String RIGHT = "RIGHT";
    String orientation;

    public Spike(int x, int y, int width, int height, String orientation) {
        super(x, y, width, height);
        this.orientation = orientation;
    }

    public String getOrientation() {
        return this.orientation;
    }

    public void draw(Graphics g) {
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];

        switch (orientation) {
            case UP:
                xPoints = new int[]{x, x + width, x + width / 2}; 
                yPoints = new int[]{y + height, y + height, y};  
                break;
            case DOWN:
                xPoints = new int[]{x, x + width, x + width / 2};  
                yPoints = new int[]{y, y, y + height}; 
                break;
            case LEFT:
                xPoints = new int[]{x, x + width, x + width};
                yPoints = new int[]{y + height / 2, y, y + height};
                break;
            case RIGHT:
                xPoints = new int[]{x, x + width, x};  
                yPoints = new int[]{y, y + height / 2, y + height};  
                break;
        }

        g.setColor(Color.GRAY);
        g.fillPolygon(xPoints, yPoints, 3);

        // border box for debug
        //g.setColor(Color.RED);
        //g.drawRect(x, y, width, height);
    }

}
