import java.awt.Color;
import javax.swing.Timer;
 
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
 
import javax.swing.JPanel;
 
public class Airplane extends GameObject implements ActionListener
{
    private Camera camera;
    private double throttle;
    private AirplaneController airplaneController;
    private AirplanePhysics physics;
    private Timer airplaneUpdater;
    private double deltaTime; //the time in seconds since last update.  
    private double lastFrame;
 
    private double maxEnginePower;
    private double pitchSpeed;
    private double yawSpeed;
    private double rollSpeed;
    private double gravity;
    private double mass;
    private double liftCoefficient;
    private double dragCoefficient;
    private double angularDragCoefficient;
    private double aerodynamicEffectAmount;
    
    private double groundLevel;  
     
     
    public Airplane(JPanel listenerPanel, Camera camIn)
    {
        super
        (
            "Airplane",
            new Mesh("planeBody.obj", new Vector3(0, 0, 0), new EulerAngle(0, Math.PI/2, 0), 1, new Color(100, 100, 100), true, true),
            new Transform(new Vector3(0, 0, 0))
        );
 
        maxEnginePower = 58;
        pitchSpeed = 40;
        yawSpeed = 40;
        rollSpeed = 40;
        gravity = 9.8;
        mass = 1100;
        liftCoefficient = 1;
        dragCoefficient = 1;
        angularDragCoefficient = 1.5;
        aerodynamicEffectAmount = 0.04;
        groundLevel = -400;
        
        deltaTime = 0.01;
        lastFrame = 0;
        airplaneController = new AirplaneController();
        camera = camIn;
        listenerPanel.addKeyListener(airplaneController);
        throttle = 0;
        physics = new AirplanePhysics();
        airplaneUpdater = new Timer(10, this);
    }
 
    public void setRenderPanel(RenderingPanel renderingPanel)
    {
        renderingPanel.addMesh(this.getMesh());
    }
 
    public void startPhysics()
    {
        airplaneUpdater.start();
        lastFrame = System.nanoTime()/1000000000.0;
    }
 
    public void actionPerformed(ActionEvent e)  
    {
        physics.update();
        physics.applyThrust(throttle*maxEnginePower);
        getMesh().refreshLighting();
        deltaTime = System.nanoTime()/1000000000.0 - lastFrame;
        lastFrame = System.nanoTime()/1000000000.0;
    }
 
    class AirplaneController implements KeyListener, ActionListener
    {
        public Timer keyTimer;
 
        public boolean throttleUp;
        public boolean throttleDown;
        public boolean pitchUp;
        public boolean pitchDown;
        public boolean rollLeft;
        public boolean rollRight;
        public boolean yawLeft;
        public boolean yawRight;
        public boolean brakes;
 
        public AirplaneController()
        {
            keyTimer = new Timer(5, this);
            throttleUp = false;
            throttleDown = false;
            pitchUp = false;
            pitchDown = false;
            rollLeft = false;
            rollRight = false;
            yawLeft = false;
            yawRight = false;
            brakes = false;
            keyTimer.start();
        }
 
        public void keyPressed(KeyEvent e)  
        {
            int key = e.getKeyCode();
            if (key == FlightSimulator.settings.throttleUp)
                throttleUp = true;
            else if (key == FlightSimulator.settings.throttleDown)
                throttleDown = true;
            else if (key == FlightSimulator.settings.pitchUp)
                pitchUp = true;
            else if (key == FlightSimulator.settings.pitchDown)
                pitchDown = true;
            else if (key == FlightSimulator.settings.rollLeft)
                rollLeft = true;
            else if (key == FlightSimulator.settings.rollRight)
                rollRight = true;
            else if (key == FlightSimulator.settings.yawLeft)
                yawLeft = true;
            else if (key == FlightSimulator.settings.yawRight)
                yawRight = true;
            else if (key == FlightSimulator.settings.brakes)
                brakes = true;
        }
 
        public void keyTyped(KeyEvent e) {}
        public void keyReleased(KeyEvent e)  
        {
            int key = e.getKeyCode();
            if (key == FlightSimulator.settings.throttleUp)
                throttleUp = false;
            else if (key == FlightSimulator.settings.throttleDown)
                throttleDown = false;
            else if (key == FlightSimulator.settings.pitchUp)
                pitchUp = false;
            else if (key == FlightSimulator.settings.pitchDown)
                pitchDown = false;
            else if (key == FlightSimulator.settings.rollLeft)
                rollLeft = false;
            else if (key == FlightSimulator.settings.rollRight)
                rollRight = false;
            else if (key == FlightSimulator.settings.yawLeft)
                yawLeft = false;
            else if (key == FlightSimulator.settings.yawRight)
                yawRight = false;
            else if (key == FlightSimulator.settings.brakes)
                brakes = false;
        }
 
        public void actionPerformed(ActionEvent e)  
        {
            if (throttleUp && throttle < 100)
                throttle += 1;
            if (throttleDown && throttle > 0)
                throttle -= 1;
            if (pitchUp)
                physics.addPitchTorque(-pitchSpeed);
            if (pitchDown)
                physics.addPitchTorque(pitchSpeed);
            if (rollLeft)
                physics.addRollTorque(rollSpeed);
            if (rollRight)
                physics.addRollTorque(-rollSpeed);
            if (yawLeft)
                physics.addYawTorque(-yawSpeed);
            if (yawRight)
                physics.addYawTorque(yawSpeed);
            //TODO: implement landing gear and brakes
        }
    }
 
