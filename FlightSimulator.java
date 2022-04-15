//Tyler Rose
//4-7-22
//
import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JFrame;    
import javax.swing.JPanel;

import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class FlightSimulator
{
    public static FlightSimulator flightSim;
    public static final File RESOURCES_FOLDER = new File("res");
    public static final String FONTSTYLE = "Times";
    public static final Color THEME_COLOR = new Color(50, 110, 184);

    private int DEFAULT_WIDTH = 1366;
    private int DEFAULT_HEIGHT = 768;

    private JFrame gameFrame;

    private CardLayout mainCardLayout;
    private JPanel mainCardPanel;

    private StartPanel startPanel;
    private LoginPanel loginPanel;
    private MainMenu mainMenu;
    private InstructionPanel instructionPanel;

    private Image backgroundImage;
    
    public static void main(String [] args)
    {
        flightSim = new FlightSimulator();
        flightSim.startGame();
    }
    
    public void startGame()
    {
        startPanel = new StartPanel();
        loginPanel = new LoginPanel();
        mainMenu = new MainMenu();
        instructionPanel = new InstructionPanel();
        createGameFrameAndCardLayout(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        addPanelToCards(startPanel, startPanel.getName());
        addPanelToCards(loginPanel, loginPanel.getName());
        addPanelToCards(mainMenu, mainMenu.getName());
        addPanelToCards(instructionPanel, instructionPanel.getName());
        showPanel(startPanel.getName());
    }
    
    public void createGameFrameAndCardLayout(int width, int height)
    {
        gameFrame = new JFrame();
        gameFrame.setSize(width, height);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setVisible(true);
        mainCardPanel = new JPanel();
        gameFrame.add(mainCardPanel);
        mainCardLayout = new CardLayout();
        mainCardPanel.setLayout(mainCardLayout);
        gameFrame.validate();
        backgroundImage = makeImage(new File(FlightSimulator.RESOURCES_FOLDER, "airplaneBackground.jpg"));
    }

    public Image makeImage(File imageFile)
    {
        Image image = null;
        try
        {
            image = ImageIO.read(imageFile);
        }
        catch (IOException e)
        {
            System.out.println("Could not locate background image file");
            e.printStackTrace();
        }
        return image;
    }
    
    public void showPanel(String name)
    {
        mainCardLayout.show(mainCardPanel, name);
    }
    
    private void addPanelToCards(JPanel panel, String name)
    {
        mainCardPanel.add(panel, name);
    }
    
    
    
    public Image getBackgroundImage()
    {
        return backgroundImage;
    }
    
    public void paintBackground(JPanel panel, Graphics g)
    {
        g.drawImage(backgroundImage, 0, 0, panel.getWidth(), (int)(((double)panel.getWidth()/backgroundImage.getWidth(panel))*backgroundImage.getHeight(panel)), panel);
    }
}
