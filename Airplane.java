import javax.swing.Timer;
 
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
  
/**
 * an extension of the {@link GameObject} class. Represents a controllable airplane in 3d
 * with physics. 
 */
public class Airplane extends GameObject implements ActionListener
{
    //threshold for the airplane crashing. Lower threshold means easier to creash
    private final int CRASH_THRESHOLD = 10; 

    //the camera that is focused on this GameObject
    private Camera camera;
    private AirplaneController airplaneController; //the interface for user input
    private AirplanePhysics physics; //the physics object that controls the plane
    private Timer airplaneUpdater; //the timer that updates the physics
    private double deltaTime; //the time in seconds since last update. 
    //Used to balance physics simulation speed across devices with different preformance levels 
    private double lastFrame; //used to calculate deltatime
    private boolean physicsEnabled; //is physics enabled?
 
    private double throttle; //the throttle of the plane from 0-1
    private double maxEnginePower; //the maximum power the engine can produce when throttle is 1
    private double pitchSpeed; //sensitivity of the pitch user control
    private double yawSpeed; //sensitivty of the yaw user control
    private double rollSpeed; //sensitivity of the roll user control
    private double gravity; //the force of gravity on the airplane
    private double mass; //the mass of the airplane
    private double liftCoefficient; //the strenth of lift
    private double dragCoefficient; //strenth of drag
    private double angularDragCoefficient; //strenth of rotational drag.
    private double yawRollEffectAmount; //the magnitude of the effect of rolling when the airplane yaws
    private double aerodynamicEffectAmount; //the strenth of the effect that straightens out the airplane
    
    private boolean takenOff; //has the airplane taken off yet?
    private boolean landed; //has the airplane landed yet?
    private double groundLevel;  //the level of ground as a y coordinate

    private GamePanel gamePanel; //the GamePanel object that manages the game
     
