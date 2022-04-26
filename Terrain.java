import java.util.ArrayList;
import java.awt.Color;

public class Terrain extends Mesh
{
    private Vector3[][] verticies;

    public Terrain(double height, double waterLevel, double snowLevel, double gridInterval, int gridLength, int gridWidth, double smoothness, double amplitude, Color waterColor, Color mountainColor, Color snowColor)
    {
        super(true, false);
        verticies = new Vector3[gridWidth][gridLength];
        for (int x = 0; x < verticies.length; x++)
        {
            for (int z = 0; z < verticies[x].length; z++)
            {
                verticies[x][z] = new Vector3((x-gridWidth/2.0)*gridInterval, Math.max(height + SimplexNoise.noise(x/smoothness, z/smoothness)*amplitude, height+waterLevel) , (z-gridLength/2.0)*gridInterval);
            }
        }
        
        Triangle tempTriangle = null;
        for (int i = 0; i < verticies.length-1; i++)
        {
            for (int j = 0; j < verticies[i].length-1; j++)
            {
                tempTriangle = new Triangle(this, verticies[i][j], verticies[i][j+1], verticies[i+1][j], mountainColor);
                if (tempTriangle.getCenter().y <= height+waterLevel)
                    tempTriangle.setBaseColor(waterColor);
                else if (tempTriangle.getCenter().y >= height+snowLevel)
                    tempTriangle.setBaseColor(snowColor);
                super.getTriangles().add(tempTriangle);
            }

            for (int j = 0; j < verticies[i].length-1; j++)
            {
                tempTriangle = new Triangle(this, verticies[i+1][j], verticies[i][j+1], verticies[i+1][j+1], mountainColor);
                if (tempTriangle.getCenter().y <= height+waterLevel)
                    tempTriangle.setBaseColor(waterColor);
                else if (tempTriangle.getCenter().y >= height+snowLevel)
                    tempTriangle.setBaseColor(snowColor);
                super.getTriangles().add(tempTriangle);
            }
        }

    }
}
