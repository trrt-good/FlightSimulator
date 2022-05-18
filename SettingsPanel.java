import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.Color;
import java.awt.Graphics;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

public class SettingsPanel extends JPanel implements FocusListener
{
    private BackButton backButton;

    private SliderSetting fovSlider;
    private SliderSetting sensitivitySlider;
    private Button controlsButton;

    public SettingsPanel()
    {
        setOpaque(false);
        setLayout(new BorderLayout());
        addFocusListener(this);
        add(topPanel(), BorderLayout.NORTH);
        add(settingsContents(), BorderLayout.CENTER);
        
    }

    private JPanel topPanel()
    {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 30));
        backButton = new BackButton(FlightSimulator.flightSim.getCardLayout(), FlightSimulator.flightSim.getCardPanelHolder());
        panel.add(backButton);
        JLabel label = new JLabel("Settings");
        label.setFont(new Font(FlightSimulator.FONTSTYLE, Font.BOLD, 40));
        label.setForeground(Color.WHITE);
        panel.add(label);
        return panel;
    }

    private JPanel settingsContents()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 100, 30));
        panel.setOpaque(false);
        fovSlider = new SliderSetting("FOV", 30, 120);
        panel.add(fovSlider);
        sensitivitySlider = new SliderSetting("Sensitivity", 1, 30);
        panel.add(sensitivitySlider);
        controlsButton = new Button("Controls", 50);
        controlsButton.addActionListener(ControlsPanel.getControlsSwitcher(SettingsPanel.name()));
        panel.add(controlsButton);
        return panel;
    }

    protected void paintComponent(Graphics g) 
    {
        Utils.paintBackground(this, g);
        requestFocusInWindow();
    }

    public void focusGained(FocusEvent e) 
    {
        fovSlider.setValue(FlightSimulator.user.getSettings().fov);
        sensitivitySlider.setValue(FlightSimulator.user.getSettings().sensitivity);
        System.out.println("gained");
    }

    public void focusLost(FocusEvent e) 
    {
        FlightSimulator.user.getSettings().fov = fovSlider.getValue();
        FlightSimulator.user.getSettings().sensitivity = sensitivitySlider.getValue();
        FlightSimulator.user.saveData();
    }

    class SliderSetting extends JPanel implements ChangeListener
    {        
        private JTextField valueDisplay;
        private JSlider slider;

        public SliderSetting(String settingName, int min, int max)
        {        
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.LEFT, 50, 50));
            JLabel settingLabel = new JLabel(settingName);
            settingLabel.setFont(new Font(FlightSimulator.FONTSTYLE, Font.BOLD, 40));
            settingLabel.setForeground(Color.WHITE);
            valueDisplay = new JTextField(5);
            valueDisplay.setEditable(false);
            valueDisplay.setFont(new Font(FlightSimulator.FONTSTYLE, Font.BOLD, 40));
            slider = new JSlider(min, max);
            slider.setOpaque(false);
            slider.addChangeListener(this);


            add(settingLabel);
            add(valueDisplay);
            add(slider);
        }

        public void setValue(double value)
        {
            slider.setValue((int)value);
            valueDisplay.setText("" + value);
        }

        public int getValue()
        {
            return slider.getValue();
        }

        public void stateChanged(ChangeEvent e) 
        {
            valueDisplay.setText("" + slider.getValue());
        }
    }

    public static SwitchToSettingsListener getSettingsSwitcher(String switcherName)
    {
        return new SwitchToSettingsListener(switcherName);
    }

    public static String name()
    {
        return "SettingsPanel";
    }

    static class SwitchToSettingsListener implements ActionListener
    {
        private String prevName;

        public SwitchToSettingsListener(String previousName)
        {
            prevName = previousName;
        }
        
        public void actionPerformed(ActionEvent e) 
        {
            FlightSimulator.flightSim.showPanel(name());
            FlightSimulator.flightSim.getSettingsPanel().backButton.setDestination(prevName);
        }
    }

    
}

