import javax.swing.Timer;
 
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
  
public class Airplane extends GameObject implements ActionListener
{
    private final int CRASH_THRESHOLD = 15;

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
    

    private boolean takenOff;
    private boolean landed;
    private double groundLevel;  

    private GamePanel gamePanel; 
     
     
    public Airplane(GamePanel gamePanelIn, Camera camIn)
    {
        super
        (
            "Airplane",
            new Mesh("airplane.obj", "airplaneTexture.png", new Vector3(0, 0, 0), new EulerAngle(0, 0, 0), 2, true, true),
            new Transform(new Vector3(0, 0, 0))
        );
 
        maxEnginePower = 12000; //12000
        pitchSpeed = 20;
        yawSpeed = 20;
        rollSpeed = 30;
        gravity = 60;
        mass = 1000;
        liftCoefficient = 1.5;
        dragCoefficient = 0.2;
        angularDragCoefficient = 1;
        aerodynamicEffectAmount = 0.01;
        yawRollEffectAmount = 30;
        groundLevel = 0;
        
        deltaTime = 0.01;
        lastFrame = 0;
        physicsEnabled = false;
        airplaneController = new AirplaneController();
        camera = camIn;
        gamePanelIn.addKeyListener(airplaneController);
        gamePanel = gamePanelIn;
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
        gamePanel.repaint();
    }

    public void reset()
    {
        gamePanel.pause();
        stopPhysics();
        takenOff = false;
        landed = false;
        getMesh().resetPosition();
        getMesh().rotate(getTransform().toLocalMatrix(), new Vector3());
        
        setTransform(new Transform(new Vector3()));
        throttle = 0;
        physics = new AirplanePhysics();
        startPhysics();
        gamePanel.unpause();
    }

    public double getAltitude()
    {
        return physics.getPosition().y-groundLevel;
    }

    public double getSpeed()
    {
        return physics.velocity.getMagnitude();
    }

    public boolean hasCrashed()
    {
        return physics.crashed();
    }

    public EulerAngle orientation()
    {
        return physics.physicsRotation;
    }

    public boolean hasLanded()
    {
        return physics.landed();
    }

    public double getThrottle()
    {
        return throttle;
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
        private boolean crashed;
 
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
            if (velocity.getSqrMagnitude() > 0.1)
            {
                double altitudeFactor = (2000/(physicsPosition.y-groundLevel+2000));
                altitudeFactor += 0.6;
                altitudeFactor *= altitudeFactor * altitudeFactor;
                velocity = Vector3.subtract(velocity, Vector3.multiply(velocity, Math.min(1, dragCoefficient*velocity.getMagnitude()*deltaTime*altitudeFactor/700)));
                Vector3 verticalDrag = Vector3.projectToVector(velocity, getTransform().getUp());
                velocity = Vector3.subtract(velocity, Vector3.multiply(verticalDrag, Math.min(1, dragCoefficient*verticalDrag.getMagnitude()*deltaTime*altitudeFactor)));
            }
        }

        public void applyYawRollEffect()
        {
            if (velocity.x != 0 && velocity.z != 0 && (airplaneController.yawLeft || airplaneController.yawRight))
            {
                double rollAmount = Vector3.dotProduct(Vector3.projectToPlane(velocity, getTransform().getUp()).getNormalized(), getTransform().getRight());
                rollAmount *= rollAmount *rollAmount;
                velocityRoll += deltaTime*rollAmount*yawRollEffectAmount;
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
            //scale lift by altitude to account for the lack of flaps.
            double altitudeFactor = (2000/(physicsPosition.y-groundLevel+2000));
            altitudeFactor += 1;
            altitudeFactor *= altitudeFactor * altitudeFactor;
            addForce(Vector3.multiply(Vector3.crossProduct(velocity, getTransform().getRight()).getNormalized(), Math.min(2000, forwardSpeed*forwardSpeed*liftCoefficient*deltaTime*altitudeFactor)));
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
                velocity = Vector3.lerp(velocity, Vector3.projectToVector(velocity, getTransform().getForward()), correctionFactor*forwardSpeed*aerodynamicEffectAmount*deltaTime/2);  
                 
                //also rotate the plane towards the direction of movement. (This is innacurate due to linear interpolation with vectors being translated into rotation)
                //Would be better with the use of quaternion multiplication.
                Vector3 direction = getTransform().transformToLocal(Vector3.lerp(getTransform().getForward(), velocity.getNormalized(), correctionFactor*aerodynamicEffectAmount*deltaTime*forwardSpeed));
                physicsRotation.y += ((direction.x < 0)? -Math.atan(direction.z/direction.x)-Math.PI/2 : Math.PI/2-Math.atan(direction.z/direction.x));
                physicsRotation.x += Math.atan(direction.y/Math.sqrt(direction.x*direction.x + direction.z*direction.z))/5;
            }
        }

        public boolean landed()
        {
            return landed;
        }

        public boolean crashed()
        {
            return crashed;
        }
 
        public void updatePosition()
        {
            if (Vector3.add(physicsPosition, velocity).y > groundLevel)
            {
                physicsPosition.add(velocity);
                grounded = false;
                if (physicsPosition.y > 50 && takenOff == false)
                {
                    takenOff = true;
                }
            }
            else
            {
                physicsPosition.y = groundLevel;
                velocity.y = 0;
                physicsPosition.add(velocity);
                grounded = true;
                checkCrash();
            }
            getTransform().setPosition(Vector3.add(getTransform().getPosition(), velocity));
            if (camera.getOrbitCamController() != null && camera.getOrbitCamController() != null)
            {
                camera.getOrbitCamController().updatePosition();
            }
        }

        public void checkCrash()
        {
            double crashFactor = physicsRotation.x*forwardSpeed*physicsRotation.x;
            if 
            (
                (crashFactor > CRASH_THRESHOLD) || (!takenOff && grounded && (Math.abs(physicsRotation.x) > 0.2 || Math.abs(physicsRotation.z) > 0.2))
            )
            {
                System.out.println("crash");
                reset();
            }
            else if (takenOff)
            {
                System.out.println("land");
                landed = true;
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

        public void breaks()
        {
            if (airplaneController.brakes)
                dragCoefficient = 2;
            else
                dragCoefficient = 0.2;
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
            if (physicsEnabled)
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
                breaks();
            }
        }
    }
 
     
}
 
 
