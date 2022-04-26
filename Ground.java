import java.util.ArrayList;
import java.awt.Color;

public class Ground extends Mesh
{
    private Vector3[][] verticies;

    public Ground(double height, double gridInterval, int gridLength, int gridWidth, double smoothness, double amplitude, Color color)
    {
        super(true, false);
        verticies = new Vector3[gridWidth][gridLength];
        for (int x = 0; x < verticies.length; x++)
        {
            for (int z = 0; z < verticies[x].length; z++)
            {
                verticies[x][z] = new Vector3((x-gridWidth/2.0)*gridInterval, height + SimplexNoise.noise(x/smoothness, z/smoothness)*amplitude, (z-gridLength/2.0)*gridInterval);
            }
        }
        
        for (int i = 0; i < verticies.length-1; i++)
        {
            for (int j = 0; j < verticies[i].length-1; j++)
            {
                super.getTriangles().add(new Triangle(this, verticies[i][j], verticies[i][j+1], verticies[i+1][j], color));
            }

            for (int j = 0; j < verticies[i].length-1; j++)
            {
                super.getTriangles().add(new Triangle(this, verticies[i+1][j], verticies[i][j+1], verticies[i+1][j+1], color));
            }
        }

    }
}
