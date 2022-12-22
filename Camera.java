import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JPanel;

public class Camera
{
    //field of view of the camera.
    private double fov; //strictly reffers to the horizontal fov as vertical fov is based off screen height 

    private Vector3 position; //position of camera in world-space
    private double hAngle; //horizontal angle of camera
    private double vAngle; //vertical angle of camera
    private double renderPlaneDistance; //distance from the camera that the rendering plane is
    private double farClipDistance; //how far away should triangles stop being rendered?
    private double nearClipDistance; //how close should triangles stop being rendered?

    //movement controller 
    private OrbitCamController orbitController = null;

    //width of the render plane based off fov. 
    private double renderPlaneWidth;

    public Camera(Vector3 positionIn, double farClipDistanceIn, double nearClipDistanceIn, double fovIn)
    {
        renderPlaneDistance = 10;
        hAngle = 0;
        vAngle = 0;
        position = positionIn;
        farClipDistance = farClipDistanceIn;
        nearClipDistance = nearClipDistanceIn;
        setFov(fovIn);
    }

    //sets the v and h angles to look at the specified position. 
    public void lookAt(Vector3 pos)
    {
        hAngle = (pos.x-position.x < 0)? -Math.toDegrees(Math.atan((pos.z-position.z)/(pos.x-position.x)))-90 : 90-Math.toDegrees(Math.atan((pos.z-position.z)/(pos.x-position.x)));

        vAngle = Math.toDegrees(Math.atan((pos.y-position.y)/(Math.sqrt((pos.x-position.x)*(pos.x-position.x) + (pos.z-position.z)*(pos.z-position.z)))));
        
        hAngle%=360;
        vAngle%=360;
    }

    //camera controller which orbits a specified GameObject. panning the camera will cause it to 
    //circle around that object. The user can also use the scroll wheel to zoom in and out from 
    //the game object. 
    class OrbitCamController implements MouseMotionListener, MouseWheelListener, MouseListener
    {
        private int maxDistance = 4000; //maximum distance the camera can be from the object
        private int minDistance = 500; //minimum distance

        private double distance; //distacne from the object

        private int maxAngle = 80; //the maximum angle the camera can go up to
        private int minAngle = -80; //the minimum angle the camera can go down to.

        private GameObject focusObj; //the game object that the camera is focused on. 
        private double sensitivity; //how fast should the camera pan?

        //the previous mouse click position
        private int prevX = 0;
        private int prevY = 0;

        //the difference in position between the camera and the object it's focusing on.
        private Vector3 difference;
        private Vector3 directionUnit; // the normalized vector pointing away from the focusObj
        
        public OrbitCamController(GameObject focusObjectIn, double startDistanceIn, double sensitivityIn)
        {
            focusObj = focusObjectIn;
            distance = startDistanceIn;
            sensitivity = sensitivityIn;
            directionUnit = new Vector3();

            //sets up the position of the camera.
            position = Vector3.add(focusObj.getTransform().getPosition(), new Vector3(0, 0, -distance));
            directionUnit = Vector3.subtract(position, focusObj.getTransform().getPosition()).getNormalized();
            difference = Vector3.multiply(directionUnit, startDistanceIn);
        }

        //changes the distance from the camera to the focusObj based on the mouse movement. 
        public void mouseWheelMoved(MouseWheelEvent e) 
        {
            if (FlightSimulator.flightSim.getGamePanel().isPaused())
                return;
            distance = Math.max(minDistance, Math.min(distance + e.getWheelRotation()*30, maxDistance));
            difference = Vector3.multiply(directionUnit, distance);
            updatePosition();
        }

