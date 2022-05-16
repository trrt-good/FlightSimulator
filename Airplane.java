import javax.swing.Timer;
 
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
 
import javax.swing.JPanel;
 
public class Airplane extends GameObject implements ActionListener
{
    private final int CRASH_THRESHOLD = 30;

    private Camera camera;
    private double throttle;
    private AirplaneController airplaneController;
    private AirplanePhysics physics;
    private Timer airplaneUpdater;
    private double deltaTime; //the time in seconds since last update.  
    private double lastFrame;
    private boolean physicsEnabled;
 
    private double maxEnginePower;
    private double pitchSpeed;
    private double yawSpeed;
    private double rollSpeed;
    private double gravity;
    private double mass;
    private double liftCoefficient;
    private double dragCoefficient;
    private double angularDragCoefficient;
    private double yawRollEffectAmount;
    private double aerodynamicEffectAmount;
    
    private double groundLevel;  
     
     
    public Airplane(JPanel listenerPanel, Camera camIn)
    {
        super
        (
            "Airplane",
            new Mesh("airplane.obj", "airplaneTexture.png", new Vector3(0, 0, 0), new EulerAngle(0, 0, 0), 2, true, true),
            new Transform(new Vector3(0, 0, 0))
        );
 
        maxEnginePower = 25000;
        pitchSpeed = 20;
        yawSpeed = 20;
        rollSpeed = 30;
        gravity = 30;
        mass = 1000;
        liftCoefficient = 15;
        dragCoefficient = 0.5;
        angularDragCoefficient = 1;
        aerodynamicEffectAmount = 0.01;
        yawRollEffectAmount = 30;
        groundLevel = -400;
        
        deltaTime = 0.01;
        lastFrame = 0;
        physicsEnabled = false;
        airplaneController = new AirplaneController();
        camera = camIn;
        listenerPanel.addKeyListener(airplaneController);
        throttle = 0;
        physics = new AirplanePhysics();
        airplaneUpdater = new Timer(30, this);
    }
 
    public void setRenderPanel(RenderingPanel renderingPanel)
    {
        renderingPanel.addMesh(this.getMesh());
    }
 
    public void startPhysics()
    {
        physicsEnabled = true;
        airplaneUpdater.start();
        lastFrame = System.currentTimeMillis();
    }

    public void stopPhysics()
    {
        physicsEnabled = false;
        airplaneUpdater.stop();
    }
 
    public void actionPerformed(ActionEvent e)  
    {
        if (airplaneController.throttleUp && throttle < 1)
            throttle += 0.1*deltaTime;
        if (airplaneController.throttleDown && throttle > 0)
            throttle -= 0.1*deltaTime;
        if (airplaneController.pitchUp)
            physics.addPitchTorque(-pitchSpeed);
        if (airplaneController.pitchDown)
            physics.addPitchTorque(pitchSpeed);
        if (airplaneController.rollLeft)
            physics.addRollTorque(rollSpeed);
        if (airplaneController.rollRight)
            physics.addRollTorque(-rollSpeed);
        if (airplaneController.yawLeft)
            physics.addYawTorque(-yawSpeed);
        if (airplaneController.yawRight)
            physics.addYawTorque(yawSpeed);

        physics.update();
        physics.applyThrust(throttle*maxEnginePower);
        getMesh().refreshLighting();
        deltaTime = (System.currentTimeMillis() - lastFrame)/1000.0;
        lastFrame = System.currentTimeMillis();
    }

    public void reset()
    {
        throttle = 0;
        this.physics.physicsReset();
        getTransform().setPosition(new Vector3());
        getTransform().setPitch(0);
        getTransform().setYaw(0);
        getTransform().setRoll(0);
        getTransform().setPitch(0);
        getTransform().setYaw(0);
        getTransform().setRoll(0);
    }

    public double getAltitude()
    {
        return physics.getPosition().y;
    }

    public double getSpeed()
    {
        return physics.forwardSpeed;
    }

    public EulerAngle orientation()
    {
        return physics.physicsRotation;
    }

    public double getVerticalClimb()
    {
        return physics.velocity.y;
    }
 
    class AirplaneController implements KeyListener
    {
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
            throttleUp = false;
            throttleDown = false;
            pitchUp = false;
            pitchDown = false;
            rollLeft = false;
            rollRight = false;
            yawLeft = false;
            yawRight = false;
            brakes = false;
        }
 
        public void keyPressed(KeyEvent e)  
        {
            if (physicsEnabled)
            {
                int key = e.getKeyCode();
                if (key == FlightSimulator.user.getSettings().throttleUp)
                    throttleUp = true;
                else if (key == FlightSimulator.user.getSettings().throttleDown)
                    throttleDown = true;
                else if (key == FlightSimulator.user.getSettings().pitchUp)
                    pitchUp = true;
                else if (key == FlightSimulator.user.getSettings().pitchDown)
                    pitchDown = true;
                else if (key == FlightSimulator.user.getSettings().rollLeft)
                    rollLeft = true;
                else if (key == FlightSimulator.user.getSettings().rollRight)
                    rollRight = true;
                else if (key == FlightSimulator.user.getSettings().yawLeft)
                    yawLeft = true;
                else if (key == FlightSimulator.user.getSettings().yawRight)
                    yawRight = true;
                else if (key == FlightSimulator.user.getSettings().brakes)
                    brakes = true;
            }
        }
 
