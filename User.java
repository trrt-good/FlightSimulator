import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.PrintWriter;

public class User 
{
    //static file which contains all the user data 
    public static final File ACCOUNT_DATA_FILE = new File("res/userData.txt");

    //data for each user.
    private double milesFlown;
    private boolean completedTraining; //has this user completed flight training or not?
    private String username;
    private String password;
    private GameSettings userSettings;
    
    //constructor for creating a NEW user. This automatically writes the user into the file.
    public User(String usernameIn, String passwordIn)
    {
        PrintWriter fileWriter = Utils.makeWriter(ACCOUNT_DATA_FILE, true);
        username = usernameIn;
        password = passwordIn;
        milesFlown = 0;
        completedTraining = false;
        userSettings = new GameSettings();
        fileWriter.append("\n--");
        fileWriter.append("\nu " + username);
        fileWriter.append("\np " + password);
        fileWriter.append("\nmilesFlown " + milesFlown);
        fileWriter.append("\ncompletedTraining " + completedTraining);
        fileWriter.append("\nsettings " + userSettings.toString());
        fileWriter.close();
    }

    //private constructor which allows this class to create a custom user object based on information in the user data file.
    private User(String usernameIn, String passwordIn, boolean completedTrainingIn, double milesFlownIn, GameSettings gameSettings)
    {
        username = usernameIn;
        password = passwordIn;
        milesFlown = milesFlownIn;
        completedTraining = completedTrainingIn;
        userSettings = gameSettings;
    }

    //writes the data of the User instance into ACCOUNT_DATA_FILE.
    public void saveData()
    {
        //make the readers/writers
        Scanner fileReader = Utils.makeReader(ACCOUNT_DATA_FILE);
        PrintWriter fileWriter = Utils.makeWriter(ACCOUNT_DATA_FILE, false);
        String line = null;
        while (fileReader.hasNextLine())
        {
            line = fileReader.nextLine();
            fileWriter.println(line);
            if (line.equals("u " + username)) //if it finds the username
            {
                //overwrites whatever is listed under the username with updated data.
                fileReader.nextLine();
                fileWriter.println("p " + password);
                fileReader.nextLine();
                fileWriter.println("milesFlown " + milesFlown);
                fileReader.nextLine();
                fileWriter.println("completedTraining " + completedTraining);
                fileReader.nextLine();
                fileWriter.println("settings " + userSettings.toString());
                break;
            }
        }
        line = "";
        //goes through the rest of the file and simply writes what it reads.
        while (fileReader.hasNextLine())
        {
            line += fileReader.nextLine()+"\n";
            
        }
        fileWriter.write(line);
        fileWriter.close();
    }

    //returns the user object associated with the specified username. This does not return the 
    //origonal user object itself, rather a clone of it using the data in ACCOUNT_DATA_FILE
    //returns null if no user with the specified username was found. 
    public static User getUser(String username)
    {
        User user = null;
        if (username != null)
        {
            Scanner fileReader = Utils.makeReader(ACCOUNT_DATA_FILE);
            String line = "";
            while (fileReader.hasNextLine())
            {
                line = fileReader.nextLine();
                if (line.equals("u " + username))
                {
                    String pass = fileReader.nextLine().substring(2);
                    double miles = Double.parseDouble(fileReader.nextLine().substring(11));
                    boolean completedTraining = Boolean.parseBoolean(fileReader.nextLine().substring(18));
                    //TODO: make this work with conlin conventions
                    StringTokenizer settingsLine = new StringTokenizer(fileReader.nextLine());
                    settingsLine.nextToken();
                    GameSettings newSettings = new GameSettings
                    (
                        Integer.parseInt(settingsLine.nextToken()), Integer.parseInt(settingsLine.nextToken()),
                        Integer.parseInt(settingsLine.nextToken()), Integer.parseInt(settingsLine.nextToken()),
                        Integer.parseInt(settingsLine.nextToken()), Integer.parseInt(settingsLine.nextToken()),
                        Integer.parseInt(settingsLine.nextToken()), Integer.parseInt(settingsLine.nextToken()),
                        Integer.parseInt(settingsLine.nextToken()), Double.parseDouble(settingsLine.nextToken()),
                        Double.parseDouble(settingsLine.nextToken()), Boolean.parseBoolean(settingsLine.nextToken())
                    );
                    user = new User(line.substring(2), pass, completedTraining, miles, newSettings);
                    break;
                }
            }
        }
        return user;
    }

    //#region get/set methods
    public String getPassword()
    {
        return password;
    }

    public String getUsername()
    {
        return username;
    }

    public void setCompletedTraining(boolean value)
    {
        completedTraining = value;
        saveData();
    }

    public boolean getCompletedTraining()
    {
        return completedTraining;
    }

    public GameSettings getSettings()
    {
        return userSettings;
    }

    public void setMilesFlown(double miles)
    {
        milesFlown = miles;
    }
    //#endregion
}
