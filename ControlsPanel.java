import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Graphics;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

public class ControlsPanel extends JPanel implements FocusListener
{
    private BackButton backButton;

    private ControlSetting throttleUpControl;
    private ControlSetting throttleDownControl;
    private ControlSetting pitchUpControl;
    private ControlSetting pitchDownControl;
    private ControlSetting rollLeftControl;
    private ControlSetting rollRightControl;
    private ControlSetting yawLeftControl;
    private ControlSetting yawRightControl;
    private ControlSetting brakesControl;

    public ControlsPanel()
    {
        setOpaque(false);
        setLayout(new BorderLayout());
        addFocusListener(this);
        add(topPanel(), BorderLayout.NORTH);
        add(ControlsContents(), BorderLayout.CENTER);
        
    }

    private JPanel topPanel()
    {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 30));
        backButton = new BackButton(FlightSimulator.flightSim.getCardLayout(), FlightSimulator.flightSim.getCardPanelHolder());
        panel.add(backButton);
        JLabel label = new JLabel("Controls");
        label.setFont(new Font(FlightSimulator.FONTSTYLE, Font.BOLD, 40));
        label.setForeground(Color.WHITE);
        panel.add(label);
        return panel;
    }

    private JPanel ControlsContents()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 100, 30));
        panel.setOpaque(false);
        throttleUpControl = new ControlSetting("Throttle Up");
        panel.add(throttleUpControl);
        throttleDownControl = new ControlSetting("Throttle Down");
        panel.add(throttleDownControl);
        pitchUpControl = new ControlSetting("Pitch Up");
        panel.add(pitchUpControl);
        pitchDownControl = new ControlSetting("Pitch Down");
        panel.add(pitchDownControl);
        rollLeftControl = new ControlSetting("Roll Left");
        panel.add(rollLeftControl);
        rollRightControl = new ControlSetting("Roll Right");
        panel.add(rollRightControl);
        yawLeftControl = new ControlSetting("Yaw Left");
        panel.add(yawLeftControl);
        yawRightControl = new ControlSetting("Yaw Right");
        panel.add(yawRightControl);
        brakesControl = new ControlSetting("Brakes");
        panel.add(brakesControl);
        Button resetButton = new Button("Restore Defaults", 30);
        resetButton.addActionListener(new restoreDefaultsListener());
        panel.add(resetButton);
        Button confirmButton = new Button("Apply Changes", 30);
        confirmButton.addActionListener(new confirmChangesListener());
        panel.add(confirmButton);
        
        return panel;
    }

    protected void paintComponent(Graphics g) 
    {
        Utils.paintBackground(this, g);
        requestFocusInWindow();
    }

    public void focusGained(FocusEvent e) 
    {
        throttleUpControl.setKey(FlightSimulator.user.getSettings().throttleUp);
        throttleDownControl.setKey(FlightSimulator.user.getSettings().throttleDown);
        pitchUpControl.setKey(FlightSimulator.user.getSettings().pitchUp);
        pitchDownControl.setKey(FlightSimulator.user.getSettings().pitchDown);
        rollLeftControl.setKey(FlightSimulator.user.getSettings().rollLeft);
        rollRightControl.setKey(FlightSimulator.user.getSettings().rollRight);
        yawLeftControl.setKey(FlightSimulator.user.getSettings().yawLeft);
        yawRightControl.setKey(FlightSimulator.user.getSettings().yawRight);
        brakesControl.setKey(FlightSimulator.user.getSettings().brakes);
    }
    public void focusLost(FocusEvent e) {}

    class ControlSetting extends JPanel
    {        
        private JTextField keyDisplay;

        public ControlSetting(String settingName)
        {        
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
            JLabel settingLabel = new JLabel(settingName);
            settingLabel.setFont(new Font(FlightSimulator.FONTSTYLE, Font.BOLD, 30));
            settingLabel.setForeground(Color.WHITE);
            keyDisplay = new JTextField(4);
            keyDisplay.setFont(new Font(FlightSimulator.FONTSTYLE, Font.BOLD, 30));

            add(keyDisplay);
            add(settingLabel);
        }

        public void setKey(int value)
        {
            switch (value) {
                case KeyEvent.VK_UP:
                    keyDisplay.setText("up");
                    return;
                case KeyEvent.VK_DOWN:
                    keyDisplay.setText("down");
                    return;
                case KeyEvent.VK_LEFT:
                    keyDisplay.setText("left");
                    return;
                case KeyEvent.VK_RIGHT:
                    keyDisplay.setText("right");
                    return;
                case KeyEvent.VK_SPACE:
                    keyDisplay.setText("space");
                    return;
                default:
                    break;
            }
            keyDisplay.setText("" + (char)(value+32));
        }

        public int getKeyValue()
        {
            switch (keyDisplay.getText().toLowerCase()) {
                case "up":
                    return KeyEvent.VK_UP;
                case "down":
                    return KeyEvent.VK_DOWN;
                case "left":
                    return KeyEvent.VK_LEFT;
                case "right":
                    return KeyEvent.VK_RIGHT;
                case "space":
                    return KeyEvent.VK_SPACE;
                default:
                    return keyDisplay.getText().toLowerCase().charAt(0) - 32;
            }
        }
    }

    class restoreDefaultsListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            FlightSimulator.user.getSettings().restoreDefaults();
            throttleUpControl.setKey(FlightSimulator.user.getSettings().throttleUp);
            throttleDownControl.setKey(FlightSimulator.user.getSettings().throttleDown);
            pitchUpControl.setKey(FlightSimulator.user.getSettings().pitchUp);
            pitchDownControl.setKey(FlightSimulator.user.getSettings().pitchDown);
            rollLeftControl.setKey(FlightSimulator.user.getSettings().rollLeft);
            rollRightControl.setKey(FlightSimulator.user.getSettings().rollRight);
            yawLeftControl.setKey(FlightSimulator.user.getSettings().yawLeft);
            yawRightControl.setKey(FlightSimulator.user.getSettings().yawRight);
            brakesControl.setKey(FlightSimulator.user.getSettings().brakes);
        }
    }

    class confirmChangesListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            FlightSimulator.user.getSettings().throttleUp = throttleUpControl.getKeyValue();
            FlightSimulator.user.getSettings().throttleDown = throttleDownControl.getKeyValue();
            FlightSimulator.user.getSettings().pitchUp = pitchUpControl.getKeyValue();
            FlightSimulator.user.getSettings().pitchDown = pitchDownControl.getKeyValue();
            FlightSimulator.user.getSettings().rollLeft = rollLeftControl.getKeyValue();
            FlightSimulator.user.getSettings().rollRight = rollRightControl.getKeyValue();
            FlightSimulator.user.getSettings().yawLeft = yawLeftControl.getKeyValue();
            FlightSimulator.user.getSettings().yawRight = yawRightControl.getKeyValue();
            FlightSimulator.user.getSettings().brakes = brakesControl.getKeyValue();
            FlightSimulator.user.saveData();
        }
    }

    public static SwitchToControlsListener getControlsSwitcher(String switcherName)
    {
        return new SwitchToControlsListener(switcherName);
    }

    public static String name()
    {
        return "ControlsPanel";
    }

    static class SwitchToControlsListener implements ActionListener
    {
        private String prevName;

        public SwitchToControlsListener(String previousName)
        {
            prevName = previousName;
        }
        
        public void actionPerformed(ActionEvent e) 
        {
            FlightSimulator.flightSim.showPanel(name());
            FlightSimulator.flightSim.getControlsPanel().backButton.setDestination(prevName);
        }
    }

    
}

