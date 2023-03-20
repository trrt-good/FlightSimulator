import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

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
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Image;
import java.awt.Insets;

//the JPanel which has the game in it
public class GamePanel extends JPanel implements KeyListener, MouseListener, FocusListener
{
    //objects for rendering
    private RenderingPanel renderingPanel; //the panel which preforms rendering
    private Airplane airplane; //the airplane object 
    private Lighting lighting; 
    private Camera gameCamera; //the camera used in the game
    private Terrain ground; //the terrain object which is just a mesh
    private GameObject runway1; //a runway game object 
    private GameObject runway2;

    private Image flightDials; //the image of the flight dials used on the side of the panel

    private int wrongAmount; //number of wrong answers the user has made
    private boolean ended; //wether or not the game has ended 

    private boolean paused; //is the game paused?
    private SidePanel sidePanel; //the panel on the side of the screen which has dials and buttons
    private Color skyColor = new Color(91, 215, 252);

    private boolean learningMode; //has the user learned to fly yet?

    //creates game objects and rendering related objects. 
    public GamePanel()
    {
        setLayout(new BorderLayout());
        addKeyListener(this);
        addFocusListener(this);
        addMouseListener(this);
        sidePanel = new SidePanel();
        wrongAmount = 0;
        add(sidePanel, BorderLayout.EAST);
        ended = false;
        runway1 = new GameObject("runeway1", new Mesh("runway.obj", Color.DARK_GRAY, new Vector3(0, -0.09, 37), new EulerAngle(), 300, false, false), new Transform(new Vector3()));
        runway2 = new GameObject("runeway2", new Mesh("runway.obj", Color.DARK_GRAY, new Vector3(0, -0.09, 2000), new EulerAngle(), 300, false, false), new Transform(new Vector3()));
        
        flightDials = Utils.makeImage(new File(FlightSimulator.RESOURCES_FOLDER, "AirplaneDials.png"));
        lighting = new Lighting(new Vector3(1, -1, 1), 30, 150);
        gameCamera = new Camera(new Vector3(0, 0, -250), 100000, 100, 60);
        airplane = new Airplane(this, gameCamera);
        ground = new Terrain(-500, -200, 6000, 1000, 800, 300, 0.02, 30, new Color(1, 75, 148), new Color(15, 99, 0), new Color(200, 200, 210));
        gameCamera.setOrbitControls(this, airplane, 1000, 10);
    }

