import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;

public class Button extends JButton
{
    //constructs a button so I don't have to repeat the same lines of code over and 
    //over for each button I make
    public Button(String text, int fontSize)
    {
        setText(text);
        setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, fontSize));
        setPreferredSize(new Dimension((int)(0.7*fontSize*text.length()), fontSize*2));
        setBackground(FlightSimulator.THEME_COLOR);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    //different constructor if I need to specify width and height
    public Button(String text, int fontSize, int width, int height)
    {
        setText(text);
        setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, fontSize));
        setPreferredSize(new Dimension(width, height));
        setBackground(FlightSimulator.THEME_COLOR);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
    }
}
