import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.Dimension;

public class AccountPanel extends JPanel
{
    private CardLayout localCardLayout;
    
    private JPanel login;
    private JPanel signUp;
    private AccountPanel accountPanel;

    private JPanel backPanel;

    public AccountPanel()
    {
        localCardLayout = new CardLayout();
        accountPanel = this;
        setLayout(localCardLayout);
        login = new LoginPanel();
        signUp = new SignUpPanel();
        
        this.add(signUp, "SignUp");
        this.add(login, "Login");
        localCardLayout.show(this, "Login");
    }

    public class LoginPanel extends JPanel
    {
        private TextFieldPanel usernamePrompt;
        private TextFieldPanel passworldPrompt;
        public LoginPanel()
        {
            setLayout(new FlowLayout(FlowLayout.CENTER, 700, 50));
            usernamePrompt = new TextFieldPanel("username");
            passworldPrompt = new TextFieldPanel("passworld");

            JButton signInButton = new Button("Sign In", 30);
            signInButton.addActionListener(new SignInListener());
            add(usernamePrompt);
            add(passworldPrompt);
            add(signInButton);
            add(makeSignUpButtonPanel());
            setOpaque(false);
        }

        //this returns the panel which has the small button to switch
        //to the signup page
        private JPanel makeSignUpButtonPanel()
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

        class SignInListener implements ActionListener
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                if (usernamePrompt.getText() != null && passworldPrompt.getText() != null)
                {
                    User user = User.getUser(usernamePrompt.getText());
                    if (user != null)
                    {
                        if (user.getPassword().equals(passworldPrompt.getText()))
                        {
                            FlightSimulator.user = user;
                            FlightSimulator.flightSim.showPanel("MainMenu");
                            FlightSimulator.flightSim.updateWelcomeText();
                        }
                        else
                        {
                            System.out.println("incorrect password");
                        }
                    }
                    else
                        System.out.println("could not find user");
                }
                else
                {
                    System.out.println("Re-enter info");
                }
            }
        }
    }

    class SignUpPanel extends JPanel
    {
        private TextFieldPanel usernamePrompt;
        private TextFieldPanel passworldPrompt;
        private TextFieldPanel confirmPassworldPrompt;
        public SignUpPanel()
        {   
            setLayout(new BorderLayout(0, 0));
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 700, 50));
            usernamePrompt = new TextFieldPanel("Enter a username");
            passworldPrompt = new TextFieldPanel("Set a password");
            confirmPassworldPrompt = new TextFieldPanel("Confirm password");
            JButton signUpButton = new Button("Sign Up", 30);
            signUpButton.addActionListener(new SignUpListener());
            panel.add(usernamePrompt);
            panel.add(passworldPrompt);
            panel.add(confirmPassworldPrompt);
            panel.add(signUpButton);
            backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
            backPanel.add(new BackButton(localCardLayout, accountPanel, "Login"));
            add(backPanel, BorderLayout.NORTH);
            add(panel, BorderLayout.CENTER);
            setOpaque(false);
            backPanel.setOpaque(false);
            panel.setOpaque(false); 
        }

        class SignUpListener implements ActionListener
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                if (usernamePrompt.getText() != null && passworldPrompt.getText() != null && confirmPassworldPrompt.getText() != null)
                {
                    if (!passworldPrompt.getText().equals(confirmPassworldPrompt.getText()))
                    {
                        System.out.println("Passwords do not match!");
     
                    }
                    else if (User.getUser(usernamePrompt.getText()) == null)
                    {
                        FlightSimulator.user = new User(usernamePrompt.getText(), confirmPassworldPrompt.getText());
                        FlightSimulator.flightSim.showPanel("MainMenu");
                        FlightSimulator.flightSim.updateWelcomeText();
                    }
                    else
                    {
                        System.out.println("Username already taken!");
                    }
                }
                else
                {
                    System.out.println("Re-enter info");
                }

                
            }
        }
    }

    public void showSignUpPanel()
    {
        localCardLayout.show(this, "SignUp");
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

    

    
}
