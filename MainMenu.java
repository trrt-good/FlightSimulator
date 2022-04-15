import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel
{
    public MainMenu()
    {
        setLayout(new BorderLayout(0, 10));
        add(makeTitlePanel(), BorderLayout.NORTH);
        add(makeMenuButtonPanel(), BorderLayout.CENTER);
    }

    public JPanel makeTitlePanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 1000, 50));
        setPreferredSize(new Dimension(100, 200));
        JLabel titleLabel = new JLabel("Flight Simulator");
        titleLabel.setFont(new Font(FlightSimulator.FONTSTYLE, Font.BOLD, 80));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);
        panel.setOpaque(false);
        return panel;
    }
    
    public JPanel makeMenuButtonPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 1000, 70));
        Button learnToFlyButton = new Button("Learn to fly", 30, 250, 60);
        learnToFlyButton.addActionListener(new LearnToFlyButtonListener());
        Button freePlayButton = new Button("Free play", 30, 250, 60);
        Button settingsButton = new Button("Settings", 30, 250, 60);
        panel.add(learnToFlyButton);
        panel.add(freePlayButton);
        panel.add(settingsButton);
        panel.setOpaque(false);
        return panel;
    }

    public void paintComponent(Graphics g)
    {
        FlightSimulator.flightSim.paintBackground(this, g);
    }

    public String getName()
    {
        return "MainMenu";
    }

    public class LearnToFlyButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            FlightSimulator.flightSim.showPanel("InstructionPanel");
        }
    }
}