    //sets up the rendering panel and starts the rendering updates. 
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        requestFocusInWindow();
        learningMode = !FlightSimulator.user.getCompletedTraining();
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
            renderingPanel.addMesh(runway1.getMesh());
            renderingPanel.addMesh(runway2.getMesh());
            renderingPanel.setFPSlimit(150);
            renderingPanel.start();
            add(renderingPanel);
            validate();
        }
    }

    //the panel that contains dials
    class SidePanel extends JPanel 
    {
        private boolean[] questionChecklist; //tells the program what questions it still has to ask.
        private double randomAltitude; //a randomized altitude that the user will be questioned about
        private double randomSpeed; //a randomized speed that the user will be questioned about

        public SidePanel()
        {
            setBackground(new Color(90, 94, 97));
            setPreferredSize(new Dimension(FlightSimulator.DEFAULT_WIDTH/4, 100));
            setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

            //set up questions
            questionChecklist = new boolean[]{false, false, false, false};
            randomAltitude = Math.random()*1500 + 2500;
            randomSpeed = Math.random()*40 + 75;

            //make buttons
            Button settingsButton = new Button("Settings", 20, 150, 50);
            settingsButton.addActionListener(SettingsPanel.getSettingsSwitcher(GamePanel.name()));
            Button controlsButton = new Button("Controls", 20, 150, 50);
            controlsButton.addActionListener(ControlsPanel.getControlsSwitcher(GamePanel.name()));
            Button resetButton = new Button("Reset", 20, 150, 50);
            resetButton.addActionListener(new ResetButtonListener());    
            add(resetButton);
            add(settingsButton);
            add(controlsButton);
        }

        //repaint called in the airplane updater causes this to be called 
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            //convert to graphics 2d (used for line thickness)
            Graphics2D g2d = (Graphics2D)g;
            //draw the flight dials
            g.drawImage(flightDials, FlightSimulator.DEFAULT_WIDTH/8, 400-FlightSimulator.DEFAULT_WIDTH/8, FlightSimulator.DEFAULT_WIDTH/4, 400, 250, 15, 480, 245, this);
            g.drawImage(flightDials, 0, 400, FlightSimulator.DEFAULT_WIDTH/4, FlightSimulator.DEFAULT_WIDTH/4+400, 0, 245, 480, 728, this);
            g2d.setStroke(new BasicStroke(4));
            g2d.setColor(Color.WHITE);
            
            //draw dial pointers
            drawThrottle(g2d, 50, 230, airplane.getThrottle());
            drawDialNeedle(g2d, 252, 315, 60, airplane.orientation().y); //compass
            drawDialNeedle(g2d, 85, 487, 60, airplane.getSpeed()/75);//airspeed
            drawTurnCoordinator(g2d, 85, 659, airplane.orientation().z); //turn coordinator
            drawDialNeedle(g2d, 255, 489, 55, airplane.getAltitude()/300/Math.PI); //altimeter hundreds needle
            drawDialNeedle(g2d, 255, 489, 30, airplane.getAltitude()/3000/Math.PI); //altimeter thousands needle
            drawDialNeedle(g2d, 255, 659, 60, airplane.getVerticalClimb()/Math.PI/10 - Math.PI/2); //Vertical climb
        }

        //draws a pointer needle for a flight dial centered at the specified coordinates and rotated 
        //a specified amount. 
        public void drawDialNeedle(Graphics2D g2d, int centerX, int centerY, int length, double rotation)
        {
            rotation -= Math.PI/2;
            int endPointX;
            int endPointY;

            endPointX = centerX+(int)(Math.cos(rotation)*length);
            endPointY = centerY+(int)(Math.sin(rotation)*length);

            g2d.drawLine(centerX, centerY, endPointX, endPointY);
        }

        //similar to the dial needles, just two and sideways for the airplane's turn coordinator
        public void drawTurnCoordinator(Graphics2D g2d, int centerX, int centerY, double rotation)
        {
            int endPointX;
            int endPointY;

            endPointX = (int)(Math.cos(-rotation)*50);
            endPointY = (int)(Math.sin(-rotation)*50);
            g2d.drawLine(centerX, centerY, centerX+endPointX, centerY+endPointY);
            g2d.drawLine(centerX, centerY, centerX-endPointX, centerY-endPointY);
        }

        //draws the throttle using two little rectangles and a string which labels the throttle.
        public void drawThrottle(Graphics2D g2d, int topLeftX, int topLeftY, double throttleAmt)
        {
            throttleAmt = 1-throttleAmt;
            g2d.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 30));
            g2d.drawString("Throttle", topLeftX + 20, topLeftY);
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRect(topLeftX, topLeftY, 10, 70);
            g2d.setColor(Color.WHITE);
            g2d.drawRect(topLeftX, (int)(topLeftY + (70*throttleAmt)) - 10, 10, 10);
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

    //toggles the pause used by the key pressed escape key
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

    //pauses the game by stopping the physics and the rendering panel
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

    //inpuases the game by resuming physics and rendering
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

    //applies the user prefferences into reality by setting the fov and sensitivity.
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

    //neccessary for key input to work
    public void mousePressed(MouseEvent e) 
    {
        requestFocusInWindow();
    }
    public void mouseReleased(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    //automatically unpuases if the game panel gains focus, but also updates settings
    //incase the user just returned from the settings panel
    public void focusGained(FocusEvent e) 
    {
        unpause();
        repaint();
        updateSettings();
    }

    //automatically pauses if focus is lost.
    public void focusLost(FocusEvent e) 
    {
        repaint();
        pause();
    }
    public void mouseEntered(MouseEvent e) {}

}
