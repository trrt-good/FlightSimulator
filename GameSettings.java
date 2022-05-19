import java.awt.event.KeyEvent;

public class GameSettings 
{
    //default settings
    private static final int DEFAULT_THROTTLE_UP = KeyEvent.VK_UP;
    private static final int DEFAULT_THROTTLE_DOWN = KeyEvent.VK_DOWN;
    private static final int DEFAULT_PITCH_UP = KeyEvent.VK_S; 
    private static final int DEFAULT_PITCH_DOWN = KeyEvent.VK_W; 
    private static final int DEFAULT_ROLL_LEFT = KeyEvent.VK_A;
    private static final int DEFAULT_ROLL_RIGHT = KeyEvent.VK_D;
    private static final int DEFAULT_YAW_LEFT = KeyEvent.VK_Q;
    private static final int DEFAULT_YAW_RIGHT = KeyEvent.VK_E;
    private static final int DEFAULT_BRAKES = KeyEvent.VK_B;

    private static final double DEFAULT_FOV = 60;
    private static final double DEFAULT_SENSITIVITY = 10;
    private static final boolean DEFAULT_FOG = true;

    //user's settings 
    public int throttleUp;
    public int throttleDown;
    public int pitchUp;
    public int pitchDown;
    public int rollLeft;
    public int rollRight;
    public int yawLeft;
    public int yawRight;
    public int brakes;

    public double fov;
    public double sensitivity;
    public boolean fog;

    public GameSettings()
    {
        restoreDefaults();
    }

    public GameSettings(int thUp, int thDown, int pUp, int pDown, int rLeft, int rRight, int yLeft, int yRight, int b, double fv, double sens, boolean fg)
    {
        throttleUp = thUp;
        throttleDown = thDown;
        pitchUp = pUp;
        pitchDown = pDown;
        rollLeft = rLeft;
        rollRight = rRight;
        yawLeft = yLeft;
        yawRight = yRight;
        brakes = b;
        fov = fv;
        sensitivity = sens;
        fog = fg;
    }

    //restores to defaults. 
    public void restoreDefaults()
    {
        throttleUp = DEFAULT_THROTTLE_UP;
        throttleDown = DEFAULT_THROTTLE_DOWN;
        pitchUp = DEFAULT_PITCH_UP;
        pitchDown = DEFAULT_PITCH_DOWN;
        rollLeft = DEFAULT_ROLL_LEFT;
        rollRight = DEFAULT_ROLL_RIGHT;
        yawLeft = DEFAULT_YAW_LEFT;
        yawRight = DEFAULT_YAW_RIGHT;
        brakes = DEFAULT_BRAKES;
        
        fov = DEFAULT_FOV;
        sensitivity = DEFAULT_SENSITIVITY;
        fog = DEFAULT_FOG;
    }

    //formats the settings into a string for writing to the file
    public String toString()
    {
        return String.format(" %d %d %d %d %d %d %d %d %d %f %f %b", throttleUp, throttleDown, pitchUp, pitchDown, rollLeft, rollRight, yawLeft, yawRight, brakes, fov, sensitivity, fog);
    }
}