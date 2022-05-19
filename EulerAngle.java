//all rotations using euler angles are intrinsic and applied in order y-x-z.  
public class EulerAngle 
{
    //because there are no non-valid values for the x y and z components
    //as well as the fact that these components are primitives and have no need
    //for having changing restrictions (like to avoid null pointers), using public
    //has no impact on code. 
    public double y; //yaw
    public double x; //pitch
    public double z; //roll

    //no-arg constructor default 0, 0, 0
    public EulerAngle()
    {
        x = 0;
        y = 0;
        z = 0;
    }

    public EulerAngle(double xIn, double yIn, double zIn)
    {
        x = xIn;
        y = yIn;
        z = zIn;
    }

    //formats into string, similar to Vector3s 
    public String toString()
    {
        return new String(String.format("[%.2f, %.2f, %.2f]", x, y, z));
    }

    //pubtracts two euler angles angle1-angle2
    public static EulerAngle subtract(EulerAngle angle1, EulerAngle angle2)
    {
        return new EulerAngle(angle1.x-angle2.x, angle1.y-angle2.y, angle1.z-angle2.z);
    }
}
