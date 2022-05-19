/**
 * Object which holds a mesh and a transform. This is used for any non stationary
 * 3d object.
 */
public class GameObject 
{  
    //the mesh of the object (a bunch of triangles)
    private Mesh mesh;

    //the transform of the object, which handles position and rotation.
    private Transform transform;

    //name
    private String name;

    public GameObject(String nameIn, Mesh meshIn, Transform transformIn)
    {
        mesh = meshIn;
        name = nameIn;
        transformIn.setGameObject(this);
        transform = transformIn;
    }
    
    //#region getter methods 
    public Mesh getMesh()
    {
        return mesh;
    }

    //returns the transform object
    public Transform getTransform()
    {
        return transform;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setTransform(Transform transformIn)
    {
        transform = transformIn;
        transform.setGameObject(this);
    }
    //#endregion
}
