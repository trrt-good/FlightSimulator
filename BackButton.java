import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.CardLayout;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;


public class BackButton extends JButton implements ActionListener
{
    //necessary for switching between cards 
    private CardLayout layout;
    private JPanel parentPanel;
    private String prevPanelName;

    //constructs a back button which takes in the nessecary variables for switching 
    //between panels in a card layout.
    public BackButton(CardLayout layoutIn, JPanel parentPanelIn, String prevPanelNameIn)
    {
        setText("Back");
        setBackground(new Color(184, 71, 42));
        setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 30));
        setForeground(Color.WHITE);
        layout = layoutIn;
        parentPanel = parentPanelIn;
        prevPanelName = prevPanelNameIn;
        this.addActionListener(this);
        setFocusPainted(false);
        setBorderPainted(false);
        setPreferredSize(new Dimension(150, 50));
    }

    //different constructor for a non-specific destination for the back button
    public BackButton(CardLayout layoutIn, JPanel parentPanelIn)
    {
        setText("Back");
        setBackground(new Color(184, 71, 42));
        setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 30));
        setForeground(Color.WHITE);
        layout = layoutIn;
        parentPanel = parentPanelIn;
        prevPanelName = null;
        this.addActionListener(this);
        setFocusPainted(false);
        setBorderPainted(false);
        setPreferredSize(new Dimension(150, 50));
    }

    //checks if the previous name variable is null, if it isn't then switch to that
    //panel, if it is null, simply show the previous panel in the card layout.
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if (prevPanelName != null)
            layout.show(parentPanel, prevPanelName);
        else
        {
            layout.previous(parentPanel);
        }
            
    }
}
