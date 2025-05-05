import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MenuPanel extends JPanel implements Runnable, SwitchablePanel{
    
    static final int GAME_WIDTH = 400;
    static final int GAME_HEIGHT = 550;
    static final int BIRD_DIAMETER = 35;
    static final int SPIKE_HEIGHT = 25;
    static final int ANIMATION_MOVEMENT = 20;
    static final int NUM_SPIKES = 8;
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    ArrayList<Spike> horizontal_spikes;
    Bird bird;
    Score score;
    Thread menuThread;
    boolean isFading;
    private volatile boolean running;
    private volatile boolean paused;
    PanelSwitcher panelSwitcher;

    MenuPanel(PanelSwitcher panelSwitcher) {
        this.panelSwitcher = panelSwitcher;
        newBird();
        score = new Score();
        this.setFocusable(true);
        this.setPreferredSize(SCREEN_SIZE);
        this.setBackground(new Color(135, 206, 235));
        this.isFading = false;
        this.addKeyListener(new AL()); // Aici daca KEY_UP sa inceapa GamePanel
        this.horizontal_spikes = new ArrayList<>();
        newHorizontalSpikes();
        
    }

    public void start() {
        setIsFading(false);
        score.updateBestScore();
        score.resetFadeBestScore();
        running = true;
        paused = false;
        menuThread = new Thread(this);
        menuThread.start();
    }

    public void pause() {
        paused = true;
    }

    public void stop() {
        running = false;
    }

    public void newBird() {
        bird = new Bird(180, 200, BIRD_DIAMETER, BIRD_DIAMETER);
    }

    public void newHorizontalSpikes() {
        int spikeWidth = GAME_WIDTH / NUM_SPIKES;

        for (int i = 0; i < NUM_SPIKES; i++) {
            int x = i * spikeWidth;
            horizontal_spikes.add(new Spike(x, 0, spikeWidth, SPIKE_HEIGHT, "DOWN"));
            horizontal_spikes.add(new Spike(x, GAME_HEIGHT - SPIKE_HEIGHT, spikeWidth, SPIKE_HEIGHT, "UP"));
        }
    }

    public void updateAnimation() {
        bird.menuAnimation(200, ANIMATION_MOVEMENT);
    }

    public void paint(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        for (Spike spike : horizontal_spikes) {
            spike.draw(g);
        }
        bird.draw(g);
        score.drawBestScore(g);
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000/ amountOfTicks;
        double delta = 0;
        score.resetFadeBestScore();

        while (running) {
                while (paused) {
                    running = false;
                    paused = false;
                }
            
            long now = System.nanoTime();
            delta += (now - lastTime)/ns;
            lastTime = now;
            if (delta >= 1) {
                if (getIsFading()) {
                    score.fadeOutBestScore();
                    if (score.hasFaded()) {
                        System.out.println(":DONE");
                        panelSwitcher.switchToPanel("GamePanel");
                        pause();
                    }
                }
                updateAnimation();
                repaint();
                delta--;
            }
        }
    }

    /* 
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000/ amountOfTicks;
        double delta = 0;
        score.resetFadeBestScore();
        while (!menuThread.isInterrupted()) {
            long now = System.nanoTime();
            delta += (now - lastTime)/ns;
            lastTime = now;
            if (delta >= 1) {
                if (getIsFading()) {
                    score.fadeOutBestScore();
                    if (score.hasFaded()) {
                        System.out.println(":DONE");
                        panelSwitcher.switchToPanel("GamePanel");
                    }
                }
                updateAnimation();
                repaint();
                delta--;
            }
        }
    }
    */

    public boolean isMenuInterrupted() {
        return menuThread.isInterrupted();
    }

    public boolean getIsFading() {
        return this.isFading;
    }

    public void setIsFading(boolean value) {
        this.isFading = value;
    }

    public class AL extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                setIsFading(true);
                //panelSwitcher.switchToPanel("GamePanel");
            }
            // STOP THREAD MenuPanel
            // startThread GamePanel
            // add GamePanel to gameFrame, and close MenuPanel
        }
    }
}
