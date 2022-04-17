import javax.swing.JPanel;

public class GamePanel extends JPanel
{
    private RenderingPanel renderingPanel;
    private JPanel sideButtonPanel;

    public GamePanel()
    {
        setLayout(null);
        renderingPanel = new RenderingPanel(800, 450);
        add(renderingPanel);
        renderingPanel.setBounds(0, 0, 800, 450);
    }
}
