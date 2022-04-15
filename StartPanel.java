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

class StartPanel extends JPanel implements ActionListener
{
    //Creates the start panel with a null layout. This contains the title and a play 
    //button which starts the rest of the game
    public StartPanel()
    {
        setLayout(null);
        JLabel titleLabel = new JLabel("Flight Simulator");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font(FlightSimulator.FONTSTYLE, Font.BOLD, 100));
        titleLabel.setBackground(new Color(255, 150, 150));
        add(titleLabel);
        titleLabel.setBounds(50, 50, 1000, 130);

        JButton playButton = new JButton("PLAY");
        playButton.setFont(new Font(FlightSimulator.FONTSTYLE, Font.BOLD, 100));
        playButton.setForeground(Color.WHITE);
        playButton.setContentAreaFilled(false);
        playButton.setBorderPainted(false);
        playButton.setFocusPainted(false);
        playButton.addActionListener(this);
        add(playButton);
        playButton.setBounds(800, 500, 600, 200);
    }

    public String getName()
    {
        return "StartPanel";
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        FlightSimulator.flightSim.showPanel("LoginPanel");
    }

    public void paintComponent(Graphics g)
    {
        FlightSimulator.flightSim.paintBackground(this, g);    
    }
}