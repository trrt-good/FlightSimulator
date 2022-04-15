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
import javax.swing.plaf.basic.BasicBorders;

import java.awt.Dimension;

public class LoginPanel extends JPanel
    {
        private String name;
        private CardLayout localCardLayout;
        private FlightSimulator parent;
        
        private JPanel login;
        private JPanel signUp;
        
        public LoginPanel(FlightSimulator parent, String nameIn)
        {
            name = nameIn;
            localCardLayout = new CardLayout();
            setLayout(localCardLayout);
            login = createLoginPanel();
            signUp = createSignUpPanel();
            
            this.add(signUp, "SignUp");
            this.add(login, "Login");
            localCardLayout.show(this, "Login");
        }
        
        public JPanel createLoginPanel()
        {
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 700, 50));
            TextFieldPanel usernamePrompt = new TextFieldPanel("username");
            TextFieldPanel passworldPrompt = new TextFieldPanel("passworld");
            JButton signInButton = new JButton("Sign In");
            signInButton.setFont(new Font("Serif", Font.BOLD, 40));
            signInButton.setBackground(new Color(255, 100, 100));
            panel.add(usernamePrompt);
            panel.add(passworldPrompt);
            panel.add(signInButton);
            panel.add(makeSignUpButtonPanel());
            return panel;
        }

        public JPanel createSignUpPanel()
        {
            JPanel parentPanel = new JPanel();
            parentPanel.setLayout(new BorderLayout(10, 10));
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 700, 50));
            TextFieldPanel usernamePrompt = new TextFieldPanel("Enter a username");
            TextFieldPanel passworldPrompt = new TextFieldPanel("Set a password");
            TextFieldPanel confirmPassworldPrompt = new TextFieldPanel("Confirm password");
            JButton signInButton = new JButton("Sign Up");
            signInButton.setFont(new Font("Serif", Font.BOLD, 40));
            signInButton.setBackground(new Color(255, 100, 100));
            panel.add(usernamePrompt);
            panel.add(passworldPrompt);
            panel.add(confirmPassworldPrompt);
            panel.add(signInButton);
            JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
            backPanel.add(new BackButton(localCardLayout, this, "Login"));
            parentPanel.add(backPanel, BorderLayout.NORTH);
            parentPanel.add(panel, BorderLayout.CENTER);

            return parentPanel;
        }

        public void showSignUpPanel()
        {
            localCardLayout.show(this, "SignUp");
        }

        public JPanel makeSignUpButtonPanel()
        {
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
            JButton signUpButton = new JButton("Sign Up");
            signUpButton.setFont(new Font("Serif", Font.BOLD, 30));
            signUpButton.setBackground(new Color(255, 100, 100));
            signUpButton.addActionListener(new SignUpButtonListener());
            JLabel signUpLabel = new JLabel("Don't have an account?");
            signUpLabel.setFont(new Font("Serif", Font.PLAIN, 25));
            panel.setPreferredSize(new Dimension(250, 100));
            panel.add(signUpLabel);
            panel.add(signUpButton);
            return panel;
        }
        
        public String getName()
        {
            return name;
        }
        
        class TextFieldPanel extends JPanel implements ActionListener
        {
            private String text;
            public TextFieldPanel(String prompt)
            {
                setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));
                JLabel promptLabel = new JLabel(prompt);
                promptLabel.setFont(new Font("Serif", Font.PLAIN, 25));
                add(promptLabel);
                JTextField textField = new JTextField("", 10);
                textField.setFont(new Font("Serif", Font.PLAIN, 30));
                textField.addActionListener(this);
                add(textField);
                setPreferredSize(new Dimension(200, 100));
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

        class SignUpButtonListener implements ActionListener
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                showSignUpPanel();
            }
            
        }


    }
