import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.BorderLayout;

public class GamePanel extends JPanel
{
    //objects for rendering
    private RenderingPanel renderingPanel;
    private Airplane airplane;
    private Lighting lighting; 
    private Camera gameCamera;
    private Terrain ground;

    private Color skyColor = new Color(200, 220, 255);

    //creates game objects and rendering related objects. 
    public GamePanel()
    {
        setLayout(new BorderLayout());
        
        lighting = new Lighting(new Vector3(1, -1, 1), 30, 100);
        gameCamera = new Camera(new Vector3(0, 0, -1000), 10000, 10, 60);
        airplane = new Airplane(this, gameCamera);
        ground = new Terrain(-500, -200, 380, 200, 500, 500, 20, 15, new Color(18, 99, 199), new Color(10, 50, 20), Color.WHITE);
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
        renderingPanel.setFog(8000, 10000, skyColor);
        renderingPanel.addMesh(ground);
        renderingPanel.start();
        add(renderingPanel);
        validate();
    }

    public String getName()
    {
        return "GamePanel";
    }
}
