import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel
{
    private JLabel welcomeLabel;
    //This constructs the main menu panel  
    public MainMenu()
    {
        setLayout(new BorderLayout(0, 10));
        add(makeTitlePanel(), BorderLayout.NORTH);
        add(makeLowerButonPanel(), BorderLayout.SOUTH);
        add(makeMenuButtonPanel(), BorderLayout.CENTER);
    }

    //returns a Jpanel which contains the title "Flight Simulator"  
    public JPanel makeTitlePanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 1000, 50));
        JLabel titleLabel = new JLabel("Flight Simulator");
        titleLabel.setFont(new Font(FlightSimulator.FONTSTYLE, Font.BOLD, 80));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);
        panel.setOpaque(false);
        return panel;
    }

    //creates the panel on the bottom of the page with the welcome text and an exit button
    public JPanel makeLowerButonPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 50, 30));
        Button exitButton = new Button("EXIT", 30, 200, 50);
        exitButton.setBackground(new Color(184, 71, 42));
        exitButton.addActionListener(new ExitButtonListener());
        welcomeLabel = new JLabel("Hi, ");
        welcomeLabel.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 30));
        welcomeLabel.setForeground(Color.WHITE);
        panel.add(welcomeLabel);
        panel.add(exitButton);
        panel.setOpaque(false);
        return panel;
    }

    //sets the text of the welcome label.
    public void setWelcomeText(String text)
    {
        if (welcomeLabel != null)
            welcomeLabel.setText(text);
    }
    
    //returns a JPanel which has all the menu buttons in a flow layout.  
    public JPanel makeMenuButtonPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 1000, 50));
        Button learnToFlyButton = new Button("Learn to fly", 30, 250, 60);
        learnToFlyButton.addActionListener(InstructionPanel.getInstructionPanelSwitcher());
        Button freePlayButton = new Button("Free play", 30, 250, 60);
        freePlayButton.addActionListener(GamePanel.getGamePanelSwitcher());
        Button settingsButton = new Button("Settings", 30, 250, 60);
        settingsButton.addActionListener(SettingsPanel.getSettingsSwitcher());
        panel.add(learnToFlyButton);
        panel.add(freePlayButton);
        panel.add(settingsButton);
        panel.setOpaque(false);
        return panel;
    }

    //sets the background
    public void paintComponent(Graphics g)
    {
        FlightSimulator.flightSim.paintBackground(this, g);
    }

    public static String name()
    {
        return "MainMenu";
    }

    public class ExitButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            System.exit(1);
        }
    }

    public static SwitchToMainMenuPanelListener getMainMenuPanelSwitcher()
    {
        return new SwitchToMainMenuPanelListener();
    }

    static class SwitchToMainMenuPanelListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e) 
        {
            FlightSimulator.flightSim.showPanel(name());
        }
    }
}
