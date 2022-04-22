import java.awt.Color;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.io.PipedOutputStream;
import java.util.logging.FileHandler;
import java.awt.event.KeyEvent;

import javax.management.relation.Role;
import javax.swing.JPanel;

public class Airplane extends GameObject implements ActionListener
{
    private double groundLevel; 
    private Camera camera;
    private Propeller propeller;
    private Wheels wheels; 
    private double throttle;
    private AirplaneController airplaneController;
    private AirplanePhysics physics;
    private Timer airplaneUpdater;

    private double p;
    private double y;
    private double r;

    private double p1;
    private double y1;
    private double r1;

    private Vector3 offset = new Vector3(0, 31, -53.5);

    public Airplane(JPanel listenerPanel, Camera camIn)
    {
        super(new Vector3(0, 0, 0), new EulerAngle(0, Math.PI/2, 0), 100, "planeBody.obj", new Color(130, 130, 130));
        airplaneController = new AirplaneController();
        groundLevel = -600;
        camera = camIn;
        listenerPanel.addKeyListener(airplaneController);
        throttle = 0;
        propeller = new Propeller();
        wheels = new Wheels();
        physics = new AirplanePhysics();
        airplaneUpdater = new Timer(10, this);
    }

    public void setPitch(double pitch)
    {
        Triangle triangle;
        for (int i = 0; i < getMesh().size(); i++)
        {
            triangle = getMesh().get(i);
            triangle.point1 = Vector3.subtract(triangle.point1, getPosition());
            triangle.point2 = Vector3.subtract(triangle.point2, getPosition());
            triangle.point3 = Vector3.subtract(triangle.point3, getPosition());
            triangle.point1 = Vector3.rotateAroundZaxis(Vector3.rotateAroundYaxis(Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis(Vector3.rotateAroundZaxis(triangle.point1, -r), -y), pitch-p), y), r); 
            triangle.point2 = Vector3.rotateAroundZaxis(Vector3.rotateAroundYaxis(Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis(Vector3.rotateAroundZaxis(triangle.point2, -r), -y), pitch-p), y), r); 
            triangle.point3 = Vector3.rotateAroundZaxis(Vector3.rotateAroundYaxis(Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis(Vector3.rotateAroundZaxis(triangle.point3, -r), -y), pitch-p), y), r); 
            triangle.point1 = Vector3.add(triangle.point1, getPosition());
            triangle.point2 = Vector3.add(triangle.point2, getPosition());
            triangle.point3 = Vector3.add(triangle.point3, getPosition());
        }
        p = pitch;
    }
    
    public void setYaw(double yaw)
    {
        Triangle triangle;
        for (int i = 0; i < getMesh().size(); i++)
        {
            triangle = getMesh().get(i);
            triangle.point1 = Vector3.subtract(triangle.point1, getPosition());
            triangle.point2 = Vector3.subtract(triangle.point2, getPosition());
            triangle.point3 = Vector3.subtract(triangle.point3, getPosition());
            triangle.point1 = Vector3.rotateAroundXaxis(Vector3.rotateAroundZaxis(Vector3.rotateAroundYaxis(Vector3.rotateAroundZaxis(Vector3.rotateAroundXaxis(triangle.point1, -p), -r), yaw-y), r), p); 
            triangle.point2 = Vector3.rotateAroundXaxis(Vector3.rotateAroundZaxis(Vector3.rotateAroundYaxis(Vector3.rotateAroundZaxis(Vector3.rotateAroundXaxis(triangle.point2, -p), -r), yaw-y), r), p); 
            triangle.point3 = Vector3.rotateAroundXaxis(Vector3.rotateAroundZaxis(Vector3.rotateAroundYaxis(Vector3.rotateAroundZaxis(Vector3.rotateAroundXaxis(triangle.point3, -p), -r), yaw-y), r), p); 
            triangle.point1 = Vector3.add(triangle.point1, getPosition());
            triangle.point2 = Vector3.add(triangle.point2, getPosition());
            triangle.point3 = Vector3.add(triangle.point3, getPosition());
        }
        y = yaw;
    }

    public void setRoll(double roll)
    {
        Triangle triangle;
        for (int i = 0; i < getMesh().size(); i++)
        {
            triangle = getMesh().get(i);
            triangle.point1 = Vector3.subtract(triangle.point1, getPosition());
            triangle.point2 = Vector3.subtract(triangle.point2, getPosition());
            triangle.point3 = Vector3.subtract(triangle.point3, getPosition());
            triangle.point1 = Vector3.rotateAroundYaxis(Vector3.rotateAroundXaxis(Vector3.rotateAroundZaxis(Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis(triangle.point1, -y), -p), roll-r), p), y); 
            triangle.point2 = Vector3.rotateAroundYaxis(Vector3.rotateAroundXaxis(Vector3.rotateAroundZaxis(Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis(triangle.point2, -y), -p), roll-r), p), y); 
            triangle.point3 = Vector3.rotateAroundYaxis(Vector3.rotateAroundXaxis(Vector3.rotateAroundZaxis(Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis(triangle.point3, -y), -p), roll-r), p), y); 
            triangle.point1 = Vector3.add(triangle.point1, getPosition());
            triangle.point2 = Vector3.add(triangle.point2, getPosition());
            triangle.point3 = Vector3.add(triangle.point3, getPosition());
        }
        r = roll;
    }
    public void setRenderPanel(RenderingPanel renderingPanel)
    {
        renderingPanel.addGameObject(this);
        renderingPanel.addGameObject(propeller);
        renderingPanel.addGameObject(wheels);
    }

    public void startPhysics()
    {
        airplaneUpdater.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        physics.update();
        physics.applyThrust(throttle);
        this.move(Vector3.subtract(physics.position, getPosition()));
        setPitch(physics.getPitch());
        setYaw(physics.getYaw());
        setRoll(physics.getRoll());
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
        public boolean landingGear;
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
            landingGear = false;
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
            else if (key == FlightSimulator.settings.landingGear)
                landingGear = true;
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
            else if (key == FlightSimulator.settings.landingGear)
                landingGear = false;
            else if (key == FlightSimulator.settings.brakes)
                brakes = false;
        }

        @Override
        public void actionPerformed(ActionEvent e) 
        {
            if (throttleUp && throttle < 100)
                throttle += 0.1;
            if (throttleDown && throttle > 0)
                throttle -= 0.1;
            if (pitchUp)
                physics.addPitchTorque(-0.2);
            if (pitchDown)
                physics.addPitchTorque(0.2);
            if (rollLeft)
                physics.addRollTorque(0.2);
            if (rollRight)
                physics.addRollTorque(-0.2);
            if (yawLeft)
                physics.addYawTorque(-0.2);
            if (yawRight)
                physics.addYawTorque(0.2);
            //TODO: implement landing gear and brakes
        }
    }

    class AirplanePhysics
    {
        //#region not changed by the program: 
        private double gravity;
        private double mass;
        private double liftCoefficient;
        private double dragCoefficient;
        private double angularDragCoefficient;
        //#endregion

        private Vector3 position;
        private Vector3 velocity;
        private double pitch;
        private double yaw;
        private double roll;
        private double velocityPitch;
        private double velocityYaw;
        private double velocityRoll;
        private Vector3 verticalVector; //vector pointing vertically relative to the airplane, which tells us pitch and roll
        private Vector3 forwardVector; //vector pointing in the direction of the nose of the airplane, which tells us yaw and pitch

        public AirplanePhysics()
        {
            gravity = 3;
            mass = 500;
            liftCoefficient = 1.2;
            dragCoefficient = 0.1;
            angularDragCoefficient = 0.05;

            position = new Vector3();
            velocity = new Vector3();
            pitch = 0;
            yaw = 0;
            roll = 0;
            velocityPitch = 0;
            velocityRoll = 0;
            velocityYaw = 0;
            verticalVector = new Vector3(0, 1, 0);
            forwardVector = new Vector3(0, 0, 1);
        }

        public void addForce(Vector3 force)
        {
            velocity.add(Vector3.multiply(force, 1/mass));
        }

        public void addPitchTorque(double amt)
        {
            velocityPitch = velocityPitch + amt/mass;
        }

        public void addYawTorque(double amt)
        {
            velocityYaw = velocityYaw + amt/mass;
        }

        public void addRollTorque(double amt)
        {
            velocityRoll = velocityRoll + amt/mass;
        }

        public void applyDrag()
        {
            velocity = Vector3.subtract(velocity, Vector3.multiply(velocity, dragCoefficient));
        }

        public void applyAngularDrag()
        {
            velocityPitch = velocityPitch - velocityPitch*angularDragCoefficient;
            velocityYaw = velocityYaw - velocityYaw*angularDragCoefficient;
            velocityRoll = velocityRoll - velocityRoll*angularDragCoefficient;
        }

        public void applyGravity()
        {
            velocity.add(new Vector3(0, -gravity, 0));
        }

        public void applyLift()
        {
            Vector3 wingsHorizontalMotion = Vector3.projectToPlane(velocity, verticalVector);
            System.out.println(wingsHorizontalMotion);
            System.out.println(forwardVector);
            addForce(Vector3.multiply(verticalVector, wingsHorizontalMotion.getSqrMagnitude()*liftCoefficient));
        }

        public void updatePosition()
        {
            if (Vector3.add(position, velocity).y > groundLevel)
                position.add(velocity);
            else
            {
                position.y = groundLevel;
                velocity = new Vector3(velocity.x, 0 , velocity.z);
                position.add(velocity);
            }
            if (camera.getOrbitCamController() != null)
            {
                camera.getOrbitCamController().updatePosition();
            }
        }

        public void updateOrientation()
        {
            pitch += velocityPitch;
            yaw += velocityYaw;
            roll += velocityRoll;
            forwardVector = Vector3.angleToVector(yaw, pitch);
            verticalVector = Vector3.crossProduct(forwardVector, Vector3.angleToVector(yaw+Math.PI/2, pitch));
        }

        public void applyThrust(double amount)
        {
            addForce(Vector3.multiply(forwardVector.getNormalized(), amount*20));
        }

        public Vector3 getPosition()
        {
            return position;
        }

        public double getPitch()
        {
            return pitch;
        }

        public double getYaw()
        {
            return yaw;
        }

        public double getRoll()
        {
            return roll;
        }

        public void update()
        {
            applyLift();
            applyGravity();
            applyDrag();
            applyAngularDrag();
            updateOrientation();
            updatePosition();
        }
    }

    
}


