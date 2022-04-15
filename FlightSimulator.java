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

public class FlightSimulator
{
    private static FlightSimulator flightSim;

    private int DEFAULT_WIDTH = 1366;
    private int DEFAULT_HEIGHT = 768;

    private JFrame gameFrame;

    private CardLayout mainCardLayout;
    private JPanel mainCardPanel;

    private StartPanel startPanel;
    private LoginPanel loginPanel;
    
    public static void main(String [] args)
    {
        flightSim = new FlightSimulator();
        flightSim.startGame();
    }
    
    public void startGame()
    {
        startPanel = new StartPanel("StartPanel");
        loginPanel = new LoginPanel(this, "LoginPanel");
        createGameFrameAndCardLayout(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        addPanelToCards(startPanel, startPanel.getName());
        addPanelToCards(loginPanel, loginPanel.getName());
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
    }
    
    public void showPanel(String name)
    {
        mainCardLayout.show(mainCardPanel, name);
    }
    
    private void addPanelToCards(JPanel panel, String name)
    {
        mainCardPanel.add(panel, name);
    }
    
    class StartPanel extends JPanel implements ActionListener
    {
        private String name;
        private Image backgroundImage;
        public StartPanel(String nameIn)
        {
            setLayout(null);
            JLabel titleLabel = new JLabel("Flight Simulator");
            titleLabel.setFont(new Font("Serif", Font.BOLD, 100));
            titleLabel.setBackground(new Color(255, 150, 150));
            add(titleLabel);
            titleLabel.setBounds(50, 50, 1000, 130);
            name = nameIn;
            
            JButton playButton = new JButton("PLAY");
            playButton.setFont(new Font("Serif", Font.BOLD, 50));
            playButton.setBackground(new Color(255, 100, 100));
            playButton.addActionListener(this);
            add(playButton);
            playButton.setBounds(1000, 500, 300, 100);
        }
        
        public String getName()
        {
            return name;
        }

        @Override
        public void actionPerformed(ActionEvent e) 
        {
            showPanel(loginPanel.getName());
        }

        public void paintComponent(Graphics g)
        {
            
        }
    }
    
    
}
