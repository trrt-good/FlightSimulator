import java.awt.Color;

public class Ground extends GameObject
{
    public Ground()
    {
        super(new Vector3(0, -500, 0), new EulerAngle(0, 0, 0), 500, "ground.obj", new Color(50, 168, 82));
    }
}
