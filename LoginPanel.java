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
import javax.swing.SingleSelectionModel;
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

import java.awt.Dimension;

public class LoginPanel extends JPanel
{
    private CardLayout localCardLayout;
    
    private JPanel login;
    private JPanel signUp;

    public LoginPanel()
    {
        localCardLayout = new CardLayout();
        setLayout(localCardLayout);
        login = createLoginPanel();
        signUp = createSignUpPanel();
        
        this.add(signUp, "SignUp");
        this.add(login, "Login");
        localCardLayout.show(this, "Login");
    }
    
    //returns a panel for login, which contains a username and password
    //input area.
    public JPanel createLoginPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 700, 50));
        TextFieldPanel usernamePrompt = new TextFieldPanel("username");
        TextFieldPanel passworldPrompt = new TextFieldPanel("passworld");
        JButton signInButton = new Button("Sign In", 30);
        signInButton.addActionListener(new SignInListener());
        panel.add(usernamePrompt);
        panel.add(passworldPrompt);
        panel.add(signInButton);
        panel.add(makeSignUpButtonPanel());
        panel.setOpaque(false);
        return panel;
    }

    //this returns a jpanel for making an account which contains a 
    //username, password and confirm password areas. 
    public JPanel createSignUpPanel()
    {
        JPanel parentPanel = new JPanel();
        parentPanel.setLayout(new BorderLayout(0, 0));
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 700, 50));
        TextFieldPanel usernamePrompt = new TextFieldPanel("Enter a username");
        TextFieldPanel passworldPrompt = new TextFieldPanel("Set a password");
        TextFieldPanel confirmPassworldPrompt = new TextFieldPanel("Confirm password");
        JButton signUpButton = new Button("Sign Up", 30);
        signUpButton.addActionListener(new SignUpListener());
        panel.add(usernamePrompt);
        panel.add(passworldPrompt);
        panel.add(confirmPassworldPrompt);
        panel.add(signUpButton);
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        backPanel.add(new BackButton(localCardLayout, this, "Login"));
        parentPanel.add(backPanel, BorderLayout.NORTH);
        parentPanel.add(panel, BorderLayout.CENTER);
        parentPanel.setOpaque(false);
        backPanel.setOpaque(false);
        panel.setOpaque(false);
        return parentPanel;
    }

    public void showSignUpPanel()
    {
        localCardLayout.show(this, "SignUp");
    }

    //this returns the panel which has the small button to switch
    //to the signup page
    public JPanel makeSignUpButtonPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 30));
        JButton signUpButton = new Button("Sign Up", 30);
        signUpButton.addActionListener(new SwitchToSignUpPanel());
        JLabel signUpLabel = new JLabel("Don't have an account?");
        signUpLabel.setForeground(Color.WHITE);
        signUpLabel.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 25));
        panel.setPreferredSize(new Dimension(300, 200));
        panel.add(signUpLabel);
        panel.add(signUpButton);
        panel.setOpaque(false);
        return panel;
    }
    
    public String getName()
    {
        return "LoginPanel";
    }
    
    //This class is for each textfield input area, which implements an action
    //listener so I can get the text entered into the fields 
    class TextFieldPanel extends JPanel implements ActionListener
    {
        private String text;
        public TextFieldPanel(String prompt)
        {
            setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));
            JLabel promptLabel = new JLabel(prompt);
            promptLabel.setForeground(Color.WHITE);
            promptLabel.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 25));
            add(promptLabel);
            JTextField textField = new JTextField("", 10);
            textField.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 30));
            textField.addActionListener(this);
            setOpaque(false);
            add(textField);
            setPreferredSize(new Dimension(260, 100));
        }
        
        public String getText()
        {
            return text;
        }
        
        public void actionPerformed(ActionEvent evt)
        {
            text = evt.getActionCommand();
        }
    }

    class SwitchToSignUpPanel implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            showSignUpPanel();
        }
    }

    public void paintComponent(Graphics g)
    {
        FlightSimulator.flightSim.paintBackground(this, g);
    }

    class SignInListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            FlightSimulator.flightSim.showPanel("MainMenu");
            //TODO: FileIO 
            //Check if entered account info is correct
        }
    }

    class SignUpListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) 
        {
            FlightSimulator.flightSim.showPanel("MainMenu");
            //TODO: FileIO
            //Write the entered account info into a file
        }

    }
}
