import javax.swing.JPanel;
import java.awt.Graphics;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import javax.imageio.ImageIO;

//utilities class for creating simple file IO objects such as scanner
//file writer or reading images. 
//Rather than having to re write a bunch of file IO methods in each class 
//where it's used, I just use this
public class Utils 
{
    // returns an image with the given file
    public static Image makeImage(File imageFile)
    {
        Image image = null;
        try
        {
            image = ImageIO.read(imageFile);
        }
        catch (IOException e)
        {
            System.out.println("Could not locate image file");
            e.printStackTrace();
        }
        return image;
    }

    //a shortcut for drawing the background image scaled to the given panel size.
    //used by other panels to draw the background as to not write unnecessary code 
    public static void paintBackground(JPanel panel, Graphics g)
    {
        g.drawImage(FlightSimulator.BACKGROUND_IMAGE, 0, 0, panel.getWidth(), (int)(((double)panel.getWidth()/FlightSimulator.BACKGROUND_IMAGE.getWidth(panel))*FlightSimulator.BACKGROUND_IMAGE.getHeight(panel)), panel);
    }

    //returns a scanner object that made for the inputted file.
    public static Scanner makeReader(File file)
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
    public static PrintWriter makeWriter(File file, boolean append)
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

    //my own implementation of the String.split method 
    public static String[] split(String string, String regex)
    {
        ArrayList<String> words = new ArrayList<>();
        
        int firstIndex = 0;
        int lastIndex = string.indexOf(regex);
        if (lastIndex != -1)
        {
            words.add(string.substring(firstIndex, lastIndex));
            while (lastIndex != -1 && string.substring(lastIndex).contains(regex))
            {
                firstIndex = lastIndex + 1;
                lastIndex = string.indexOf(regex, firstIndex);
                if (lastIndex != -1)
                {
                    String word = string.substring(firstIndex, lastIndex).trim();
                    if (!word.equals(regex) && !word.equals(""))
                        words.add(word);
                }
            }
            words.add(string.substring(firstIndex).trim());
        }
        String[] strings = new String[words.size()];
        for (int i = 0; i < words.size(); i++)
        {
            strings[i] = words.get(i);
        }
        return words.toArray(strings);
    }
}
