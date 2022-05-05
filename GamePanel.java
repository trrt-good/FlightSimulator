import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel implements KeyListener
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
        requestFocusInWindow();
        renderingPanel = new RenderingPanel(FlightSimulator.DEFAULT_WIDTH, FlightSimulator.DEFAULT_HEIGHT);
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

    public void setUpSidePanel()
    {
        JPanel sidePanel = new JPanel();
        sidePanel.setBackground(new Color(50, 50, 50));
        sidePanel.setPreferredSize(new Dimension(FlightSimulator.DEFAULT_WIDTH/5, 100));
        this.add(sidePanel);

    }

    public String getName()
    {
        return "GamePanel";
    }

    public void keyPressed(KeyEvent e) 
    {
        requestFocusInWindow();
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            System.out.println(Thread.currentThread());
            System.out.println(2);
            if (paused)
            {
                paused = false;
                airplane.startPhysics();
                renderingPanel.start();
            }
            else
            {
                paused = true;
                airplane.stopPhysics();
                renderingPanel.stopThread();
            }
        }
    }

    public static boolean isPaused()
    {
        return paused;
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}
