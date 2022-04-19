import javax.naming.ldap.Rdn;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.BorderLayout;

public class GamePanel extends JPanel
{
    private RenderingPanel renderingPanel;
    private GameObject tempGameObject;
    private Lighting lighting; 
    private Camera gameCamera;

    private Color skyColor = new Color(200, 220, 255);

    public GamePanel()
    {
        setLayout(new BorderLayout());

        tempGameObject = new GameObject(new Vector3(0, 0, 0), new EulerAngle(0, 0, 0), 1, "lowPolyPlane1.obj", new Color(150, 150, 150));
        lighting = new Lighting(new Vector3(0, -1, 0), 100, 100);
        gameCamera = new Camera(new Vector3(0, 0, -1000), 10000, 60);
        gameCamera.setOrbitControls(this, tempGameObject, 1000, 10);
    }

    public void paintComponent(Graphics g)
    {
        requestFocusInWindow();
        renderingPanel = new RenderingPanel(FlightSimulator.DEFAULT_WIDTH, FlightSimulator.DEFAULT_HEIGHT);
        renderingPanel.addGameObject(tempGameObject);
        renderingPanel.setLighting(lighting);
        renderingPanel.setCamera(gameCamera);
        renderingPanel.setLighting(lighting);
        renderingPanel.setFog(5000, 7000, skyColor);
        renderingPanel.startRenderUpdates();
        add(renderingPanel);
        validate();
    }

    public String getName()
    {
        return "GamePanel";
    }
}
