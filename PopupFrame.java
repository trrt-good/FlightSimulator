import javax.swing.JFrame;
import javax.swing.JOptionPane;

//an object for message popups
public class PopupFrame extends JFrame
{
    public PopupFrame(String text)
    {
        JOptionPane.showMessageDialog(this, text);
    }
}
