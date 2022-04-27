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
    //this card layout switches between the login and signup pages
    private CardLayout localCardLayout;
    
    //login and signup panels 
    private JPanel login;
    private JPanel signUp;

    //for the other panels to access 
    private AccountPanel accountPanel;

    //panel for going back to the login page.
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
        private TextFieldPanel passwordPrompt;
        public LoginPanel()
        {
            setLayout(new FlowLayout(FlowLayout.CENTER, 700, 50));
            usernamePrompt = new TextFieldPanel("username");
            passwordPrompt = new TextFieldPanel("password");

            JButton signInButton = new Button("Sign In", 30);
            signInButton.addActionListener(new SignInListener());
            add(usernamePrompt);
            add(passwordPrompt);
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
            JButton signUpButton = new Button("Sign Up ", 30);
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

        //listens for the signin button to be called, and gets the user info
        class SignInListener implements ActionListener
        {
             
            public void actionPerformed(ActionEvent e) 
            {
                if (usernamePrompt.getText() != null && passwordPrompt.getText() != null)
                {
                    User user = User.getUser(usernamePrompt.getText());
                    if (user != null)
                    {
                        if (user.getPassword().equals(passwordPrompt.getText()))
                        {
                            FlightSimulator.user = user;
                            FlightSimulator.flightSim.showPanel("MainMenu");
                            FlightSimulator.flightSim.updateWelcomeText();
                        }
                        else
                        {
                            //placeholders for pop-up window distplaying the error
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
        private TextFieldPanel passwordPrompt;
        private TextFieldPanel confirmPasswordPrompt;
        public SignUpPanel()
        {   
            setLayout(new BorderLayout(0, 0));
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 700, 50));
            usernamePrompt = new TextFieldPanel("Enter a username");
            passwordPrompt = new TextFieldPanel("Set a password");
            confirmPasswordPrompt = new TextFieldPanel("Confirm password");
            JButton signUpButton = new Button("Sign Up ", 30);
            signUpButton.addActionListener(new SignUpListener());
            panel.add(usernamePrompt);
            panel.add(passwordPrompt);
            panel.add(confirmPasswordPrompt);
            panel.add(signUpButton);
            backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
            backPanel.add(new BackButton(localCardLayout, accountPanel, "Login"));
            add(backPanel, BorderLayout.NORTH);
            add(panel, BorderLayout.CENTER);
            setOpaque(false);
            backPanel.setOpaque(false);
            panel.setOpaque(false); 
        }

        //listens for the signup button to be pressed and creates a new user
        class SignUpListener implements ActionListener
        {
             
            public void actionPerformed(ActionEvent e) 
            {
                if (usernamePrompt.getText() != null && passwordPrompt.getText() != null && confirmPasswordPrompt.getText() != null)
                {
                    if (!passwordPrompt.getText().equals(confirmPasswordPrompt.getText()))
                    {
                        System.out.println("Passwords do not match!");
     
                    }
                    else if (User.getUser(usernamePrompt.getText()) == null)
                    {
                        FlightSimulator.user = new User(usernamePrompt.getText(), confirmPasswordPrompt.getText());
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

    //switches to sign up panel.
    class SwitchToSignUpPanel implements ActionListener
    {
         
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
