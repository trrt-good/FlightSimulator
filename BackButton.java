import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.CardLayout;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;

public class BackButton extends JButton implements ActionListener
{
    private CardLayout layout;
    private JPanel parentPanel;
    private String prevPanelName;
    public BackButton(CardLayout layoutIn, JPanel parentPanelIn, String prevPanelNameIn)
    {
        setText("Back");
        setBackground(new Color(255, 100, 100));
        setFont(new Font("Serif", Font.BOLD, 30));
        layout = layoutIn;
        parentPanel = parentPanelIn;
        prevPanelName = prevPanelNameIn;
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        layout.show(parentPanel, prevPanelName);
    }
}
