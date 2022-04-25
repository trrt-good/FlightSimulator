import java.util.ArrayList;

public class Ground extends Mesh
{
    public ArrayList<Triangle> triangles;
    public Vector3[][] verticies;

    public Ground(double height, double gridInterval, int gridLength, int gridWidth)
    {
        super();
        triangles = getTriangles();
        verticies = new Vector3[gridLength][gridWidth];
        for (int x = 0; x < verticies.length; x++)
        {
            for (int z = 0; z < verticies[x].length; z++)
            {
                verticies[z][x] = new Vector3((x-gridLength/2.0)*gridInterval, height, (z-gridWidth/2.0)*gridInterval);
            }
        }
        
        for (int i = 0; i < verticies.length-1; i++)
        {
            for (int j = 0; j < verticies[i].length-1; j++)
            {
                triangles.add(new Triangle(this, verticies[j][i], verticies[j+1][i], verticies[j][i+1]));
            }

            for (int j = 0; j < verticies[i].length-1; j++)
            {
                triangles.add(new Triangle(this, verticies[j][i+1], verticies[j+1][i], verticies[j+1][i+1]));
            }
        }
    }
}
