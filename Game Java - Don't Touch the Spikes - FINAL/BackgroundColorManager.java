import java.awt.Color;

public class BackgroundColorManager {
    
    private Color[] colorOptions;
    
    public BackgroundColorManager() {
        colorOptions = new Color[] {
            new Color(135, 206, 235),
            new Color(246, 206, 138),  
            new Color(2, 48, 32),     
            new Color(239, 43, 124),   
            new Color(0, 0, 54),       
            new Color(225, 165, 0),   
            new Color(199, 202, 201), 
            new Color(235, 235, 235),  
            new Color(230, 210, 181),  
            new Color(121, 36, 36)
        };
    }

    public Color getBackgroundColor(int score) {
        int color_idx = score / 10;
        color_idx = Math.min(color_idx, colorOptions.length - 1);
        return colorOptions[color_idx];
    }
}