import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, SwitchablePanel{
    
    static final int GAME_WIDTH = 400;
    static final int GAME_HEIGHT = 550;
    static final int BIRD_DIAMETER = 30;
    static final int SPIKE_HEIGHT = 25;
    static final int WALL_SPIKE_WIDTH = 25;
    static final int NUM_SPIKES = 8;
    static final int GAP_HEIGHT = BIRD_DIAMETER;
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    Bird bird;
    ArrayList<Spike> horizontal_spikes;
    ArrayList<Spike> wall_spikes;
    Thread gameThread;
    Random rand;
    Score score;
    BackgroundColorManager backgroundColorManager;
    PanelSwitcher panelSwitcher;
    boolean running;
    boolean paused;
    boolean isCollision;

    GamePanel(PanelSwitcher panelSwitcher) {
        this.panelSwitcher = panelSwitcher;
        newBird();
        backgroundColorManager = new BackgroundColorManager();
        score = new Score();
        this.setPreferredSize(SCREEN_SIZE);
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.setBackground(backgroundColorManager.getBackgroundColor(score.getCurrentScore()));
        this.addKeyListener(new AL());
        this.horizontal_spikes = new ArrayList<>();
        this.wall_spikes = new ArrayList<>();
        System.out.println("gamgsadf");
        newHorizontalSpikes();
        generateWallSpikes(bird.getXDirection());
    }

    public void start() {
        running = true;
        paused = false;
        gameThread = new Thread(this);
        gameThread.start();
        this.setFocusable(true);
        this.requestFocusInWindow();
        isCollision = false;
        resetGame();
    }

    public void pause() {
        paused = true;
        resetGame();
    }

    public void stop() {
        running = false;
    }

    public void newHorizontalSpikes() {
        int spikeWidth = GAME_WIDTH / NUM_SPIKES;

        for (int i = 0; i < NUM_SPIKES; i++) {
            int x = i * spikeWidth;
            horizontal_spikes.add(new Spike(x, 0, spikeWidth, SPIKE_HEIGHT, "DOWN"));
            horizontal_spikes.add(new Spike(x, GAME_HEIGHT - SPIKE_HEIGHT, spikeWidth, SPIKE_HEIGHT, "UP"));
        }
    }

    public void generateWallSpikes(int birdDirection) {
        wall_spikes.clear();
        rand = new Random();
        int spike_x;
        String orientation;
        if (birdDirection == 1) {
            spike_x = GAME_WIDTH - WALL_SPIKE_WIDTH; 
            orientation = "LEFT";
        }
        else {
            spike_x = 0;
            orientation = "RIGHT";
        }
        int spike_y;
        int num_wallSpikes;
        int currentScore = score.getCurrentScore();
        int minSpikes = Math.min(2 + currentScore / 10, 6);
        int maxSpikes = Math.min(3 + currentScore / 10, 8);
        num_wallSpikes = rand.nextInt(minSpikes, maxSpikes);

        for (int i = 1; i <= num_wallSpikes; i++) {
            
            boolean positionIsValid; // assessing the overlapping issue caused by random call
            do {
                positionIsValid = true;
                spike_y = rand.nextInt(SPIKE_HEIGHT, GAME_HEIGHT - SPIKE_HEIGHT);
                for (Spike spike : wall_spikes) {
                    if (Math.abs(spike.y - spike_y) < SPIKE_HEIGHT) {
                        positionIsValid = false;
                        break;
                    }
                }
            } while (!positionIsValid);

            wall_spikes.add(new Spike(spike_x, spike_y, WALL_SPIKE_WIDTH, SPIKE_HEIGHT, orientation));
        }
    }

    public void newBird() {
        bird = new Bird(180, 200, BIRD_DIAMETER, BIRD_DIAMETER);
    }

    public void paint(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        for (Spike spike : horizontal_spikes) {
            spike.draw(g);
        }
        for (Spike spike : wall_spikes) {
            spike.draw(g);
        }
        bird.draw(g);
        score.draw(g);
    }

    public void move() {
        bird.move();
        checkCollision();
    }

    public void checkCollision() {
        // if bird touches one of the walls it changes direction
        if (bird.x <= 0) {
            bird.setXDirection(1);
            score.increaseScore();
            generateWallSpikes(bird.getXDirection());
        }
        if (bird.x >= GAME_WIDTH - BIRD_DIAMETER) {
            bird.setXDirection(-1);
            score.increaseScore();
            generateWallSpikes(bird.getXDirection());
        }
        if (bird.y >= GAME_HEIGHT) {
            bird.y = GAME_HEIGHT;
        
            // bird dies here but need to update this with spikes later
        }
        if (bird.y <= 0) {
            bird.y = 0;
            // bird dies here but need to update this with spikes later
        }

        // CHECK collision with spike
        for (Spike spike : horizontal_spikes) {
            if (bird.getBounds().intersects(spike.getBounds()) && !isCollision) {
                System.out.println("COLLISION");
                isCollision = true;
                break;
                // aici o sa resetam jocul, game over
            }
        }

        for (Spike spike : wall_spikes) {
            if (bird.getBounds().intersects(spike.getBounds()) && !isCollision) {
                System.out.println("COLLISION");
                isCollision = true; 
                break;
                // aici o sa resetam jocul, game over
            }
        }

        if (isCollision) {
            panelSwitcher.switchToPanel("MenuPanel"); 
            generateWallSpikes(bird.getXDirection());
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000/ amountOfTicks;
        double delta = 0;
        int previousScore = 0;

        while (running) {
                while (paused) {
                    running = false;
                    paused = false;
                }
            

                long now = System.nanoTime();
                delta += (now - lastTime)/ns;
                lastTime = now;
                if (delta >= 1) {
                    if (score.getCurrentScore() % 10 == 0 && score.getCurrentScore() != previousScore && previousScore < 100) {
                        amountOfTicks = Math.min(60 + (previousScore / 10) * 5, 100);
                        ns = 1000000000/ amountOfTicks;
                        previousScore = score.getCurrentScore();
                    }
                    changeBackground();

                    if (!isCollision) {
                        move();
                    }

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
        int previousScore = 0;
        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime)/ns;
            lastTime = now;
            if (delta >= 1) {
                if (score.getCurrentScore() % 10 == 0 && score.getCurrentScore() != previousScore && previousScore < 100) {
                    amountOfTicks += 5.0;
                    ns = 1000000000/ amountOfTicks;
                    previousScore = score.getCurrentScore();
                    changeBackground();
                }
                move();
                repaint();
                delta--;
            }
        }
    }
    */

    public static int getGAME_HEIGHT() {
        return GAME_HEIGHT;
    }

    public static int getGAME_WIDTH() {
        return GAME_WIDTH;
    }

    public void changeBackground() {
        this.setBackground(backgroundColorManager.getBackgroundColor(score.getCurrentScore()));
    }

    public void resetGame() {
        score.updateBestScore();
        score.saveBestScore();
        score.resetScore();
        bird.resetPosition();
        changeBackground();
    }

    public class AL extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (!isCollision) {
                bird.keyPressed(e);
            }
        }
    }
}
