import javax.swing.Action;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.FocusListener;

public class GamePanel extends JPanel implements KeyListener, MouseListener, FocusListener
{
    //objects for rendering
    private RenderingPanel renderingPanel;
    private Airplane airplane;
    private Lighting lighting; 
    private Camera gameCamera;
    private Terrain ground;

    private static boolean paused;

    private Color skyColor = new Color(200, 220, 255);

    //creates game objects and rendering related objects. 
    public GamePanel()
    {
        setLayout(new BorderLayout());
        addKeyListener(this);
        addFocusListener(this);
        addMouseListener(this);
        setUpSidePanel();
        lighting = new Lighting(new Vector3(1, -1, 1), 30, 150);
        gameCamera = new Camera(new Vector3(0, 0, -1000), 30000, 10, 60);
        airplane = new Airplane(this, gameCamera);
        ground = new Terrain(-500, -200, 2000, 750, 500, 500, 0.02, 30, new Color(18, 99, 199), new Color(10, 50, 20), new Color(230, 230, 230));
        //gameCamera.setFreeControls(this, 500, 10);
        gameCamera.setOrbitControls(this, airplane, 1000, 10);
    }

    //sets up the rendering panel and starts the rendering updates. 
    public void paintComponent(Graphics g)
    {
        setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 30));
        requestFocusInWindow();
        if (renderingPanel == null)
        {
            renderingPanel = new RenderingPanel(FlightSimulator.DEFAULT_WIDTH - FlightSimulator.DEFAULT_WIDTH/5, FlightSimulator.DEFAULT_HEIGHT);
            gameCamera.setFov(FlightSimulator.user.getSettings().fov);
            gameCamera.setSensitivity(FlightSimulator.user.getSettings().sensitivity);
            airplane.setRenderPanel(renderingPanel);
            airplane.startPhysics();
            renderingPanel.setLighting(lighting);
            renderingPanel.setCamera(gameCamera);
            renderingPanel.setLighting(lighting);
            renderingPanel.setFog(25000, 30000, skyColor);
            renderingPanel.addMesh(ground);
            renderingPanel.setFPSlimit(150);
            renderingPanel.start();
            add(renderingPanel);
            validate();
        }

        if (paused)
        {
            g.drawString("PAUSED", 300, 200);
        }
        System.out.println("paintcomp");
    }

    public void setUpSidePanel()
    {
        JPanel sidePanel = new JPanel();
        sidePanel.setBackground(new Color(50, 50, 50));
        sidePanel.setPreferredSize(new Dimension(FlightSimulator.DEFAULT_WIDTH/5, 100));
        sidePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 30));
        Button mainMenuButton = new Button("Main Menu", 30);
        mainMenuButton.addActionListener(MainMenu.getMainMenuPanelSwitcher());
        Button settingsButton = new Button("Settings", 30);
        settingsButton.addActionListener(SettingsPanel.getSettingsSwitcher());
        Button controlsButton = new Button("Controls", 30);
        sidePanel.add(mainMenuButton);
        sidePanel.add(settingsButton);
        sidePanel.add(controlsButton);
        this.add(sidePanel, BorderLayout.EAST);
    }

    public static String name()
    {
        return "GamePanel";
    }

    public void keyPressed(KeyEvent e) 
    {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            togglePause();
        }
    }

    public void togglePause()
    {
        if (paused)
        {
            paused = false;
            airplane.startPhysics();
            renderingPanel.start();
        }
        else
        {
            //repaint();
            paused = true;
            airplane.stopPhysics();
            renderingPanel.stopThread();
        }
    }

    public void pause()
    {
        if (!paused)
        {
            //repaint();
            paused = true;
            airplane.stopPhysics();
            renderingPanel.stopThread();
        }
    }

    public void unpause()
    {
        if (paused)
        {
            paused = false;
            airplane.startPhysics();
            renderingPanel.start();
        }
    }

    public static boolean isPaused()
    {
        return paused;
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

    public static SwitchToGamePanelListener getGamePanelSwitcher()
    {
        return new SwitchToGamePanelListener();
    }

    static class SwitchToGamePanelListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e) 
        {
            if (FlightSimulator.user.getCompletedTraining())
                FlightSimulator.flightSim.showPanel(name());
        }
    }

    class PauseListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            togglePause();
        }
    }

    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) 
    {
        requestFocusInWindow();
    }
    public void mouseReleased(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void focusGained(FocusEvent e) 
    {
        unpause();
        System.out.println("focused");
    }
    public void focusLost(FocusEvent e) 
    {
        pause();
    }
    public void mouseEntered(MouseEvent e) {}
}
