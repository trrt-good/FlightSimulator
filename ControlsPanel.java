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

//a panel which allows the user to edit their controls. This also writes the user's
//prefferences to a text file.
public class ControlsPanel extends JPanel implements FocusListener
{
    //the button which allows the user to go back to the previous panel
    private BackButton backButton;

    //each airplane control has a control setting object 
    private ControlSetting throttleUpControl;
    private ControlSetting throttleDownControl;
    private ControlSetting pitchUpControl;
    private ControlSetting pitchDownControl;
    private ControlSetting rollLeftControl;
    private ControlSetting rollRightControl;
    private ControlSetting yawLeftControl;
    private ControlSetting yawRightControl;
    private ControlSetting brakesControl;

    //set up the panel
    public ControlsPanel()
    {
        setOpaque(false);
        setLayout(new BorderLayout());
        addFocusListener(this);
        add(topPanel(), BorderLayout.NORTH);
        add(ControlsContents(), BorderLayout.CENTER);
        
    }

    //returns a jpanel which is the panel at the top of the ControlsPanel which 
    //contains the back button and the title.
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

    //the central panel in the borderLayout which contains all the individual control settings
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

    //when this panel gains focus (rather than using paintComponent) I load all the 
    //user preferences into the ControlSetting objects. 
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

    //This class is the object for each each keybind which contains a text field and a 
    //label for the function of the keybind. 
    class ControlSetting extends JPanel
    {        
        private JTextField keyDisplay; //the textfield that displays the current setting

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

        //sets the key based on the value param. accounts for special keys like 
        //the arrows.
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

        //returns the value of the key based on the KeyEvent VK finals 
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

    //a listener class for the restore defaults button. This restores all the controls to their 
    //corresponding default values then saves it into the text file.
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

    //applies any changes from the ControlSettings objects into the text file
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

    //returns a listener for switching to this panel.
    public static SwitchToControlsListener getControlsSwitcher(String switcherName)
    {
        return new SwitchToControlsListener(switcherName);
    }

    //the name used for card layout switching
    public static String name()
    {
        return "ControlsPanel";
    }

    //a static listener class which can handle any button who's function is to
    //switch to the ControlsPanel. This is a polymorphic apporach that avoids having
    //to make multiple listeners in many difference classes which all have the same purpose.
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

