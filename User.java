import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.PrintWriter;

public class User 
{
    public static final File USERDATA_FILE = new File("res/userData.txt");

    private double milesFlown;
    private boolean completedTraining;

    private static Scanner fileReader;
    private static PrintWriter fileWriter;

    public User()
    {
        milesFlown = 0;
        completedTraining = false;


    }

    public static ArrayList<User> getUsers()
    {

    }

    public static User getUser(int userNum)
    {

    }

    private static Scanner makeReader(File file)
    {
        Scanner scanner = null;
        try
        {
            scanner = new Scanner(file);
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        }
        return scanner;
    }

    private static PrintWriter makeWriter(File file)
    {
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(file);
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        }
        return printWriter;
    }
}
