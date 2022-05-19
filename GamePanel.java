import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

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
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Image;
import java.awt.Insets;

//the JPanel which has the game in it
public class GamePanel extends JPanel implements KeyListener, MouseListener, FocusListener
{
    //objects for rendering
    private RenderingPanel renderingPanel; //the panel which preforms rendering
    private Airplane airplane; //the airplane object 
    private Lighting lighting; 
    private Camera gameCamera; //the camera used in the game
    private Terrain ground; //the terrain object which is just a mesh
    private GameObject runway1; //a runway game object 
    private GameObject runway2;

    private Image flightDials; //the image of the flight dials used on the side of the panel

    private int wrongAmount; //number of wrong answers the user has made
    private boolean ended; //wether or not the game has ended 

    private boolean paused; //is the game paused?
    private SidePanel sidePanel; //the panel on the side of the screen which has dials and buttons
    private Color skyColor = new Color(91, 215, 252);

    private boolean learningMode; //has the user learned to fly yet?

    //creates game objects and rendering related objects. 
    public GamePanel()
    {
        setLayout(new BorderLayout());
        addKeyListener(this);
        addFocusListener(this);
        addMouseListener(this);
        sidePanel = new SidePanel();
        wrongAmount = 0;
        add(sidePanel, BorderLayout.EAST);
        ended = false;
        runway1 = new GameObject("runeway1", new Mesh("runway.obj", Color.DARK_GRAY, new Vector3(0, -0.09, 37), new EulerAngle(), 300, false, false), new Transform(new Vector3()));
        runway2 = new GameObject("runeway2", new Mesh("runway.obj", Color.DARK_GRAY, new Vector3(0, -0.09, 2000), new EulerAngle(), 300, false, false), new Transform(new Vector3()));
        
        flightDials = Utils.makeImage(new File(FlightSimulator.RESOURCES_FOLDER, "AirplaneDials.png"));
        lighting = new Lighting(new Vector3(1, -1, 1), 30, 150);
        gameCamera = new Camera(new Vector3(0, 0, -1000), 50000, 100, 60);
        airplane = new Airplane(this, gameCamera);
        ground = new Terrain(-500, -200, 6000, 1000, 800, 150, 0.02, 30, new Color(1, 75, 148), new Color(15, 99, 0), new Color(200, 200, 210));
        gameCamera.setOrbitControls(this, airplane, 2500, 10);
    }

