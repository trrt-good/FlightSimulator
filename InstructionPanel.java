import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JFrame;    
import javax.swing.JPanel;

import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Insets;
import java.util.ArrayList;

import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

import java.util.Scanner;
import java.io.FileNotFoundException;

public class InstructionPanel extends JPanel
{
    private CardLayout instructionsCardLayout;
    private JPanel instructionsPanelHolder;

    private Scanner instructionsReader;

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
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                nextSlide();
            }
        }

        class BackButtonListener implements ActionListener
        {
            @Override
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

    private void nextSlide()
    {
        instructionsCardLayout.next(instructionsPanelHolder);
        activeSlideIndex = (activeSlideIndex+1)%numberOfSlides;
    }

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

    public String getName()
    {
        return "InstructionPanel";
    }
}
