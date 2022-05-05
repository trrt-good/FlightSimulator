import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.Color;
public class SettingsPanel extends JPanel
{
    public SettingsPanel()
    {
        setBackground(Color.WHITE);
    }

    public static SwitchToSettingsListener getSettingsSwitcher()
    {
        return new SwitchToSettingsListener();
    }

    public static String name()
    {
        return "SettingsPanel";
    }

    static class SwitchToSettingsListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e) 
        {
            FlightSimulator.flightSim.showPanel(name());
        }
    }
}

