import java.awt.Color;

public class Ground extends GameObject
{
    public Ground()
    {
        super(new Vector3(5000, -1000, 10000), new EulerAngle(0, 0, 0), 1000, "ground.obj", new Color(50, 168, 82));
        super.backFaceCull = false;
    }
}
