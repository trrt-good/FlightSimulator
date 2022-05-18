import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.awt.event.MouseEvent;
import java.awt.event.FocusListener;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Scanner;

public class GamePanel extends JPanel implements KeyListener, MouseListener, FocusListener
{
    //objects for rendering
    private RenderingPanel renderingPanel;
    private Airplane airplane;
    private Lighting lighting; 
    private Camera gameCamera;
    private Terrain ground;
    private Image flightDials; 

    private ArrayList<String> questions;
    private ArrayList<String> aAnswers;
    private ArrayList<String> bAnswers;
    private ArrayList<String> cAnswers;
    private ArrayList<String> dAnswers;

    private boolean paused;
    private SidePanel sidePanel;
    private Color skyColor = new Color(91, 215, 252);

    //creates game objects and rendering related objects. 
    public GamePanel()
    {
        setLayout(new BorderLayout());
        addKeyListener(this);
        addFocusListener(this);
        addMouseListener(this);
        sidePanel = new SidePanel();
        add(sidePanel, BorderLayout.EAST);
        flightDials = Utils.makeImage(new File(FlightSimulator.RESOURCES_FOLDER, "AirplaneDials.png"));
        lighting = new Lighting(new Vector3(1, -1, 1), 30, 150);
        gameCamera = new Camera(new Vector3(0, 0, -1000), 50000, 30, 60);
        airplane = new Airplane(this, gameCamera);
        ground = new Terrain(-500, -200, 6000, 1000, 500, 500, 0.02, 30, new Color(1, 75, 148), new Color(15, 99, 0), new Color(200, 200, 210));
        gameCamera.setOrbitControls(this, airplane, 1000, 10);
    }

    public void readQuestions()
    {
        Scanner reader = Utils.makeReader(new File(FlightSimulator.RESOURCES_FOLDER, "questions.txt"));
        String line = "";
        while (reader.hasNextLine())
        {
            line = reader.nextLine();
            if (line.startsWith("q "))
            {
                questions.add(line.substring(2));
            }
            else if (line.startsWith("a "))
            {
                aAnswers.add(line.substring(2));
            }
            else if (line.startsWith("b "))
            {
                bAnswers.add(line.substring(2));
            }
            else if (line.startsWith("c "))
            {
                cAnswers.add(line.substring(2));
            }
            else if (line.startsWith("d "))
            {
                dAnswers.add(line.substring(2));
            }
        }
    }

