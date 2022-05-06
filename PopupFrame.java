import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class PopupFrame extends JFrame
{
    public PopupFrame(String text)
    {
        JOptionPane.showMessageDialog(this, text);
    }
}
