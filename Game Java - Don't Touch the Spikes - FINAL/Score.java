import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Score extends Rectangle {

    private BufferedReader br;
    private PrintWriter pw;
    private String l;
    private float alpha = 1.0f; //pt fade out la text
    int currentScore;
    int bestScore;

    Score() {
        this.currentScore = 0;
        this.bestScore = loadBestScore();
    }

    private int loadBestScore() {
        File f = new File("score.txt");
        String[] s;
        if (f.exists()) {
            try {
                br = new BufferedReader(new FileReader(f));
                while((l = br.readLine()) != null) {
                    s = l.split("=");
                    return Integer.parseInt(s[1]);
                }
                br.close();
            } catch(IOException ioe) {ioe.printStackTrace();}
        }
        else {
            System.out.println("File doesn't exist");
        }
        return 0;
    }

    public void setBestScore(int newBestScore) {
        this.bestScore = newBestScore;
    }

    public void increaseScore() {
        this.currentScore++;
    }

    public void resetScore() {
        this.currentScore = 0;
    }

    public int getCurrentScore() {
        return this.currentScore;
    }

    public int getBestScore() {
        return this.bestScore;
    }

    public void updateBestScore() {
        if (getCurrentScore() > getBestScore()) {
            this.bestScore = getCurrentScore();
        }
    }

    public String bestScoreToString() {
        return "bestscore=" + getBestScore();
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Calibri", Font.BOLD, 60));
        String scoreText = String.valueOf(getCurrentScore());
        int textWidth = g.getFontMetrics().stringWidth(scoreText);

        g.drawString(scoreText, (GamePanel.getGAME_WIDTH() - textWidth) / 2 , GamePanel.getGAME_HEIGHT() / 2);
    }

    public void drawBestScore(Graphics g) {
        setBestScore(loadBestScore());
        int alphaValue = (int) Math.max(0, Math.min(255, alpha * 255));
        g.setColor(new Color(255, 255, 255, alphaValue));
        g.setFont(new Font("Calibri", Font.PLAIN ,30));

        String bestScoreText = "BEST SCORE: " + String.valueOf(getBestScore());
        int textWidth = g.getFontMetrics().stringWidth(bestScoreText);
        g.drawString(bestScoreText, (GamePanel.getGAME_WIDTH() - textWidth) / 2 , GamePanel.getGAME_HEIGHT() / 2 + 60);
        
        String pressKeyText = "PRESS UP KEY TO START";
        textWidth = g.getFontMetrics().stringWidth(pressKeyText);
        g.drawString(pressKeyText, (GamePanel.getGAME_WIDTH() - textWidth) / 2 , GamePanel.getGAME_HEIGHT() / 2 - 100);
    }

    public void fadeOutBestScore() {
        if (alpha > 0.0f) {
            alpha -= 0.02f;
        }
    }

    public boolean hasFaded() {
        if (alpha <= 0.0f) {
            return true;
        }
        return false;
    }

    public void resetFadeBestScore() {
        alpha = 1.0f;
    }

    public void saveBestScore() {
        updateBestScore();
        try {
            pw = new PrintWriter(new FileWriter("score.txt"));
            pw.println(bestScoreToString());
            pw.close();
        } catch(IOException e) {e.printStackTrace();}  
    }

}