    //sets up the rendering panel and starts the rendering updates. 
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 30));
        requestFocusInWindow();
        if (renderingPanel == null)
        {
            renderingPanel = new RenderingPanel(FlightSimulator.DEFAULT_WIDTH - FlightSimulator.DEFAULT_WIDTH/4, FlightSimulator.DEFAULT_HEIGHT);
            gameCamera.setFov(FlightSimulator.user.getSettings().fov);
            gameCamera.setSensitivity(FlightSimulator.user.getSettings().sensitivity);
            airplane.setRenderPanel(renderingPanel);
            airplane.startPhysics();
            renderingPanel.setLighting(lighting);
            renderingPanel.setCamera(gameCamera);
            renderingPanel.setLighting(lighting);
            renderingPanel.setFog(gameCamera.getFarClipDistancee()*0.6, gameCamera.getFarClipDistancee(), skyColor);
            renderingPanel.addMesh(ground);
            renderingPanel.setFPSlimit(150);
            renderingPanel.start();
            add(renderingPanel);
            validate();
        }
        new QuestionPopup(1, true, true, false, true);
    }

    class SidePanel extends JPanel 
    {
        public SidePanel()
        {
            setBackground(new Color(90, 94, 97));
            setPreferredSize(new Dimension(FlightSimulator.DEFAULT_WIDTH/4, 100));
            setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
            Button mainMenuButton = new Button("Main Menu", 20, 150, 50);
            mainMenuButton.addActionListener(MainMenu.getMainMenuPanelSwitcher());
            Button settingsButton = new Button("Settings", 20, 150, 50);
            settingsButton.addActionListener(SettingsPanel.getSettingsSwitcher(GamePanel.name()));
            Button controlsButton = new Button("Controls", 20, 150, 50);
            controlsButton.addActionListener(ControlsPanel.getControlsSwitcher(GamePanel.name()));
            Button resetButton = new Button("Reset", 20, 150, 50);
            resetButton.addActionListener(new ResetButtonListener());
            add(mainMenuButton);
            add(settingsButton);
            add(controlsButton);
            add(resetButton);
            
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D)g;
            g.drawImage(flightDials, FlightSimulator.DEFAULT_WIDTH/8, 400-FlightSimulator.DEFAULT_WIDTH/8, FlightSimulator.DEFAULT_WIDTH/4, 400, 250, 15, 480, 245, this);
            g.drawImage(flightDials, 0, 400, FlightSimulator.DEFAULT_WIDTH/4, FlightSimulator.DEFAULT_WIDTH/4+400, 0, 245, 480, 728, this);
            g2d.setStroke(new BasicStroke(4));
            g2d.setColor(Color.WHITE);
            
            //  compass center = 253, 315
            //  airspeed center = 86, 487
            // if (airplane.getAltitude() > 4000)
            //     new QuestionPopup(1, 'a');
            drawDialNeedle(g2d, 252, 315, 60, airplane.orientation().y); //compass
            drawDialNeedle(g2d, 85, 487, 60, airplane.getSpeed()/75);//airspeed
            drawTurnCoordinator(g2d, 85, 659, airplane.orientation().z); //turn coordinator
            drawDialNeedle(g2d, 255, 489, 55, airplane.getAltitude()/300/Math.PI); //altimeter hundreds needle
            drawDialNeedle(g2d, 255, 489, 30, airplane.getAltitude()/3000/Math.PI); //altimeter thousands needle
            drawDialNeedle(g2d, 255, 659, 60, airplane.getVerticalClimb()/Math.PI/10 - Math.PI/2); //Vertical climb
        }

        public void drawDialNeedle(Graphics2D g2d, int centerX, int centerY, int length, double rotation)
        {
            rotation -= Math.PI/2;
            int endPointX;
            int endPointY;

            endPointX = centerX+(int)(Math.cos(rotation)*length);
            endPointY = centerY+(int)(Math.sin(rotation)*length);

            g2d.drawLine(centerX, centerY, endPointX, endPointY);
        }

        public void drawTurnCoordinator(Graphics2D g2d, int centerX, int centerY, double rotation)
        {
            int endPointX;
            int endPointY;

            endPointX = (int)(Math.cos(-rotation)*50);
            endPointY = (int)(Math.sin(-rotation)*50);
            g2d.drawLine(centerX, centerY, centerX+endPointX, centerY+endPointY);
            g2d.drawLine(centerX, centerY, centerX-endPointX, centerY-endPointY);
        }
    }

    public static String name()
    {
        return "GamePanel";
    }

    public void keyPressed(KeyEvent e) 
    {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            togglePause();
        }
    }

    public void togglePause()
    {
        if (paused)
        {
            paused = false;
            repaint();
            airplane.startPhysics();
            renderingPanel.start();
        }
        else
        {
            paused = true;
            repaint();
            airplane.stopPhysics();
            renderingPanel.stopThread();
        }
    }

    public void pause()
    {
        if (!paused)
        {
            paused = true;
            repaint();
            airplane.stopPhysics();
            renderingPanel.stopThread();
        }
    }

    class QuestionPopup extends JFrame implements ActionListener
    {
        private boolean[] selectedAnswers;
        private boolean[] answers; 
        private int numCorrect;

        public QuestionPopup(int questionNum, boolean... correctAnswers)
        {
            setVisible(true);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            setSize(500, 500);
            setResizable(false);
            setLayout(new BorderLayout());
            answers = correctAnswers;

            for (int i = 0; i < answers.length; i ++)
            {
                if (answers[i] == true)
                    numCorrect ++;
            }

            selectedAnswers = new boolean[]{false, false, false, false};
            add(new AnswerChoicePanel(), BorderLayout.CENTER);
            add(new BottomPanel(), BorderLayout.SOUTH);
            
            
        }

        public void paintComponent(Graphics g)
        {
            System.out.println("paintC");
            requestFocusInWindow();
        }

        public void actionPerformed(ActionEvent e)
        {

        }

        public boolean checkAnswers()
        {
            boolean correct = true;
            for (int i = 0; i < selectedAnswers.length; i++)
            {
                if (selectedAnswers[i] != answers[i])
                    correct = false;
            }
            return correct;
        }

        public void closeQuestionFrame()
        {
            dispose();
        }

        class AnswerChoicePanel extends JPanel implements ActionListener
        {

            public AnswerChoicePanel()
            {
                setLayout(new FlowLayout(FlowLayout.LEFT, 50, 50));
                if (numCorrect > 1)
                {
                    JCheckBox aCheckBox = new JCheckBox("0) ");
                    JCheckBox bCheckBox = new JCheckBox("1) ");
                    JCheckBox cCheckBox = new JCheckBox("2) ");
                    JCheckBox dCheckBox = new JCheckBox("3) ");
                    aCheckBox.addActionListener(this);
                    bCheckBox.addActionListener(this);
                    cCheckBox.addActionListener(this);
                    dCheckBox.addActionListener(this);
                    add(aCheckBox);
                    add(bCheckBox);
                    add(cCheckBox);
                    add(dCheckBox);
                }
                else
                {
                    ButtonGroup radioButtonGroup = new ButtonGroup();
                    JRadioButton aButton = new JRadioButton("0) ");
                    JRadioButton bButton = new JRadioButton("1) ");
                    JRadioButton cButton = new JRadioButton("2) ");
                    JRadioButton dButton = new JRadioButton("3) ");
                    aButton.addActionListener(this);
                    bButton.addActionListener(this);
                    cButton.addActionListener(this);
                    dButton.addActionListener(this);
                    radioButtonGroup.add(aButton);
                    radioButtonGroup.add(bButton);
                    radioButtonGroup.add(cButton);
                    radioButtonGroup.add(dButton);
                    add(aButton);
                    add(bButton);
                    add(cButton);
                    add(dButton);
                }
            }

            public void actionPerformed(ActionEvent e) 
            {
                AbstractButton button = null;
                if (e.getSource() instanceof AbstractButton)
                    button = (AbstractButton)e.getSource();
                else
                    return;
                if (numCorrect == 1)
                    selectedAnswers = new boolean[]{false, false, false, false};
                int answerChosen = Integer.parseInt(e.getActionCommand().substring(0, 1));
                selectedAnswers[answerChosen] = button.isSelected();
            }
        }

        class BottomPanel extends JPanel implements ActionListener
        {
            private Button confirmButton;
            public BottomPanel()
            {
                setLayout(new FlowLayout());
                confirmButton = new Button("Check", 30, 200, 50);
                setPreferredSize(new Dimension(100, 100));
                confirmButton.addActionListener(this);
                add(confirmButton);
            }

            public void actionPerformed(ActionEvent e)
            {
                if (confirmButton.getText().equalsIgnoreCase("close"))
                {
                    closeQuestionFrame();
                    return;
                }
                if (checkAnswers())
                {
                    confirmButton.setText("close");
                }
            }
        }
    }

    public void unpause()
    {
        if (paused)
        {
            paused = false;
            repaint();
            airplane.startPhysics();
            renderingPanel.start();
        }
    }

    public void updateSettings()
    {
        gameCamera.setFov(FlightSimulator.user.getSettings().fov);
        gameCamera.setSensitivity(FlightSimulator.user.getSettings().sensitivity);
    }

    public boolean isPaused()
    {
        return paused;
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

    public static SwitchToGamePanelListener getGamePanelSwitcher()
    {
        return new SwitchToGamePanelListener();
    }

    static class SwitchToGamePanelListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e) 
        {
            FlightSimulator.flightSim.showPanel(name());
        }
    }

    class ResetButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            airplane.reset();
        }
    }

    class PauseListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            togglePause();
        }
    }

    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) 
    {
        requestFocusInWindow();
    }
    public void mouseReleased(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void focusGained(FocusEvent e) 
    {
        unpause();
        repaint();
        updateSettings();
    }
    public void focusLost(FocusEvent e) 
    {
        repaint();
        pause();
    }
    public void mouseEntered(MouseEvent e) {}
}
