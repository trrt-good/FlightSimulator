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
import java.io.File;
import java.awt.event.MouseEvent;
import java.awt.event.FocusListener;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Image;

public class GamePanel extends JPanel implements KeyListener, MouseListener, FocusListener
{
    //objects for rendering
    private RenderingPanel renderingPanel;
    private Airplane airplane;
    private Lighting lighting; 
    private Camera gameCamera;
    private Terrain ground;
    private Image flightDials; 


    private boolean paused;
    private SidePanel sidePanel;
    private Color skyColor = new Color(91, 215, 252);

    //creates game objects and rendering related objects. 
    public GamePanel()
    {
        setLayout(new BorderLayout());
        addKeyListener(this);
        addFocusListener(this);
        addMouseListener(this);
        sidePanel = new SidePanel();
        add(sidePanel, BorderLayout.EAST);
        flightDials = FlightSimulator.makeImage(new File(FlightSimulator.RESOURCES_FOLDER, "AirplaneDials.png"));
        lighting = new Lighting(new Vector3(1, -1, 1), 30, 150);
        gameCamera = new Camera(new Vector3(0, 0, -1000), 50000, 30, 60);
        airplane = new Airplane(this, gameCamera);
        ground = new Terrain(-500, -200, 6000, 1000, 500, 500, 0.02, 30, new Color(1, 75, 148), new Color(15, 99, 0), new Color(200, 200, 210));
        gameCamera.setOrbitControls(this, airplane, 1000, 10);
    }

    //sets up the rendering panel and starts the rendering updates. 
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 30));
        requestFocusInWindow();
        if (renderingPanel == null)
        {
            renderingPanel = new RenderingPanel(FlightSimulator.DEFAULT_WIDTH - FlightSimulator.DEFAULT_WIDTH/4, FlightSimulator.DEFAULT_HEIGHT);
            gameCamera.setFov(FlightSimulator.user.getSettings().fov);
            gameCamera.setSensitivity(FlightSimulator.user.getSettings().sensitivity);
            airplane.setRenderPanel(renderingPanel);
            airplane.startPhysics();
            renderingPanel.setLighting(lighting);
            renderingPanel.setCamera(gameCamera);
            renderingPanel.setLighting(lighting);
            renderingPanel.setFog(gameCamera.getFarClipDistancee()*0.6, gameCamera.getFarClipDistancee(), skyColor);
            renderingPanel.addMesh(ground);
            renderingPanel.setFPSlimit(150);
            renderingPanel.start();
            add(renderingPanel);
            validate();
        }
    }

    class SidePanel extends JPanel 
    {
        public SidePanel()
        {
            setBackground(new Color(90, 94, 97));
            setPreferredSize(new Dimension(FlightSimulator.DEFAULT_WIDTH/4, 100));
            setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
            Button mainMenuButton = new Button("Main Menu", 20, 150, 50);
            mainMenuButton.addActionListener(MainMenu.getMainMenuPanelSwitcher());
            Button settingsButton = new Button("Settings", 20, 150, 50);
            settingsButton.addActionListener(SettingsPanel.getSettingsSwitcher(GamePanel.name()));
            Button controlsButton = new Button("Controls", 20, 150, 50);
            controlsButton.addActionListener(ControlsPanel.getControlsSwitcher(GamePanel.name()));
            Button resetButton = new Button("Reset", 20, 150, 50);
            resetButton.addActionListener(new ResetButtonListener());
            add(mainMenuButton);
            add(settingsButton);
            add(controlsButton);
            add(resetButton);
            
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D)g;
            g.drawImage(flightDials, FlightSimulator.DEFAULT_WIDTH/8, 400-FlightSimulator.DEFAULT_WIDTH/8, FlightSimulator.DEFAULT_WIDTH/4, 400, 250, 15, 480, 245, this);
            g.drawImage(flightDials, 0, 400, FlightSimulator.DEFAULT_WIDTH/4, FlightSimulator.DEFAULT_WIDTH/4+400, 0, 245, 480, 728, this);
            g2d.setStroke(new BasicStroke(4));
            g2d.setColor(Color.WHITE);
            
            //  compass center = 253, 315
            //  airspeed center = 86, 487
            g.drawLine(252, 315, 252, 250); //compass
            g.drawLine(86, 487, 86, 430); //airspeed
        }
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
            repaint();
            airplane.startPhysics();
            renderingPanel.start();
        }
        else
        {
            paused = true;
            repaint();
            airplane.stopPhysics();
            renderingPanel.stopThread();
        }
    }

    public void pause()
    {
        if (!paused)
        {
            paused = true;
            repaint();
            airplane.stopPhysics();
            renderingPanel.stopThread();
        }
    }

    public void unpause()
    {
        if (paused)
        {
            paused = false;
            repaint();
            airplane.startPhysics();
            renderingPanel.start();
        }
    }

    public void updateSettings()
    {
        gameCamera.setFov(FlightSimulator.user.getSettings().fov);
        gameCamera.setSensitivity(FlightSimulator.user.getSettings().sensitivity);
    }

    public boolean isPaused()
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
            FlightSimulator.flightSim.showPanel(name());
        }
    }

    class ResetButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            airplane.reset();
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
        repaint();
        updateSettings();
    }
    public void focusLost(FocusEvent e) 
    {
        repaint();
        pause();
    }
    public void mouseEntered(MouseEvent e) {}
}
