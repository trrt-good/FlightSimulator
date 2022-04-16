import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import java.io.PrintWriter;

public class User 
{
    public static final File USERDATA_FILE = new File("res/userData.txt");

    private double milesFlown;
    private boolean completedTraining;
    private String username;
    private String password;
    

    public User(String usernameIn, String passwordIn)
    {
        PrintWriter fileWriter = makeWriter(USERDATA_FILE, true);
        username = usernameIn;
        password = passwordIn;
        milesFlown = 0;
        completedTraining = false;
        fileWriter.append("\n--");
        fileWriter.append("\nu " + username);
        fileWriter.append("\np " + password);
        fileWriter.append("\nmilesFlown " + milesFlown);
        fileWriter.append("\ncompletedTraining " + completedTraining);
        fileWriter.close();
    }

    private User(String usernameIn, String passwordIn, boolean completedTrainingIn, double milesFlownIn)
    {
        username = usernameIn;
        password = passwordIn;
        milesFlown = milesFlownIn;
        completedTraining = completedTrainingIn;
    }

    public String getPassword()
    {
        return password;
    }

    public String getUsername()
    {
        return username;
    }

    public void completedTraining()
    {
        completedTraining = true;
        saveData();
    }

    public void setMilesFlown(double miles)
    {
        milesFlown = miles;
    }

    public void saveData()
    {
        Scanner fileReader = makeReader(USERDATA_FILE);
        PrintWriter fileWriter = makeWriter(USERDATA_FILE, false);
        String line = null;
        while (fileReader.hasNextLine())
        {
            line = fileReader.nextLine();
            fileWriter.println(line);
            if (line.equals("u " + username))
            {
                fileReader.nextLine();
                fileWriter.println("p " + password);
                fileReader.nextLine();
                fileWriter.println("milesFlown " + milesFlown);
                fileReader.nextLine();
                fileWriter.println("completedTraining " + completedTraining);
                break;
            }
        }
        line = "";
        while (fileReader.hasNextLine())
        {
            line += fileReader.nextLine()+"\n";
            
        }
        fileWriter.write(line);
        fileWriter.close();
    }

    // public static ArrayList<User> getUsers()
    // {

    // }

    public static User getUser(String username)
    {
        User user = null;
        if (username != null)
        {
            Scanner fileReader = makeReader(USERDATA_FILE);
            String line = "";
            while (fileReader.hasNextLine())
            {
                line = fileReader.nextLine();
                if (line.equals("u " + username))
                {
                    String pass = fileReader.nextLine().substring(2);
                    double miles = Double.parseDouble(fileReader.nextLine().substring(12));
                    boolean completedTraining = Boolean.parseBoolean(fileReader.nextLine().substring(19));
                    user = new User(line.substring(2), pass, completedTraining, miles);
                    //TODO: fix scanner not reading the file
                }
            }
        }
        return user;
    }

    private static Scanner makeReader(File file)
    {
        Scanner scanner = null;
        try
        {
            scanner = new Scanner(file, "UTF-8");
            scanner.hasNextLine();
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
            System.out.println("file not found");
        }
        return scanner;
    }

    private static PrintWriter makeWriter(File file, boolean append)
    {
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new FileWriter(file, append), true);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return printWriter;
    }
}