        public void keyTyped(KeyEvent e) {}
        public void keyReleased(KeyEvent e)  
        {
            if (physicsEnabled)
            {
                int key = e.getKeyCode();
                if (key == FlightSimulator.user.getSettings().throttleUp)
                    throttleUp = false;
                else if (key == FlightSimulator.user.getSettings().throttleDown)
                    throttleDown = false;
                else if (key == FlightSimulator.user.getSettings().pitchUp)
                    pitchUp = false;
                else if (key == FlightSimulator.user.getSettings().pitchDown)
                    pitchDown = false;
                else if (key == FlightSimulator.user.getSettings().rollLeft)
                    rollLeft = false;
                else if (key == FlightSimulator.user.getSettings().rollRight)
                    rollRight = false;
                else if (key == FlightSimulator.user.getSettings().yawLeft)
                    yawLeft = false;
                else if (key == FlightSimulator.user.getSettings().yawRight)
                    yawRight = false;
                else if (key == FlightSimulator.user.getSettings().brakes)
                    brakes = false;
            }
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
        private boolean grounded; 
 
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
            amt *= Math.min(1, forwardSpeed/20);
            velocityPitch = velocityPitch + amt/mass*deltaTime;
        }
 
        public void addYawTorque(double amt)
        {
            amt *= Math.min(1, forwardSpeed/20);
            velocityYaw = velocityYaw + amt/mass*deltaTime;
        }
 
        public void addRollTorque(double amt)
        {
            amt *= Math.min(1, forwardSpeed/20);
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

        public void applyYawRollEffect()
        {
            if (velocity.x != 0 && velocity.z != 0 && (airplaneController.yawLeft || airplaneController.yawRight))
            {
                double rollAmount = Vector3.dotProduct(Vector3.projectToPlane(velocity, getTransform().getUp()).getNormalized(), getTransform().getRight());
                rollAmount *= rollAmount *rollAmount;
                physicsRotation.z += rollAmount*yawRollEffectAmount*deltaTime;
            }
        }
 
        public void calculateForwardV()
        {
            forwardSpeed = Vector3.dotProduct(velocity, getTransform().getForward());
        }

        public void physicsReset()
        {
            physicsRotation = new EulerAngle();
            physicsPosition = new Vector3();
            velocity = new Vector3();
            velocityPitch = 0;
            velocityRoll = 0;
            velocityYaw = 0;
            updateOrientation();
            updatePosition();
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
                velocity = Vector3.lerp(velocity, Vector3.projectToVector(velocity, getTransform().getForward()), correctionFactor*forwardSpeed*aerodynamicEffectAmount*deltaTime/5);  
                 
                //also rotate the plane towards the direction of movement.
                Vector3 direction = getTransform().transformToLocal(Vector3.lerp(getTransform().getForward(), velocity.getNormalized(), aerodynamicEffectAmount*deltaTime*forwardSpeed*1.5));
                physicsRotation.y += ((direction.x < 0)? -Math.atan(direction.z/direction.x)-Math.PI/2 : Math.PI/2-Math.atan(direction.z/direction.x));
                physicsRotation.x += Math.atan(direction.y/Math.sqrt(direction.x*direction.x + direction.z*direction.z));
            }
        }
 
        public void updatePosition()
        {
            if (Vector3.add(physicsPosition, velocity).y > groundLevel)
            {
                physicsPosition.add(velocity);
                grounded = false;
            }
            else
            {
                physicsPosition.y = groundLevel;
                velocity = new Vector3(velocity.x, 0 , velocity.z);
                physicsPosition.add(velocity);
                grounded = true;
                checkCrash();
            }
            getTransform().setPosition(Vector3.add(getTransform().getPosition(), velocity));
            if (camera.getOrbitCamController() != null)
            {
                camera.getOrbitCamController().updatePosition();
            }
        }

        public void checkCrash()
        {
            double crashFactor = Vector3.dotProduct(velocity, new Vector3(0, -1, 0));
            if 
            (
                (crashFactor > CRASH_THRESHOLD) || (grounded && (Math.abs(physicsRotation.x) > 0.2 || Math.abs(physicsRotation.z) > 0.2))
            )
            {
                reset();
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
            calculateForwardV();
            applyAerodynamicEffect();
            applyYawRollEffect();
            applyGravity();  
            applyLift();  
            applyDrag();
            applyAngularDrag();
            updateOrientation();
            updatePosition();  
        }
    }
 
     
}
 
 
