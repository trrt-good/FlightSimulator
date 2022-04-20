import java.awt.Color;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;

public class Airplane extends GameObject
{
    private Propeller propeller;
    private Wheels wheels; 
    private double throttle;
    private AirplaneController airplaneController;

    private Vector3 offset = new Vector3(0, 31, -53.5);

    public Airplane(JPanel listenerPanel)
    {
        super(new Vector3(0, 0, 0), new Vector3(100.86, 75, -2.14), new EulerAngle(0, 0, 0), 1, "planeBody.obj", new Color(130, 130, 130));
        airplaneController = new AirplaneController();
        listenerPanel.addKeyListener(airplaneController);
        throttle = 0;
        propeller = new Propeller();
        wheels = new Wheels();
    }

    public void setRenderPanel(RenderingPanel renderingPanel)
    {
        renderingPanel.addGameObject(this);
        renderingPanel.addGameObject(propeller);
        renderingPanel.addGameObject(wheels);
    }

    class Propeller extends GameObject implements ActionListener
    {
        private Timer rotator;

        public Propeller()
        {
            super(new Vector3(-230.54, -36.11, 54.05), new EulerAngle(0, 0, 0), 1, "propeller.obj", new Color(50, 50, 50));
            rotator = new Timer(20, this);
            rotator.start();
        }

        public void actionPerformed(ActionEvent e) 
        {
            rotator.setDelay(20);
            double angle = Math.toRadians(Math.pow(1.1, throttle-60)+0.3*throttle);

            Triangle triangle;
            for (int i = 0; i < getMesh().size(); i++)
            {
                triangle = getMesh().get(i);
                triangle.point1 = Vector3.subtract(triangle.point1, Vector3.add(getPosition(), offset));
                triangle.point1 = Vector3.rotateAroundXaxis(triangle.point1, angle);
                triangle.point1 = Vector3.add(triangle.point1, Vector3.add(getPosition(), offset));

                triangle.point2 = Vector3.subtract(triangle.point2, Vector3.add(getPosition(), offset));
                triangle.point2 = Vector3.rotateAroundXaxis(triangle.point2, angle);
                triangle.point2 = Vector3.add(triangle.point2, Vector3.add(getPosition(), offset));

                triangle.point3 = Vector3.subtract(triangle.point3, Vector3.add(getPosition(), offset));
                triangle.point3 = Vector3.rotateAroundXaxis(triangle.point3, angle);
                triangle.point3 = Vector3.add(triangle.point3, Vector3.add(getPosition(), offset));
            }
        }


    }

    class Wheels extends GameObject 
    {
        public Wheels()
        {
            super(new Vector3(130.86, -72, -2.14), new EulerAngle(0, 0, 0), 1, "wheels.obj", new Color(50, 50, 50));
        }
    }

    class AirplaneController implements KeyListener
    {
        public void keyPressed(KeyEvent e) 
        {
            if (e.getKeyChar() == 's')
            {
                throttle++;
            }
        }

        public void keyTyped(KeyEvent e) {}
        public void keyReleased(KeyEvent e) {}
    }
}