        //pans the difference vector around the focused object by changing the directionUnit vector 
        //and then multiplying that by the distance scalar to get the actual differece, then calls
        //the update position method which sets the position of the camera based on the difference vector.
        public void mouseDragged(MouseEvent e) 
        {
            if (FlightSimulator.flightSim.getGamePanel().isPaused())
                return;
            directionUnit = Vector3.rotateAroundYaxis(directionUnit, (e.getX()-prevX)/(2000/sensitivity));
            if (vAngle > -maxAngle && (e.getY()-prevY)/(200/sensitivity) > 0)
                directionUnit = Vector3.rotateAroundYaxis(Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis(directionUnit, -hAngle*0.017453292519943295), (e.getY()-prevY)/(2000/sensitivity)), hAngle*0.017453292519943295);
            else if (vAngle < -minAngle && (e.getY()-prevY)/(200/sensitivity) < 0)
                directionUnit = Vector3.rotateAroundYaxis(Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis(directionUnit, -hAngle*0.017453292519943295), (e.getY()-prevY)/(2000/sensitivity)), hAngle*0.017453292519943295);
            difference = Vector3.multiply(directionUnit, distance);
            updatePosition();
            lookAt(focusObj.getTransform().getPosition());
            vAngle = Math.max(-89, Math.min(89, vAngle));
            prevX = e.getX();
            prevY = e.getY();
        }

        public void mousePressed(MouseEvent e) 
        {
            prevX = e.getX();
            prevY = e.getY();
        }

        //sets the sensitivty of the camera which is used for user settings
        public void setSensitivity(double sens)
        {
            sensitivity = sens;
        }

        //updates the position of the camera to be around the focusObject. 
        //uses the difference vector calculated on other methods
        public void updatePosition()
        {
            if (FlightSimulator.flightSim.getGamePanel().isPaused())
                return;
            position = Vector3.add(focusObj.getTransform().getPosition(), difference);
        }

        //returns the focusObj
        public GameObject getFocusObj()
        {
            return focusObj;
        }

        public void mouseMoved(MouseEvent e) {}
        public void mouseClicked(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
    }

    //sets the controls of the camera to orbit mode, with the needed values for the constructor.
    public void setOrbitControls(JPanel panel, GameObject focusObject, double startDistance, double sensitivity)
    {
        orbitController = new OrbitCamController(focusObject, startDistance, sensitivity);
        panel.addMouseListener(orbitController);
        panel.addMouseMotionListener(orbitController);
        panel.addMouseWheelListener(orbitController);
    }

    //calculates the render plane width, which is a slightly expensive method, so it is only called once. 
    private double calculateRenderPlaneWidth()
    {
        return Math.tan(fov*0.017453292519943295/2)*renderPlaneDistance*2;
    }

    //#region getter/setter methods
    public double getFarClipDistancee()
    {
        return farClipDistance;
    }

    //returns the distance of the near clipping pane used by rendering
    public double getNearClipDistance()
    {
        return nearClipDistance;
    }

    public OrbitCamController getOrbitCamController()
    {
        return orbitController;
    }

    //sets the fov but also re calculates the width of the rendering plane
    //based on the new fov which is used for rendering
    public void setFov(double fovIn)
    {
        fov = fovIn;
        renderPlaneWidth = calculateRenderPlaneWidth();
    }

    public void setSensitivity(double sense)
    {
        if (orbitController != null)
            orbitController.setSensitivity(sense);
    }

    public double getRenderPlaneDistance()
    {
        return renderPlaneDistance;
    }

    //returns the direction of the camera as a normalized vector.
    public Vector3 getDirectionVector()
    {
        return Vector3.angleToVector(hAngle*0.017453292519943295, vAngle*0.017453292519943295);
    }

    //returns the horizontal orientation of the camera (yaw)
    public double getHorientation()
    {
        return hAngle;
    }

    //returns the vertical orientation of the camera (pitch)
    public double getVorientation()
    {
        return vAngle;
    }
    
    public void setFocusObj(GameObject obj)
    {
        orbitController.focusObj = obj;
    }

    public Vector3 getPosition()
    {
        return position;
    }

    public double getRenderPlaneWidth()
    {
        return renderPlaneWidth;
    }

    //#endregion
}