    /**
     * Constructs an airplane object with default physics values
     * @param gamePanelIn the GamePanel object that created the airplane
     * @param camIn the camera that is linked to this GameObject
     */
    public Airplane(GamePanel gamePanelIn, Camera camIn)
    {
        super
        (
            "Airplane",
            new Mesh("airplane.obj", "airplaneTexture.png", new Vector3(0, 0, 0), new EulerAngle(0, 0, 0), 2, true, true),
            new Transform(new Vector3(0, 0, 0))
        );
 
        //innitialize fields to their default physics values 
        maxEnginePower = 20000; 
        pitchSpeed = 30;
        yawSpeed = 20;
        rollSpeed = 40;
        gravity = 65;
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
 
    /**
     * called by the GamePanel which tells this object what the rendering panel is,
     * and automatically adds itself to the panel.
     * @param renderingPanel the RenderingPanel object
     */
    public void setRenderPanel(RenderingPanel renderingPanel)
    {
        renderingPanel.addMesh(this.getMesh());
    }
 
    /**
     * starts physics simulation by starting the physics updater timer
     */
    public void startPhysics()
    {
        physicsEnabled = true;
        airplaneUpdater.start();
        lastFrame = System.currentTimeMillis();
    }

    /**
     * stops the physics simulation by stopping the timer and changing the boolean value
     * used for pausing
     */
    public void stopPhysics()
    {
        physicsEnabled = false;
        airplaneUpdater.stop();
    }
 
    /**
     * called by the timer object and preforms physics updates 
     * as well as enabling user input
     */
    public void actionPerformed(ActionEvent e)  
    {
        //checks each inpput of the airplane controller 
        //and applies the corresponding changes to the physics 
        //object
        if (airplaneController.throttleUp && throttle < 1)
            throttle += 1*deltaTime;
        if (airplaneController.throttleDown && throttle > 0)
            throttle -= 1*deltaTime;
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

        //update physics
        physics.update();
        physics.applyThrust(throttle*maxEnginePower);
        getMesh().refreshLighting();

        //calculate delta time
        deltaTime = (System.currentTimeMillis() - lastFrame)/1000.0;
        lastFrame = System.currentTimeMillis();
        gamePanel.repaint();
    }

    /**
     * resets the plane to (0, 0, 0)
     */
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

    /**
     * returns the altitude of the plane relative to ground level
     */
    public double getAltitude()
    {
        return physics.getPosition().y-groundLevel;
    }

    //returns the speed of the plane
    public double getSpeed()
    {
        return physics.velocity.getMagnitude();
    }

    //returns the orientation for flight dials
    public EulerAngle orientation()
    {
        return physics.physicsRotation;
    }

    //returns the throttle value for the flight dials
    public double getThrottle()
    {
        return throttle;
    }

    //returns vertical speed for flight dials
    public double getVerticalClimb()
    {
        return physics.velocity.y;
    }
 

    class AirplaneController implements KeyListener
    {
        //boolean for each user control. 
        //true means that the user is pressing it
        //and false means that the user isn't.
        private boolean throttleUp;
        private boolean throttleDown;
        private boolean pitchUp;
        private boolean pitchDown;
        private boolean rollLeft;
        private boolean rollRight;
        private boolean yawLeft;
        private boolean yawRight;
        private boolean brakes;
 
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
 
        //checks each keybind based on user settings and sets the 
        //corresponding value accordingly
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

        //checks each keybind based on user settings and sets the 
        //corresponding value accordingly
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
 
    /**
     * Preforms all physics calculations for the airplane. It contains seperated
     * positions and rotations from the actual GameObject's Transform object.
     */
    class AirplanePhysics
    {
        private Vector3 physicsPosition; //the simulated position of the airplane physics.
        //physics calcuations are done to the physicsPosition/rotation before being applied 
        //to the Transform of the actual GameObject
        private EulerAngle physicsRotation;//the simulated rotation
        private double forwardSpeed;//the speed the plane is going strictly in the local z axis.
        //used for lift, drag and crashing calculations
        private Vector3 velocity; // the velocty of the plane as a Vector3
        private double velocityPitch; //the velocty of the plane's pitch. Basically how fast it's pitching
        private double velocityYaw; //how fast the plane is yawing
        private double velocityRoll; //how fast the plane is rolling
        private boolean grounded; //is the plane touching the ground?
 
        //creates an airplanePhysics object with default physics values
        public AirplanePhysics()
        {
            physicsRotation = new EulerAngle();
            physicsPosition = new Vector3();
            velocity = new Vector3();
            velocityPitch = 0;
            velocityRoll = 0;
            velocityYaw = 0;
        }
        
        //applies a force as a Vector3 by dividing by mass and adding that to the velocity.
        public void addForce(Vector3 force)
        {
            velocity.add(Vector3.multiply(force, 1/mass));
        }
 
        //applies a torque around the local x axis by amt again by dividing by mass. 
        public void addPitchTorque(double amt)
        {
            amt *= Math.min(1, forwardSpeed/20);
            velocityPitch = velocityPitch + amt/mass*deltaTime;
        }
 
        //applies a torque around the local y axis by amt again by dividing by mass. 
        public void addYawTorque(double amt)
        {
            amt *= Math.min(1, forwardSpeed/20);
            velocityYaw = velocityYaw + amt/mass*deltaTime;
        }
 
        //applies a torque around the local z axis by amt again by dividing by mass. 
        public void addRollTorque(double amt)
        {
            amt *= Math.min(1, forwardSpeed/20);
            velocityRoll = velocityRoll + amt/mass*deltaTime;
        }
 
        //applies drag to the airplane by decreasing the velocty by an amount based on altitude, speed and the drag
        //coefficient. The math used is not realistic, but it's looks fine in the simulation
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

        //applies the effect that causes an airplane to roll in the direction of a yaw. This is because when yawing, the 
        //outer wing moves faster, increasing it's lift and causing a roll in the same direction. This method tries to 
        //replicate that artificially.
        public void applyYawRollEffect()
        {
            if (velocity.x != 0 && velocity.z != 0 && (airplaneController.yawLeft || airplaneController.yawRight))
            {
                double rollAmount = Vector3.dotProduct(Vector3.projectToPlane(velocity, getTransform().getUp()).getNormalized(), getTransform().getRight());
                rollAmount *= rollAmount *rollAmount;
                velocityRoll += deltaTime*rollAmount*yawRollEffectAmount;
            }
        }
 
        //calculates the forward velocty by getting the magnitude of the projection onto the local z axis of the transform.
        public void calculateForwardV()
        {
            forwardSpeed = Vector3.dotProduct(velocity, getTransform().getForward());
        }

        //resets the physics entirely. This is caused by the main Airplane class when it is reset.
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
 
        //applies angular drag by decreasing all the rotational velocities by an amount based off
        //the angular drag coefficient. 
        public void applyAngularDrag()
        {
            velocityPitch = velocityPitch - velocityPitch*angularDragCoefficient*deltaTime;
            velocityYaw = velocityYaw - velocityYaw*angularDragCoefficient*deltaTime;
            velocityRoll = velocityRoll - velocityRoll*angularDragCoefficient*deltaTime;
        }
 
        //applies gravity by adding a force with a magnitude of the gravity strenth
        public void applyGravity()
        {
            velocity.add(new Vector3(0, -gravity*deltaTime, 0));
        }
 
        //applies lift to the velocity by adding a vector perpendicular to the veloicty and the local x-axis of the transoform.
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
 
        //applies the position of the simulated physicsPosition to the actual Transform of the GameObject.
        //Makes sure that the position cannot fall below the ground level. However, if it approaches the ground level
        //it checks for a crash.  
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
            }
            getTransform().setPosition(Vector3.add(getTransform().getPosition(), velocity));
            if (camera.getOrbitCamController() != null && camera.getOrbitCamController() != null)
            {
                camera.getOrbitCamController().updatePosition();
            }
        }
 
        //applies the velocities to the physics rotation then applies that to the 
        //game object's transform (this is actually slightly bugged because of the use of 
        //euler angles. Quaternions would solve this problem but it's a lot of work)
        public void updateOrientation()
        {
            physicsRotation.x += velocityPitch;
            physicsRotation.y += velocityYaw;
            physicsRotation.z += velocityRoll;
 
            getTransform().setPitch(physicsRotation.x);
            getTransform().setYaw(physicsRotation.y);
            getTransform().setRoll(physicsRotation.z);
 
        }

        //checks if the user has the brakes button down, if so
        //it greatly increases the drag coefficient 
        public void breaks()
        {
            if (airplaneController.brakes)
                dragCoefficient = 1;
            else
                dragCoefficient = 0.2;
        }
 
        //applies a force in the forward direction of the plane based on the amount param
        public void applyThrust(double amount)
        {
            addForce(Vector3.multiply(getTransform().getForward(), amount*deltaTime));
        }
 
        //returns the simulated position
        public Vector3 getPosition()
        {
            return physicsPosition;
        }
 
        //calls all the methods for updating and calculating physics.
        //note that the order matters, so be careful before making changes.
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
 
 