    //sets up the rendering panel and starts the rendering updates. 
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        requestFocusInWindow();
        learningMode = !FlightSimulator.user.getCompletedTraining();
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
            renderingPanel.addMesh(runway1.getMesh());
            renderingPanel.addMesh(runway2.getMesh());
            renderingPanel.setFPSlimit(150);
            renderingPanel.start();
            add(renderingPanel);
            validate();
        }
    }

    //the panel that contains dials
    class SidePanel extends JPanel 
    {
        private boolean[] questionChecklist; //tells the program what questions it still has to ask.
        private double randomAltitude; //a randomized altitude that the user will be questioned about
        private double randomSpeed; //a randomized speed that the user will be questioned about

        public SidePanel()
        {
            setBackground(new Color(90, 94, 97));
            setPreferredSize(new Dimension(FlightSimulator.DEFAULT_WIDTH/4, 100));
            setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

            //set up questions
            questionChecklist = new boolean[]{false, false, false, false};
            randomAltitude = Math.random()*1500 + 2500;
            randomSpeed = Math.random()*40 + 75;

            //make buttons
            Button mainMenuButton = new Button("Main Menu", 20, 150, 50);
            mainMenuButton.addActionListener(MainMenu.getMainMenuPanelSwitcher());
            Button settingsButton = new Button("Settings", 20, 150, 50);
            settingsButton.addActionListener(SettingsPanel.getSettingsSwitcher(GamePanel.name()));
            Button controlsButton = new Button("Controls", 20, 150, 50);
            controlsButton.addActionListener(ControlsPanel.getControlsSwitcher(GamePanel.name()));
            Button resetButton = new Button("Reset", 20, 150, 50);
            resetButton.addActionListener(new ResetButtonListener());    
            add(resetButton);
            add(mainMenuButton);
            add(settingsButton);
            add(controlsButton);
        }

        //repaint called in the airplane updater causes this to be called 
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            //convert to graphics 2d (used for line thickness)
            Graphics2D g2d = (Graphics2D)g;
            //draw the flight dials
            g.drawImage(flightDials, FlightSimulator.DEFAULT_WIDTH/8, 400-FlightSimulator.DEFAULT_WIDTH/8, FlightSimulator.DEFAULT_WIDTH/4, 400, 250, 15, 480, 245, this);
            g.drawImage(flightDials, 0, 400, FlightSimulator.DEFAULT_WIDTH/4, FlightSimulator.DEFAULT_WIDTH/4+400, 0, 245, 480, 728, this);
            g2d.setStroke(new BasicStroke(4));
            g2d.setColor(Color.WHITE);

            if (learningMode)
                askQuestions();

            if (learningMode)
            {
                if (airplane.hasLanded())
                landingProtocol();
                else if (airplane.hasCrashed())
                crashProtocol();
            }
            
            //draw dial pointers
            drawThrottle(g2d, 50, 230, airplane.getThrottle());
            drawDialNeedle(g2d, 252, 315, 60, airplane.orientation().y); //compass
            drawDialNeedle(g2d, 85, 487, 60, airplane.getSpeed()/75);//airspeed
            drawTurnCoordinator(g2d, 85, 659, airplane.orientation().z); //turn coordinator
            drawDialNeedle(g2d, 255, 489, 55, airplane.getAltitude()/300/Math.PI); //altimeter hundreds needle
            drawDialNeedle(g2d, 255, 489, 30, airplane.getAltitude()/3000/Math.PI); //altimeter thousands needle
            drawDialNeedle(g2d, 255, 659, 60, airplane.getVerticalClimb()/Math.PI/10 - Math.PI/2); //Vertical climb
        }

        //makes popup questions based on the checklist and asks certain questions at the right
        //times, for example asking a questions about landing while the user is landing.
        public void askQuestions()
        {
            int actualAltitude = (int)(airplane.getAltitude()*0.16976527263);
            int actualSpeed = (int)(airplane.getSpeed()/2);
            if (!questionChecklist[0])
            {
                //takeoff question
                if (actualAltitude > 50)
                {
                    questionChecklist[0] = true;
                    new QuestionPopup(
                        "Which of the following statements are true?", 
                        new String[]{"the plane is pitching up", "the plane's altitude is " + actualAltitude + "ft", "the plane is facing south", "the airspeed is 50 mph"}, 
                        new boolean[]{true, true, false, false});
                }
            }

            if (!questionChecklist[1])
            {
                if (actualAltitude > randomAltitude)
                {
                    questionChecklist[1] = true;
                    new QuestionPopup(
                        "What is your current altitude?", 
                        new String[]{"" + actualAltitude + "m", "" + actualAltitude + "ft", "" + (actualAltitude+100) + "m", "" + (actualAltitude-500) + "ft"}, 
                        new boolean[]{false, true, false, false});
                }
            }

            if (!questionChecklist[2])
            {
                if (actualSpeed > randomSpeed)
                {
                    questionChecklist[2] = true;
                    new QuestionPopup(
                        "Which of the following statements are true?", 
                        new String[]{"the plane's speed is  " + actualSpeed + " ft/minute", "the plane's speed is " + (actualSpeed-10) + " ft/minute", "the plane's speed is " + (actualSpeed+10) + " knots", "the plane's speed is " + (actualSpeed) + " knots"}, 
                        new boolean[]{false, false, false, true});
                }
            }

            if (!questionChecklist[3] && questionChecklist[0])
            {
                if (actualAltitude < 50)
                {
                    //landing question
                    questionChecklist[3] = true;
                    new QuestionPopup(
                        "What is about the optimal speed for landing?", 
                        new String[]{"60 mph", "71 knots ", "69 knots", "60 knots"}, 
                        new boolean[]{false, false, false, true});
                }
            }
        }

        //called when the plane lands and creates and opens an end frame
        public void landingProtocol()
        {
            if (!ended)
            {
                new EndFrame(false, wrongAmount <= 2);
                ended = true;
            }
            pause();
        }

        //called when the plane crashes and opens an end frame 
        public void crashProtocol()
        {
            if (!ended)
            {
                new EndFrame(true, false);
                ended = true;
            }
            pause();
        }

        //draws a pointer needle for a flight dial centered at the specified coordinates and rotated 
        //a specified amount. 
        public void drawDialNeedle(Graphics2D g2d, int centerX, int centerY, int length, double rotation)
        {
            rotation -= Math.PI/2;
            int endPointX;
            int endPointY;

            endPointX = centerX+(int)(Math.cos(rotation)*length);
            endPointY = centerY+(int)(Math.sin(rotation)*length);

            g2d.drawLine(centerX, centerY, endPointX, endPointY);
        }

        //similar to the dial needles, just two and sideways for the airplane's turn coordinator
        public void drawTurnCoordinator(Graphics2D g2d, int centerX, int centerY, double rotation)
        {
            int endPointX;
            int endPointY;

            endPointX = (int)(Math.cos(-rotation)*50);
            endPointY = (int)(Math.sin(-rotation)*50);
            g2d.drawLine(centerX, centerY, centerX+endPointX, centerY+endPointY);
            g2d.drawLine(centerX, centerY, centerX-endPointX, centerY-endPointY);
        }

        //draws the throttle using two little rectangles and a string which labels the throttle.
        public void drawThrottle(Graphics2D g2d, int topLeftX, int topLeftY, double throttleAmt)
        {
            throttleAmt = 1-throttleAmt;
            g2d.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 30));
            g2d.drawString("Throttle", topLeftX + 20, topLeftY);
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRect(topLeftX, topLeftY, 10, 70);
            g2d.setColor(Color.WHITE);
            g2d.drawRect(topLeftX, (int)(topLeftY + (70*throttleAmt)) - 10, 10, 10);
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

    //toggles the pause used by the key pressed escape key
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

    //pauses the game by stopping the physics and the rendering panel
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

    //inpuases the game by resuming physics and rendering
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

    //applies the user prefferences into reality by setting the fov and sensitivity.
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

    //neccessary for key input to work
    public void mousePressed(MouseEvent e) 
    {
        requestFocusInWindow();
    }
    public void mouseReleased(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    //automatically unpuases if the game panel gains focus, but also updates settings
    //incase the user just returned from the settings panel
    public void focusGained(FocusEvent e) 
    {
        unpause();
        repaint();
        updateSettings();
    }

    //automatically pauses if focus is lost.
    public void focusLost(FocusEvent e) 
    {
        repaint();
        pause();
    }
    public void mouseEntered(MouseEvent e) {}

    //the endframe pops up after a landing attempt to tell the user their test results.
    class EndFrame extends JFrame
    {
        //boolean crashed and passed tells the end frame how to respond.
        public EndFrame(boolean crashed, boolean passed)
        {
            setVisible(true);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setSize(800, 680);
            setBackground(Color.WHITE);
            setLayout(new FlowLayout(FlowLayout.CENTER, 200, 30));
            JLabel title;
            JTextArea description = new JTextArea(4, 30);
            description.setEditable(false);
            description.setLineWrap(true);
            description.setWrapStyleWord(true);
            description.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 20));
            Button button = new Button("close", 30, 500, 50);
            if (crashed)
            {
                title = new JLabel("CRASHED");
                description.setText("You crashed! please go over the instructions again, particularly slide 5 about landing");
                button.setText("Instruction Slides");
                button.setPreferredSize(new Dimension(500, 50));
                button.addActionListener(InstructionPanel.getInstructionPanelSwitcher());
            }
            else
            {
                if (passed)
                {
                    title = new JLabel("CONGRADUATIONS!");
                    description.setText("You successfully landed the airplane and scored sufficiently on the questions! You will now have access to the \"Free Play\" mode" 
                        + " in the main menu which will be saved on your account.");
                    button.setText("Main menu");
                    button.setPreferredSize(new Dimension(200, 50));
                    FlightSimulator.user.setCompletedTraining(true);
                    button.addActionListener(MainMenu.getMainMenuPanelSwitcher());
                }
                else
                {
                    title = new JLabel("TEST FAILED");
                    description.setText("You landed the plane but got sadly got " + wrongAmount + " questions wrong. Please go over the instruction slides again and re-attempt");
                    button.setText("Instruction Slides");
                    button.setPreferredSize(new Dimension(500, 50));
                    button.addActionListener(InstructionPanel.getInstructionPanelSwitcher());
                }
            }
            title.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 60));
            
            add(title);
            add(description);
            add(button);

        }

        public void paintComponent(Graphics g)
        {
            super.paintComponents(g);
        }
    }

    //a popup frame which contains a question about flying for the user.
    //automatically choses between JRadioButtons and JCheckBoxes depending on the 
    //number of correct answers 
    class QuestionPopup extends JFrame
    {
        //the choices that the user has selected
        private boolean[] selectedChoises;

        //the correct answers 
        private boolean[] answers; 
        
        //the number of correct answers (not the user's answers)
        private int numCorrect;

        //constructs a question popup object with the specified question, answers 
        //and the correct answer.
        public QuestionPopup(String question, String[] answrs, boolean[] correctAnswers)
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

            setBackground(Color.WHITE);
            selectedChoises = new boolean[]{false, false, false, false};
            add(new QuestionPanel(question), BorderLayout.NORTH);
            add(new BottomPanel(), BorderLayout.SOUTH);
            add(new AnswerChoicePanel(answrs[0], answrs[1], answrs[2], answrs[3]), BorderLayout.CENTER);
            
        }

        public void paintComponent(Graphics g)
        {
            requestFocusInWindow();
        }

        //goes through the user's answers and the correct ones
        // if there are any differences, it is wrong.
        public boolean checkAnswers()
        {
            boolean correct = true;
            for (int i = 0; i < selectedChoises.length; i++)
            {
                if (selectedChoises[i] != answers[i])
                    correct = false;
            }
            return correct;
        }

        //closes the frame
        public void closeQuestionFrame()
        {
            dispose();
        }

        //the panel that contains the question for the user in a JTextArea
        class QuestionPanel extends JPanel 
        {
            public QuestionPanel(String questionText)
            {
                setBackground(Color.WHITE);
                JTextArea textField = new JTextArea(questionText, 2, 20);
                add(textField);
                textField.setLineWrap(true);
                textField.setWrapStyleWord(true);
                textField.setEditable(false);
                textField.setMargin(new Insets(10, 10, 10, 10));
                textField.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 20));
                textField.setForeground(Color.BLACK);
            }   
        }

        //a JPanel which contains the answer choices for the user. 
        class AnswerChoicePanel extends JPanel implements ActionListener
        {
            //takes in the four answers choises 
            public AnswerChoicePanel(String choice1Text, String choice2Text, String choice3Text, String choice4Text)
            {
                setBackground(Color.WHITE);
                setLayout(new FlowLayout(FlowLayout.LEFT, 10, 30));
                if (numCorrect > 1)
                {
                    //creates check boxes if there are multiple right answers
                    JCheckBox aCheckBox = new JCheckBox("0) " + choice1Text);
                    JCheckBox bCheckBox = new JCheckBox("1) " + choice2Text);
                    JCheckBox cCheckBox = new JCheckBox("2) " + choice3Text);
                    JCheckBox dCheckBox = new JCheckBox("3) " + choice4Text);
                    aCheckBox.addActionListener(this);
                    bCheckBox.addActionListener(this);
                    cCheckBox.addActionListener(this);
                    dCheckBox.addActionListener(this);
                    aCheckBox.setBackground(Color.WHITE);
                    bCheckBox.setBackground(Color.WHITE);
                    cCheckBox.setBackground(Color.WHITE);
                    dCheckBox.setBackground(Color.WHITE);
                    aCheckBox.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 15));
                    bCheckBox.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 15));
                    cCheckBox.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 15));
                    dCheckBox.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 15));
                    add(aCheckBox);
                    add(bCheckBox);
                    add(cCheckBox);
                    add(dCheckBox);
                }
                else
                {
                    //creates radio buttons if there is only on right answer
                    ButtonGroup radioButtonGroup = new ButtonGroup();
                    JRadioButton aButton = new JRadioButton("0) " + choice1Text);
                    JRadioButton bButton = new JRadioButton("1) " + choice2Text);
                    JRadioButton cButton = new JRadioButton("2) " + choice3Text);
                    JRadioButton dButton = new JRadioButton("3) " + choice4Text);
                    aButton.addActionListener(this);
                    bButton.addActionListener(this);
                    cButton.addActionListener(this);
                    dButton.addActionListener(this);
                    aButton.setBackground(Color.WHITE);
                    bButton.setBackground(Color.WHITE);
                    cButton.setBackground(Color.WHITE);
                    dButton.setBackground(Color.WHITE);
                    aButton.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 15));
                    bButton.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 15));
                    cButton.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 15));
                    dButton.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 15));
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

            //if any of the buttons are pressed this is called 
            //updates the selected choices array.
            public void actionPerformed(ActionEvent e) 
            {
                AbstractButton button = null;
                //make sure the source is from a button
                if (e.getSource() instanceof AbstractButton)
                    button = (AbstractButton)e.getSource();
                if (numCorrect == 1)
                    selectedChoises = new boolean[]{false, false, false, false};
                int answerChosen = Integer.parseInt(e.getActionCommand().substring(0, 1));
                selectedChoises[answerChosen] = button.isSelected();
            }
        }

        //panel at the bottom that has the check button. 
        class BottomPanel extends JPanel implements ActionListener
        {
            private Button confirmButton; //check button, which turns into close if the answer is right.
            private boolean wrong; //true if the result of the check came as wrong

            public BottomPanel()
            {
                wrong = false;
                setBackground(Color.WHITE);
                setLayout(new FlowLayout());
                confirmButton = new Button("Check", 30, 200, 50);
                setPreferredSize(new Dimension(100, 100));
                confirmButton.addActionListener(this);
                add(confirmButton);
            }

            //if the text is check (which is default), it will check the answers. 
            //if the answers are right, then it sets the text to close and allows the 
            //user to close the question panel. Otherwise, it increments the wrong amount
            //field variable which hurts the user's score.
            public void actionPerformed(ActionEvent e)
            {
                if (confirmButton.getText().equalsIgnoreCase("close"))
                {
                    closeQuestionFrame();
                    return;
                }
                if (checkAnswers())
                {
                    new PopupFrame("Correct!");
                    confirmButton.setText("close");
                }
                else
                {
                    if (wrong == false)
                    {
                        wrongAmount++;
                        wrong = true;
                    }
                    new PopupFrame("Try again!");
                }
            }
        }
    }
}
