//Tyler Rose
//4-7-22
//FlightSimulator.java
import java.awt.CardLayout;
import java.awt.Color;

import javax.swing.JFrame;    
import javax.swing.JPanel;

import java.awt.Image;
import java.io.File;

//The main class for the FlightSimulator game. This creates the main frame
//central card layout and card holder. It also contains some public finals which
//are accessed throughout the project.
public class FlightSimulator
{
    //Final variables
    public static final File RESOURCES_FOLDER = new File("res");
    public static final String FONTSTYLE = "Times";
    public static final Color THEME_COLOR = new Color(50, 110, 184);
    public static final int DEFAULT_WIDTH = 1366;
    public static final int DEFAULT_HEIGHT = 768;

    public static final Image BACKGROUND_IMAGE = Utils.makeImage(new File(RESOURCES_FOLDER, "airplaneBackground.jpg"));

    //store the essential data for all the classes to have acess to. 
    public static FlightSimulator flightSim; //the flight sim object in use.
    public static User user = User.getUser("user"); //the current user 
    
    //the frame that the program displays.
    private JFrame gameFrame;    
 
    //central card layout that switches between major panels.
    private CardLayout mainCardLayout; 
    private JPanel mainCardPanel; 
 
    //all the major panels that are held by the card panel. 
    private GamePanel gamePanel;
    private SettingsPanel settingsPanel;
    private ControlsPanel controlsPanel;


    
    public static void main(String [] args)
    {   
        flightSim = new FlightSimulator();
        flightSim.startGame();
    }
    
    //  This method starts the game by creating the nessecary panels and adding them to the 
    //  central card layout
    public void startGame()
    {
        createGameFrameAndCardLayout(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        gamePanel = new GamePanel();
        settingsPanel = new SettingsPanel();
        controlsPanel = new ControlsPanel();

        addPanelToCards(gamePanel, GamePanel.name());
        addPanelToCards(settingsPanel, SettingsPanel.name());
        addPanelToCards(controlsPanel, ControlsPanel.name());
    }

    //  This method creates the game frame and creates and adds the card layout to the game frame
    public void createGameFrameAndCardLayout(int width, int height)
    {
        gameFrame = new JFrame();
        gameFrame.setSize(width, height);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setVisible(true);
        gameFrame.setResizable(false);
        mainCardPanel = new JPanel();
        gameFrame.add(mainCardPanel);
        mainCardLayout = new CardLayout();
        mainCardPanel.setLayout(mainCardLayout);
        gameFrame.validate();
    }
    
    //  shows a given panel name using the card layout
    public void showPanel(String name)
    {
        mainCardLayout.show(mainCardPanel, name);
    }
    
    //adds a given panel to the card layout
    private void addPanelToCards(JPanel panel, String name)
    {
        mainCardPanel.add(panel, name);
    }

    public CardLayout getCardLayout()
    {
        return mainCardLayout;
    }

    public JPanel getCardPanelHolder()
    {
        return mainCardPanel;
    }

    public GamePanel getGamePanel()
    {
        return gamePanel;
    }
    
    public SettingsPanel getSettingsPanel()
    {
        return settingsPanel;
    }

    public ControlsPanel getControlsPanel()
    {
        return controlsPanel;
    }

    
}
