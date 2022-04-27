import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

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
    
    //constructor for creating a NEW user. This automatically writes the user into the file.
    public User(String usernameIn, String passwordIn)
    {
        PrintWriter fileWriter = makeWriter(ACCOUNT_DATA_FILE, true);
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

    //private constructor which allows this class to create a custom user object based on information in the user data file.
    private User(String usernameIn, String passwordIn, boolean completedTrainingIn, double milesFlownIn)
    {
        username = usernameIn;
        password = passwordIn;
        milesFlown = milesFlownIn;
        completedTraining = completedTrainingIn;
    }

    //writes the data of the User instance into ACCOUNT_DATA_FILE.
    public void saveData()
    {
        //make the readers/writers
        Scanner fileReader = makeReader(ACCOUNT_DATA_FILE);
        PrintWriter fileWriter = makeWriter(ACCOUNT_DATA_FILE, false);
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
            Scanner fileReader = makeReader(ACCOUNT_DATA_FILE);
            String line = "";
            while (fileReader.hasNextLine())
            {
                line = fileReader.nextLine();
                if (line.equals("u " + username))
                {
                    String pass = fileReader.nextLine().substring(2);
                    double miles = Double.parseDouble(fileReader.nextLine().substring(11));
                    boolean completedTraining = Boolean.parseBoolean(fileReader.nextLine().substring(18));
                    user = new User(line.substring(2), pass, completedTraining, miles);
                    break;
                }
            }
        }
        return user;
    }

    //returns a scanner object that made for the inputted file.
    private static Scanner makeReader(File file)
    {
        Scanner scanner = null;
        try
        {
            scanner = new Scanner(file);
            scanner.hasNextLine();
            //^ called to mitigate bug which causes the scanner to think the file is empty
        } 
        catch (FileNotFoundException e) 
        {
            System.err.println("ERROR at: User/makeReader() method:\n\tCould not find user data file: " + file.getAbsolutePath());
        }
        return scanner;
    }

    //returns a PrintWriter object for the specified file, and with the option to append or not
    private static PrintWriter makeWriter(File file, boolean append)
    {
        PrintWriter printWriter = null;
        try 
        {
            printWriter = new PrintWriter(new FileWriter(file, append), true);
        } 
        catch (IOException e) 
        {
            System.err.println("ERROR at: User/makeWriter() method:\n\tIOException related to making a PrintWriter object for file: " + file.getAbsolutePath());
        }
        return printWriter;
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

    public void setMilesFlown(double miles)
    {
        milesFlown = miles;
    }
    //#endregion
}
