//Tyler Rose
//4-7-22
//
import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Color;

import javax.swing.JFrame;    
import javax.swing.JPanel;

import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class FlightSimulator
{
    //Final variables
    public static final File RESOURCES_FOLDER = new File("res");
    public static final String FONTSTYLE = "Times";
    public static final Color THEME_COLOR = new Color(50, 110, 184);
    public static final int DEFAULT_WIDTH = 1366;
    public static final int DEFAULT_HEIGHT = 768;

    //store the essential data for all the classes to have acess to. 
    public static FlightSimulator flightSim; //the flight sim object in use.
    public static User user; //the current user 
    
    //the frame that the program displays.
    private JFrame gameFrame;

    //central card layout that switches between major panels.
    private CardLayout mainCardLayout;
    private JPanel mainCardPanel;

    //all the major panels that are held by the card panel. 
    private StartPanel startPanel;
    private AccountPanel loginPanel;
    private MainMenu mainMenu;
    private InstructionPanel instructionPanel;
    private GamePanel gamePanel;
    private SettingsPanel settingsPanel;
    private ControlsPanel controlsPanel;

    //background image 
    private Image backgroundImage;
    
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

        startPanel = new StartPanel();
        loginPanel = new AccountPanel();
        mainMenu = new MainMenu();
        instructionPanel = new InstructionPanel();
        gamePanel = new GamePanel();
        settingsPanel = new SettingsPanel();
        controlsPanel = new ControlsPanel();

        
        addPanelToCards(startPanel, startPanel.getName());
        addPanelToCards(loginPanel, loginPanel.getName());
        addPanelToCards(mainMenu, MainMenu.name());
        addPanelToCards(instructionPanel, InstructionPanel.name());
        addPanelToCards(gamePanel, GamePanel.name());
        addPanelToCards(settingsPanel, SettingsPanel.name());
        addPanelToCards(controlsPanel, ControlsPanel.name());
        showPanel(startPanel.getName());
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
        backgroundImage = makeImage(new File(FlightSimulator.RESOURCES_FOLDER, "airplaneBackground.jpg"));
    }

    //  returns an image with the given file
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

    public void updateWelcomeText()
    {
        mainMenu.setWelcomeText("Hi, " + user.getUsername());
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
    
    //returns background image
    public Image getBackgroundImage()
    {
        return backgroundImage;
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

    //a shortcut for drawing the background image scaled to the given panel size.
    //used by other panels to draw the background as to not write unnecessary code 
    public void paintBackground(JPanel panel, Graphics g)
    {
        g.drawImage(backgroundImage, 0, 0, panel.getWidth(), (int)(((double)panel.getWidth()/backgroundImage.getWidth(panel))*backgroundImage.getHeight(panel)), panel);
    }
}
