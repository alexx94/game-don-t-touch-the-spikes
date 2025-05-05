import java.awt.*;
import java.awt.event.KeyEvent;

public class Bird extends Rectangle {
    
    double xVelocity = 3.0;
    double yVelocity = 1.0;
    double gravity = 0.2;
    int XDirection;
    int YDirection;
    long lastJumpTime = System.currentTimeMillis();
    static final long JUMP_DELAY = 100;

    Bird(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.XDirection = 1;
        this.YDirection = 1;
    }

    public int getXDirection() {
        return this.XDirection;
    }

    public void setXDirection(int newXDirection) {
        this.XDirection = newXDirection;
    }

    public void setYDirection(int newYDirection) {
        this.YDirection = newYDirection;
    }

    public void resetPosition() {
        this.x = 180;
        this.y = 200;
        setXDirection(1);
        setYDirection(1);
        yVelocity = 1.0;
        xVelocity = 3.0;
    }

    public void move() {
        yVelocity += gravity;   
        this.x += xVelocity * XDirection;
        this.y += yVelocity * YDirection;

        if (this.y >= (GamePanel.getGAME_HEIGHT() - this.height)) {
            yVelocity = 0;
        }  
        // pot imbunatati putin logica si cu partea de sus, jos e perfect acum
    }

    public void menuAnimation(int initialY, int animationMovement) { 
        if (this.y <= initialY - animationMovement) {
            YDirection = 1;
        }
        if (this.y >= initialY + animationMovement) {
            YDirection = -1;  
        }

        this.y += yVelocity * YDirection;
    }

    public void draw(Graphics g) {
        g.setColor(Color.white);
        g.fillOval(x, y, width, height);
    }

    public void jump() {
        yVelocity = -4;
    }

    public void keyPressed(KeyEvent e) {
        long currentTime = System.currentTimeMillis();
        if (e.getKeyCode()==KeyEvent.VK_UP && currentTime - lastJumpTime >= JUMP_DELAY) {
            jump();
            lastJumpTime = currentTime;
        }
    }
}
