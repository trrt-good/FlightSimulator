import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;   
import javax.swing.JPanel;
import javax.swing.JTextArea;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Insets;
import java.util.ArrayList;

import java.io.File;

import java.util.Scanner;
import java.io.FileNotFoundException;

public class InstructionPanel extends JPanel
{
    //the card that holds all the instruction slides.
    private CardLayout instructionsCardLayout;
    private JPanel instructionsPanelHolder;

    //the scanner that reads the instructions text file. 
    private Scanner instructionsReader;

    //slide info
    private int activeSlideIndex; 
    private int numberOfSlides;

    //constructs an instructions panel which has a card layout for switching between
    //slides in the instructions.  
    public InstructionPanel()
    {
        ArrayList<String> instructionsText = readInstructionsText();
        numberOfSlides = instructionsText.size();
        activeSlideIndex = 0;
        setLayout(new BorderLayout());
        instructionsCardLayout = new CardLayout();
        instructionsPanelHolder = new JPanel();
        instructionsPanelHolder.setLayout(instructionsCardLayout);
        InstructionChangerPanel instructionChangerPanel = new InstructionChangerPanel();
        add(instructionChangerPanel, BorderLayout.NORTH);
        add(instructionsPanelHolder, BorderLayout.CENTER);
        for (int i = 0; i < instructionsText.size(); i++)
        {
            InstructionSlide slide = new InstructionSlide(instructionsText.get(i));
            instructionsPanelHolder.add(slide, "" + i);
        }
        instructionsCardLayout.show(instructionsPanelHolder, "" + activeSlideIndex);
    }  

    //this jpanel is the top panel which is used to switch between instruction slides
    class InstructionChangerPanel extends JPanel
    {
        public InstructionChangerPanel()
        {
            setLayout(new BorderLayout());
            Button backButton = new Button("Back", 30, 200, 50);
            backButton.setBackground(new Color(184, 71, 42));
            Button nextButton = new Button("Next", 30, 200, 50);
            add(backButton, BorderLayout.WEST);
            add(nextButton, BorderLayout.EAST);

            backButton.addActionListener(new BackButtonListener());
            nextButton.addActionListener(new NextButtonListener());
        }

        class NextButtonListener implements ActionListener
        {
             
            public void actionPerformed(ActionEvent e) 
            {
                nextSlide();
            }
        }

        class BackButtonListener implements ActionListener
        {
             
            public void actionPerformed(ActionEvent e) 
            {
                prevSlide();
            }
        }
    }

    //this Jpanel is a slide for the instructions 
    class InstructionSlide extends JPanel
    {
        public InstructionSlide(String text)
        {
            setLayout(new GridLayout(2, 1));
            JTextArea textArea = new JTextArea();
            textArea.setText(text);
            //TODO: Read instructions for each slid from a text file
            textArea.setEnabled(false);
            textArea.setFont(new Font(FlightSimulator.FONTSTYLE, Font.PLAIN, 20));
            textArea.setForeground(Color.BLACK);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setMargin(new Insets(20, 20, 20, 20));
            PicturePanel pictPanel = new PicturePanel();
            add(pictPanel);
            add(textArea);
        }

        class PicturePanel extends JPanel
        {
            public void paintComponent()
            {
                
            }
        }
    }

    //moves to the next slide
    private void nextSlide()
    {
        instructionsCardLayout.next(instructionsPanelHolder);
        activeSlideIndex = (activeSlideIndex+1)%numberOfSlides;
    }

    //changes to the prev slide, unless on the first slide where it moves back to the main menu
    private void prevSlide()
    {
        if (activeSlideIndex == 0)
        {
            FlightSimulator.flightSim.showPanel("MainMenu");
        }
        else
        {
            instructionsCardLayout.previous(instructionsPanelHolder);
            activeSlideIndex -= 1;
        }
    }

    //reads the instructions in the text file
    private ArrayList<String> readInstructionsText()
    {
        ArrayList<String> instructions = new ArrayList<String>();
        try 
        {
            instructionsReader = new Scanner(new File(FlightSimulator.RESOURCES_FOLDER, "instructions.txt"));
        } 
        catch (FileNotFoundException e) 
        {
            System.out.println("Couldnt find file");
            e.printStackTrace();
        }
        String line = "";
        line = instructionsReader.nextLine();
        while (instructionsReader.hasNextLine())
        {
            if (line.startsWith("#"))
            {
                String textBlock = "";
                while(instructionsReader.hasNextLine() && !(line = instructionsReader.nextLine()).startsWith("#"))
                {
                    textBlock += line + "\n";
                }
                instructions.add(textBlock);
            }
        }
        return instructions;
    }

    public static String name()
    {
        return "InstructionPanel";
    }

    public static SwitchToInstructionPanelListener getInstructionPanelSwitcher()
    {
        return new SwitchToInstructionPanelListener();
    }

    static class SwitchToInstructionPanelListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e) 
        {
            FlightSimulator.flightSim.showPanel(name());
        }
    }
}
