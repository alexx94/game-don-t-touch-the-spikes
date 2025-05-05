import java.awt.*;
import java.awt.color.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GameFrame extends JFrame implements PanelSwitcher{
    private GamePanel gamePanel;
    private MenuPanel menuPanel;
    private CardLayout cardLayout;
    private Map<String, SwitchablePanel> panels;

    GameFrame() {
        cardLayout = new CardLayout();
        this.setLayout(cardLayout);
        panels = new HashMap<>();

        /* 
        menuPanel = new MenuPanel(this);
        gamePanel = new GamePanel(this);

        this.add(menuPanel, "MenuPanel");
        this.add(gamePanel, "GamePanel");
        */

        registerPanel("MenuPanel", new MenuPanel(this));
        registerPanel("GamePanel", new GamePanel(this));


        this.setTitle("Don't Touch the Spikes");
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);

        //menuPanel.start();
        switchToPanel("MenuPanel");
    }

    public void registerPanel(String id, SwitchablePanel panel) {
        panels.put(id, panel);
        add((JPanel) panel, id);
    }

    public void switchToPanel(String panelId) {
        SwitchablePanel currentPanel = panels.get(panelId);
        if (currentPanel != null) {
            // opresc toate panelurile pe care le am
            // SA MODIFIC ASTA MAI INCOLO MAI CUSTOM PUTIN
            panels.values().forEach(SwitchablePanel::pause);

            //currentPanel.start();
            cardLayout.show(getContentPane(), panelId);
            //((JPanel) currentPanel).requestFocusInWindow();

            printThreads();

            System.out.println("transitioning");
            SwingUtilities.invokeLater(() -> ((JPanel) currentPanel).requestFocusInWindow());
            SwingUtilities.invokeLater(() -> currentPanel.start());
        } else {
            System.err.println("No panel found with ID: " + panelId);
        }
    }

    public static void printThreads() {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        int activeThreads = group.activeCount();
        Thread[] threads = new Thread[activeThreads];
        group.enumerate(threads);
        
        for (Thread thread : threads) {
            System.out.println(thread.getName());
        }
    }
    

    /*
     * 
     
    @Override
    public void switchToGamePanel() {
        //menuPanel.stopMenu();
        gamePanel.startGame();
        this.remove(menuPanel);
        cardLayout.show(this.getContentPane(), "GamePanel");
        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
    }

    @Override
    public void switchToMenuPanel() {
        gamePanel.stopGame();
        cardLayout.show(this.getContentPane(), "MenuPanel");
        SwingUtilities.invokeLater(() -> menuPanel.requestFocusInWindow());
    }
    */
}