    class AirplanePhysics
    {
 
        private Vector3 physicsPosition;
        private EulerAngle physicsRotation;
        private double forwardSpeed;
        private Vector3 velocity;
        private double velocityPitch;
        private double velocityYaw;
        private double velocityRoll;
 
        public AirplanePhysics()
        {
            physicsRotation = new EulerAngle();
            physicsPosition = new Vector3();
            velocity = new Vector3();
            velocityPitch = 0;
            velocityRoll = 0;
            velocityYaw = 0;
        }
 
        public void addForce(Vector3 force)
        {
            velocity.add(Vector3.multiply(force, 1/mass));
        }
 
        public void addPitchTorque(double amt)
        {
            velocityPitch = velocityPitch + amt/mass*deltaTime;
        }
 
        public void addYawTorque(double amt)
        {
            velocityYaw = velocityYaw + amt/mass*deltaTime;
        }
 
        public void addRollTorque(double amt)
        {
            velocityRoll = velocityRoll + amt/mass*deltaTime;
        }
 
        public void applyDrag()
        {
            if (velocity.getSqrMagnitude() > 0)
            {
                velocity = Vector3.subtract(velocity, Vector3.multiply(velocity, dragCoefficient*velocity.getMagnitude()*deltaTime/100));
                Vector3 verticalDrag = Vector3.projectToVector(velocity, getTransform().getUp());
                velocity = Vector3.subtract(velocity, Vector3.multiply(verticalDrag, dragCoefficient*verticalDrag.getMagnitude()*deltaTime));
            }
        }
 
        public void calculateForward()
        {
            forwardSpeed = Vector3.dotProduct(velocity, getTransform().getForward());
        }
 
        public void applyAngularDrag()
        {
            velocityPitch = velocityPitch - velocityPitch*angularDragCoefficient*deltaTime;
            velocityYaw = velocityYaw - velocityYaw*angularDragCoefficient*deltaTime;
            velocityRoll = velocityRoll - velocityRoll*angularDragCoefficient*deltaTime;
        }
 
        public void applyGravity()
        {
            velocity.add(new Vector3(0, -gravity*deltaTime, 0));
        }
 
        public void applyLift()
        {
            addForce(Vector3.multiply(Vector3.crossProduct(velocity, getTransform().getRight()).getNormalized(), forwardSpeed*forwardSpeed*liftCoefficient*deltaTime));
        }
 
        //gives the effect of the plane naturally alligning itself to the direction it's pointing.
        public void applyAerodynamicEffect()
        {
            if (velocity.getSqrMagnitude() > 0)
            {
                //compares the direction we are facing to the direction we are moving
                double correctionFactor = Vector3.dotProduct(getTransform().getForward(), velocity.getNormalized());
                //squaring gives it a better rolloff curve for the effect
                correctionFactor *= correctionFactor;
 
                //calculates the new velocity by bending the current velocity towards the  
                //direction we are facing, by the correction factor.
                velocity = Vector3.lerp(velocity, Vector3.projectToVector(velocity, getTransform().getForward()), correctionFactor*forwardSpeed*aerodynamicEffectAmount*deltaTime/1000);  
                 
                //also rotate the plane towards the direction of movement.
                Vector3 direction = getTransform().transformToLocal(Vector3.lerp(getTransform().getForward(), velocity.getNormalized(), correctionFactor*forwardSpeed*aerodynamicEffectAmount*deltaTime));
                physicsRotation.y += ((direction.x < 0)? -Math.atan(direction.z/direction.x)-Math.PI/2 : Math.PI/2-Math.atan(direction.z/direction.x));
                physicsRotation.x += Math.atan(direction.y/Math.sqrt(direction.x*direction.x + direction.z*direction.z));
            }
        }
 
        public void updatePosition()
        {
            if (Vector3.add(physicsPosition, velocity).y > groundLevel)
                physicsPosition.add(velocity);
            else
            {
                physicsPosition.y = groundLevel;
                velocity = new Vector3(velocity.x, 0 , velocity.z);
                physicsPosition.add(velocity);
            }
            getTransform().setPosition(Vector3.add(getTransform().getPosition(), velocity));
            if (camera.getOrbitCamController() != null)
            {
                camera.getOrbitCamController().updatePosition();
            }
        }
 
        public void updateOrientation()
        {
            physicsRotation.x += velocityPitch;
            physicsRotation.y += velocityYaw;
            physicsRotation.z += velocityRoll;
 
            getTransform().setPitch(physicsRotation.x);
            getTransform().setYaw(physicsRotation.y);
            getTransform().setRoll(physicsRotation.z);
 
        }
 
        public void applyThrust(double amount)
        {
            addForce(Vector3.multiply(getTransform().getForward(), amount*deltaTime));
        }
 
        public Vector3 getPosition()
        {
            return physicsPosition;
        }
 
        public void update()
        {
            calculateForward();
            applyAerodynamicEffect();
            applyGravity();  
            applyLift();  
            applyDrag();
            applyAngularDrag();
            updateOrientation();
            updatePosition();
             
             
             
                       
        }
    }
 
     
}
 
 
