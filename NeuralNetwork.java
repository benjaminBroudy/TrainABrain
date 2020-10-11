

import java.awt.Color;
import java.awt.Font;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.awt.*;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

public class NeuralNetwork {

    public static double[][] trainingData;

    public static double[][] correctOutputs;

    public static int amountInput;
    public static int amountHidden;
    public static int amountOutput;

    public static double[] inputNeurons;
    public static double[] hiddenNeurons;
    public static double[] outputNeurons;

    public static double[][] inputToHidden;
    public static double[][] hiddenToOutput;

    public static double[] hiddenNeuronBias;
    public static double[] outputNeuronBias;

    public static double learningRate;
    public static double reps;

    public static double[] outputError;
    public static double[] hiddenError;

    public static String inputTrainingDataFilepath;
    public static String outputTrainingDataFilepath;

    public static String[] outputNames;

    public static String filepathTrain;

    public static JFrame jframe;
    public static JButton newButton;
    public static JButton runButton;
    public static JButton trainButton;
    public static JTextArea startingName;
    public static JTextArea startingBackground;
    public static boolean done2;

    public static Color backgroundColor;

    public static JButton trainData;
    public static JTextArea trainFile;

    public static JButton inputData;
    public static JTextArea inputFile;

    public static JButton outputData;
    public static JTextArea outputFile;

    public static JScrollPane inputFileScroll;
    public static JScrollPane outputFileScroll;
    public static JScrollPane trainFileScroll;

    public static JTextArea hiddenNeuronTitle;
    public static JTextArea hiddenNeuronInput;
    public static JScrollPane hiddenNeuronInputScroll;

    public static JTextArea repsTitle;
    public static JTextArea repsInput;
    public static JScrollPane repsInputScroll;

    public static JTextArea learningRateTitle;
    public static JTextArea learningRateInput;
    public static JScrollPane learningRateInputScroll;

    public static JTextArea newTitle;

    public static JButton helpButton;
    public static JButton submitButton;

    public static String[] restOfCommand;

    public static String s;
    public static boolean saveNetwork;
    public static boolean yesOrNo;
    public static boolean done;
    public static boolean inputTxt;
    public static String filepath;

    public static JButton returnToHome;
    public static boolean isReturn;
    public static boolean bool;
    public static ArrayList<Integer> highestNeurons;
    public static ImageIcon img;

    public static void main(String[] args) throws IOException {

        boolean running = true;
        img = new ImageIcon(ClassLoader.getSystemResource("icon.png"));
        JFrame errorframe = new JFrame("Error!");
        errorframe.setIconImage(img.getImage());
        isReturn = false;

        returnToHome = new JButton("Return To Start");
        returnToHome.setSize(100, 30);
        returnToHome.setFont(new Font("Times New Roman", Font.PLAIN, 9));

        while (running) {

            try {

                run();

            } catch (Exception e) {

                if (isReturn) {

                    isReturn = false;

                } else {

                    JOptionPane.showMessageDialog(errorframe, e.getStackTrace());

                }

            }

        }

    }

    public static void run() throws IOException {

        jframe = new JFrame("Train A Brain");
        jframe.setIconImage(img.getImage());
        backgroundColor = jframe.getBackground();
        s = "";
        restOfCommand = new String[6];

        initializeFrame();

        jframe.setVisible(true);
        jframe.setLocationRelativeTo(null);

        amountHidden = 4;
        learningRate = 0.2;
        reps = 1000000;

        String command = initialSetup();

        if (command.equals("new") || command.equals("train")) {

            if (command.equals("train")) {

                runNeuralNet(filepathTrain);

            } else {

                dataExtractionFromFile();

                amountInput = trainingData[0].length;
                amountOutput = correctOutputs[0].length;

                inputNeurons = new double[amountInput];
                hiddenNeurons = new double[amountHidden];
                outputNeurons = new double[amountOutput];

                inputToHidden = new double[amountInput][amountHidden];
                hiddenToOutput = new double[amountHidden][amountOutput];

                hiddenNeuronBias = new double[amountHidden];
                outputNeuronBias = new double[amountOutput];

            }

            outputError = new double[amountOutput];
            hiddenError = new double[amountHidden];

            if (command.equals("new")) {

                randomize();

            } else {

                dataExtractionFromFile();

            }

            for (int i = 0; i < reps; i++) {

                for (int j = 0; j < trainingData.length; j++) {

                    calculate(trainingData[j]);

                    backPropagate(j);

                }

            }

            wouldYouLikeToSave();

            if (saveNetwork) {

                saved();

            }

        } else if (command.equals("run")) {

            boolean running = true;

            while (running) {

                String[] inputs = runNetDataInputPage();

                boolean wantAnotherInput = false;

                for (int i = 0; i < amountInput; i++) {

                    inputNeurons[i] = Double.parseDouble(inputs[i]);

                }

                calculate(inputNeurons);

                double highest = -1;

                highestNeurons = new ArrayList<Integer>();

                for (int i = 0; i < amountOutput; i++) {

                    if (outputNeurons[i] > highest) {

                        highest = outputNeurons[i];
                        highestNeurons.clear();
                        highestNeurons.add(i);

                    } else if (outputNeurons[i] == highest) {

                        highest = outputNeurons[i];
                        highestNeurons.add(i);

                    }

                }

                showOutputs(highestNeurons);

                wantAnotherInput = doYouWantAnotherInput();

                if (wantAnotherInput) {

                    running = true;

                } else {

                    running = false;

                }

            }

        }

    }

    public static void randomize() {

        Random rd = new Random();

        for (int i = 0; i < inputToHidden.length; i++) {

            for (int j = 0; j < inputToHidden[i].length; j++) {

                double num = rd.nextDouble();
                num = num * 100;
                num = Math.round(num);

                inputToHidden[i][j] = (num / 100);

            }

        }

        for (int i = 0; i < hiddenToOutput.length; i++) {

            for (int j = 0; j < hiddenToOutput[i].length; j++) {

                double num = rd.nextDouble();
                num = num * 100;
                num = Math.round(num);

                hiddenToOutput[i][j] = (num / 100);

            }

        }

        for (int i = 0; i < hiddenNeuronBias.length; i++) {

            double num = rd.nextDouble();
            num = num * 100;
            num = Math.round(num);

            hiddenNeuronBias[i] = (num / 100);

        }

        for (int i = 0; i < outputNeuronBias.length; i++) {

            double num = rd.nextDouble();
            num = num * 100;
            num = Math.round(num);

            outputNeuronBias[i] = (num / 100);

        }

    }

    public static void calculate(double[] inputs) {

        for (int i = 0; i < inputs.length; i++) {

            inputNeurons[i] = inputs[i];

        }

        for (int i = 0; i < amountHidden; i++) {

            for (int j = 0; j < inputToHidden.length; j++) {

                hiddenNeurons[i] += inputNeurons[j] * inputToHidden[j][i];

            }

            hiddenNeurons[i] += hiddenNeuronBias[i];
            hiddenNeurons[i] = sigmoid(hiddenNeurons[i]);

        }

        for (int i = 0; i < amountOutput; i++) {

            for (int j = 0; j < hiddenToOutput.length; j++) {

                outputNeurons[i] += hiddenNeurons[j] * hiddenToOutput[j][i];

            }

            outputNeurons[i] += outputNeuronBias[i];
            outputNeurons[i] = sigmoid(outputNeurons[i]);

        }

    }

    public static void backPropagate(int dataNumber) {

        for (int i = 0; i < amountOutput; i++) {

            outputError[i] = (correctOutputs[dataNumber][i] - outputNeurons[i]) * sigmoidDerivative(outputNeurons[i]);

        }

        for (int i = 0; i < amountHidden; i++) {

            hiddenError[i] = 0.0;

            for (int j = 0; j < amountOutput; j++) {

                hiddenError[i] += outputError[j] * hiddenToOutput[i][j];

            }

            hiddenError[i] *= sigmoidDerivative(hiddenNeurons[i]);

        }

        for (int i = 0; i < amountOutput; i++) {

            for (int j = 0; j < amountHidden; j++) {

                hiddenToOutput[j][i] += (learningRate * outputError[i] * hiddenNeurons[j]);

            }

            outputNeuronBias[i] += (learningRate * outputError[i]);

        }

        for (int i = 0; i < amountHidden; i++) {

            for (int j = 0; j < amountInput; j++) {

                inputToHidden[j][i] += (learningRate * hiddenError[i] * inputNeurons[j]);

            }

            hiddenNeuronBias[i] += (learningRate * hiddenError[i]);

        }

    }

    public static double sigmoid(double num) {

        return (1.0 / (1.0 + Math.exp(-num)));

    }

    public static double sigmoidDerivative(double num) {

        return (num * (1.0 - num));

    }

    public static String initialSetup() throws IOException {

        boolean running = true;

        String command1 = "";

        while (running) {

            String[] command = new String[7];

            command[0] = addFirstButtonFunctionality();

            secondPage();

            for (int i = 1; i < command.length; i++) {

                command[i] = restOfCommand[i - 1];

            }

            command1 = command[0].substring(0, command[0].length() - 1);

            String currentCommand = "";

            if (command[0].substring(0, command[0].length() - 1).equals("new") || command[0].equals("train ")) {

                for (int i = 1; i < command.length - 1; i++) {
                    
                    currentCommand = command[i].substring(0, command[i].length() - 1);

                    if (i == 1) {

                        inputTrainingDataFilepath = currentCommand;

                    } else if (i == 2) {

                        outputTrainingDataFilepath = currentCommand;

                    } else if (i == 3) {

                        amountHidden = Integer.parseInt(currentCommand);

                    } else if (i == 4) {

                        learningRate = Double.parseDouble(currentCommand);

                    } else if (i == 5) {

                        if (command[0].equals("new ")) {

                            reps = Integer.parseInt(command[5]);

                        } else {

                            reps = Integer.parseInt(currentCommand);
                            filepathTrain = command[6];

                        }

                        running = false;

                    }

                }

            } else if (command[0].equals("run ")) {

                runNeuralNet(command[1]);
                running = false;

            }

        }

        return command1;

    }

    public static void dataExtractionFromFile() throws IOException {

        String input = Scan.scan(inputTrainingDataFilepath);

        input = input.replaceAll(" ", "");
        input = input.replaceAll("\\}", "");
        input = input.replaceAll("\\{", "");

        String[] inputs = input.split(";");

        String[][] inputsFinal = new String[inputs.length][inputs[1].split(",").length];

        trainingData = new double[inputsFinal.length][inputsFinal[1].length];

        for (int i = 0; i < inputsFinal.length; i++) {

            inputsFinal[i] = inputs[i].split(",");

        }

        for (int i = 1; i < inputsFinal.length; i++) {

            for (int j = 0; j < inputsFinal[1].length; j++) {

                trainingData[i - 1][j] = Double.parseDouble(inputsFinal[i][j]);

            }

        }

        outputNames = inputsFinal[0];

        String output = Scan.scan(outputTrainingDataFilepath);

        output = output.replaceAll(" ", "");
        output = output.replaceAll("\\}", "");
        output = output.replaceAll("\\{", "");

        String[] outputs = output.split(";");

        String[][] outputsFinal = new String[outputs.length][outputs[1].split(",").length];
        
        correctOutputs = new double[outputsFinal.length][outputsFinal[1].length];

        for (int i = 0; i < outputsFinal.length; i++) {

            outputsFinal[i] = outputs[i].split(",");

        }

        for (int i = 1; i < outputsFinal.length; i++) {

            for (int j = 0; j < outputsFinal[1].length; j++) {

                correctOutputs[i - 1][j] = Double.parseDouble(outputsFinal[i][j]);

            }

        }

    }

    public static void save(String filepath) throws IOException {

        String str = "";

        // input to hidden, hidden to output, hidden bias, output bias, output names (0
        // - x)

        // input to hidden
        for (int i = 0; i < inputToHidden.length; i++) {

            for (int j = 0; j < inputToHidden[0].length; j++) {

                if (j == (inputToHidden[0].length - 1)) {

                    str = str + inputToHidden[i][j];

                } else {

                    str = str + inputToHidden[i][j] + ",";

                }

            }

            if (!(i == (inputToHidden.length - 1))) {

                str = str + ";";

            }

        }

        str = str + ":";

        // hidden to output
        for (int i = 0; i < hiddenToOutput.length; i++) {

            for (int j = 0; j < hiddenToOutput[0].length; j++) {

                if (j == (hiddenToOutput[0].length - 1)) {

                    str = str + hiddenToOutput[i][j];

                } else {

                    str = str + hiddenToOutput[i][j] + ",";

                }

            }

            if (!(i == (hiddenToOutput.length - 1))) {

                str = str + ";";

            }

        }

        str = str + ":";

        // hidden bias
        for (int i = 0; i < hiddenNeuronBias.length; i++) {

            if (i == (hiddenNeuronBias.length - 1)) {

                str = str + hiddenNeuronBias[i];

            } else {

                str = str + hiddenNeuronBias[i] + ",";

            }

        }

        str = str + ":";

        // output bias
        for (int i = 0; i < outputNeuronBias.length; i++) {

            if (i == (outputNeuronBias.length - 1)) {

                str = str + outputNeuronBias[i];

            } else {

                str = str + outputNeuronBias[i] + ",";

            }

        }

        str = str + ":";

        // output names (0 - x)
        for (int i = 0; i < outputNames.length; i++) {

            if (i == (outputNames.length - 1)) {

                str = str + outputNames[i];

            } else {

                str = str + outputNames[i] + ",";

            }

        }

        Writer.write(filepath, str);

    }

    public static void wouldYouLikeToSave() throws IOException {

        String input = "";

        jframe.setVisible(false);
        jframe.setLocationRelativeTo(null);

        saveScreen();

        if (saveNetwork) {

            input = getSaveFilePath();

        }

        boolean output = true;
        boolean fileEmpty = false;

        while (output) {

            if (saveNetwork) {

                String wantedFile = Scan.scan(input);

                if (wantedFile.equals("")) {

                    fileEmpty = true;

                } else {

                    boolean decision = fileNotEmptyOverwrite();

                    boolean deciding = true;

                    while (deciding) {

                        if (decision) {

                            fileEmpty = true;
                            deciding = false;

                        } else {

                            wouldYouLikeToSave();
                            fileEmpty = false;
                            deciding = false;

                        } 

                    }

                }

                if (fileEmpty) {

                    try {

                        save(input);

                    } catch (Exception e) {

                        boolean output1 = error();

                        if (output1) {

                            wouldYouLikeToSave();

                        } else {

                            System.exit(0);

                        }

                    }

                }

                output = false;

            } else {

                output = false;
                System.exit(0);

            }

        }

    }

    public static void runNeuralNet(String filepath) throws IOException {

        String str = Scan.scan(filepath);

        String[] splitStr = str.split(":");

        inputToHidden = new double[splitStr[0].split(";").length][splitStr[0].split(";")[0].split(",").length];

        for (int i = 0; i < splitStr[0].split(";").length; i++) {

            for (int j = 0; j < splitStr[0].split(";")[0].split(",").length; j++) {

                inputToHidden[i][j] = Double.parseDouble(splitStr[0].split(";")[i].split(",")[j]);

            }

        }

        hiddenToOutput = new double[splitStr[1].split(";").length][splitStr[1].split(";")[0].split(",").length];

        for (int i = 0; i < splitStr[1].split(";").length; i++) {

            for (int j = 0; j < splitStr[1].split(";")[0].split(",").length; j++) {

                hiddenToOutput[i][j] = Double.parseDouble(splitStr[1].split(";")[i].split(",")[j]);

            }

        }

        inputNeurons = new double[inputToHidden.length];
        hiddenNeurons = new double[hiddenToOutput.length];
        outputNeurons = new double[hiddenToOutput[0].length];

        amountInput = inputNeurons.length;
        amountHidden = hiddenNeurons.length;
        amountOutput = outputNeurons.length;

        hiddenNeuronBias = new double[splitStr[2].split(",").length];
        outputNeuronBias = new double[splitStr[3].split(",").length];

        outputNames = new String[splitStr[4].split(",").length];

        for (int i = 0; i < splitStr[2].split(",").length; i++) {

            hiddenNeuronBias[i] = Double.parseDouble(splitStr[2].split(",")[i]);

        }

        for (int i = 0; i < splitStr[3].split(",").length; i++) {

            outputNeuronBias[i] = Double.parseDouble(splitStr[3].split(",")[i]);

        }

        outputNames = splitStr[4].split(",");

    }

    public static void initializeFrame() throws IOException {

        jframe.setResizable(false);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        newButton = new JButton("New");
        newButton.setBounds(30, 350, 100, 40);
        jframe.add(newButton);

        runButton = new JButton("Run");
        runButton.setBounds(160, 350, 100, 40);
        jframe.add(runButton);

        trainButton = new JButton("Cont. Train");
        trainButton.setBounds(290, 350, 100, 40);
        jframe.add(trainButton);

        startingName = new JTextArea();
        startingName.setEditable(false);
        startingName.setText("Train\n       A\n         Brain!");
        startingName.setFont(new Font("Denmark", Font.PLAIN, 50));
        startingName.setOpaque(false);
        startingName.setBounds(82, 75, 265, 200);
        jframe.add(startingName);

        startingBackground = new JTextArea();
        startingBackground.setEditable(false);
        startingBackground.setText("01010110010101011101010101110111010101011010101011001010101110101010111011101010101110\n101010100011001011001010101010100001010101010100101000100111110001010101010101000101110\n001110010101010101001110000101010100110101001110010110101010001010111010101101110011101\n01010110010101011101010101110111010101011010101011001010101110101010111011101010101110\n101010100011001011001010101010100001010101010100101000100111110001010101010101000101110\n001110010101010101001110000101010100110101001110010110101010001010111010101101110011101\n01010110010101011101010101110111010101011010101011001010101110101010111011101010101110\n101010100011001011001010101010100001010101010100101000100111110001010101010101000101110\n001110010101010101001110000101010100110101001110010110101010001010111010101101110011101\n01010110010101011101010101110111010101011010101011001010101110101010111011101010101110\n101010100011001011001010101010100001010101010100101000100111110001010101010101000101110\n001110010101010101001110000101010100110101001110010110101010001010111010101101110011101\n01010110010101011101010101110111010101011010101011001010101110101010111011101010101110\n101010100011001011001010101010100001010101010100101000100111110001010101010101000101110\n001110010101010101001110000101010100110101001110010110101010001010111010101101110011101\n01010110010101011101010101110111010101011010101011001010101110101010111011101010101110\n101010100011001011001010101010100001010101010100101000100111110001010101010101000101110\n001110010101010101001110000101010100110101001110010110101010001010111010101101110011101\n01010110010101011101010101110111010101011010101011001010101110101010111011101010101110\n101010100011001011001010101010100001010101010100101000100111110001010101010101000101110\n001110010101010101001110000101010100110101001110010110101010001010111010101101110011101\n01010110010101011101010101110111010101011010101011001010101110101010111011101010101110\n101010100011001011001010101010100001010101010100101000100111110001010101010101000101110\n01010110010101011101010101110111010101011010101011001010101110101010111011101010101110\n101010100011001011001010101010100001010101010100101000100111110001010101010101000101110\n001110010101010101001110000101010100110101001110010110101010001010111010101101110011101\n01010110010101011101010101110111010101011010101011001010101110101010111011101010101110\n101010100011001011001010101010100001010101010100101000100111110001010101010101000101110\n01010110010101011101010101110111010101011010101011001010101110101010111011101010101110\n101010100011001011001010101010100001010101010100101000100111110001010101010101000101110\n001110010101010101001110000101010100110101001110010110101010001010111010101101110011101\n01010110010101011101010101110111010101011010101011001010101110101010111011101010101110\n101010100011001011001010101010100001010101010100101000100111110001010101010101000101110\n001110010101010101001110000101010100110101001110010110101010001010111010101101110011101\n01010110010101011101010101110111010101011010101011001010101110101010111011101010101110\n101010100011001011001010101010100001010101010100101000100111110001010101010101000101110\n001110010101010101001110000101010100110101001110010110101010001010111010101101110011101\n001110010101010101001110000101010100110101001110010110101010001010111010101101110011101\n001110010101010101001110000101010100110101001110010110101010001010111010101101110011101\n");
        startingBackground.setFont(new Font("Times New Roman", Font.PLAIN, 10));
        startingBackground.setOpaque(false);
        startingBackground.setBounds(0, 0, 430, 450);
        jframe.add(startingBackground);

        jframe.setSize(430, 450);
        jframe.setLayout(null);

    }

    public static String addFirstButtonFunctionality() {

        s = "";
        done2 = true;
        Object obj = new Object();

        newButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                s = "new ";

                synchronized (obj) {

                    obj.notify();

                }

            }

        });

        runButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                s = "run ";

                synchronized (obj) {

                    obj.notify();

                }

            }

        });

        trainButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                s = "train ";

                synchronized (obj) {

                    obj.notify();

                }

            }

        });

        synchronized (obj) {

            try {

                obj.wait();

            } catch (InterruptedException e) {



            }

        }

        return s;

    }

    public static void secondPage() {

        if (s.equals("new ")) {

            newNetScreen();

        } else if (s.equals("run ")) {

            runNetScreen();

        } else if (s.equals("train ")) {

            trainNetScreen();

        }

    }

    public static void newNetScreen() {

        Object obj = new Object();

        jframe.getContentPane().removeAll();

        jframe.setSize(430, 450);

        returnToHome.setBounds(158, 365, 100, 30);
        jframe.add(returnToHome);

        newTitle = new JTextArea();
        newTitle.setBounds(25, 25, 375, 40);
        newTitle.setText("         New Neural Network Page; Please Enter Info\n     Press \"help\" button for information about this page.");
        newTitle.setFont(new Font("Times New Roman", Font.BOLD, 15));
        newTitle.setEditable(false);
        newTitle.setOpaque(false);
        jframe.add(newTitle);

        inputData = new JButton("Input Data");
        inputData.setBounds(25, 90, 105, 40);
        jframe.add(inputData);

        inputFile = new JTextArea();
        inputFile.setEditable(false);
        inputFileScroll = new JScrollPane(inputFile);
        inputFileScroll.setBounds(155, 93, 235, 34);
        inputFileScroll.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        jframe.add(inputFileScroll);
        
        outputData = new JButton("Output Data");
        outputData.setBounds(25, 160, 105, 40);
        jframe.add(outputData);

        outputFile = new JTextArea();
        outputFile.setEditable(false);
        outputFileScroll = new JScrollPane(outputFile);
        outputFileScroll.setBounds(155, 163, 235, 34);
        outputFileScroll.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        jframe.add(outputFileScroll);

        hiddenNeuronTitle = new JTextArea();
        hiddenNeuronTitle.setBounds(25, 225, 105, 40);
        hiddenNeuronTitle.setFont(new Font("Times New Roman", Font.BOLD, 12));
        hiddenNeuronTitle.setText("Hidden Neurons:");
        hiddenNeuronTitle.setEditable(false);
        hiddenNeuronTitle.setOpaque(false);
        jframe.add(hiddenNeuronTitle);

        hiddenNeuronInput = new JTextArea();
        hiddenNeuronInputScroll = new JScrollPane(hiddenNeuronInput);
        hiddenNeuronInputScroll.setBounds(25, 250, 105, 40);
        hiddenNeuronInput.setFont(new Font("Times New Roman", Font.BOLD, 27));
        jframe.add(hiddenNeuronInputScroll);

        repsTitle = new JTextArea();
        repsTitle.setBounds(155, 225, 105, 40);
        repsTitle.setFont(new Font("Times New Roman", Font.BOLD, 12));
        repsTitle.setText("Repetitions/Epochs:");
        repsTitle.setEditable(false);
        repsTitle.setOpaque(false);
        jframe.add(repsTitle);

        repsInput = new JTextArea();
        repsInputScroll = new JScrollPane(repsInput);
        repsInputScroll.setBounds(155, 250, 105, 40);
        repsInput.setFont(new Font("Times New Roman", Font.BOLD, 27));
        jframe.add(repsInputScroll);

        learningRateTitle = new JTextArea();
        learningRateTitle.setBounds(285, 225, 105, 40);
        learningRateTitle.setFont(new Font("Times New Roman", Font.BOLD, 12));
        learningRateTitle.setText("Learnging Rate:");
        learningRateTitle.setEditable(false);
        learningRateTitle.setOpaque(false);
        jframe.add(learningRateTitle);

        learningRateInput = new JTextArea();
        learningRateInputScroll = new JScrollPane(learningRateInput);
        learningRateInputScroll.setBounds(285, 250, 105, 40);
        learningRateInput.setFont(new Font("Times New Roman", Font.BOLD, 27));
        jframe.add(learningRateInputScroll);

        helpButton = new JButton("Help!");
        helpButton.setBounds(332, 365, 60, 30);
        helpButton.setFont(new Font("Times New Roman", Font.PLAIN, 9));
        jframe.add(helpButton);

        submitButton = new JButton("Submit");
        submitButton.setBounds(25, 365, 60, 30);
        submitButton.setFont(new Font("Times New Roman", Font.PLAIN, 9));
        jframe.add(submitButton);

        // 417, 415

        inputData.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                JFileChooser inputData = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                int returnValue = inputData.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {

                    File selectedFile = inputData.getSelectedFile();

                    inputFile.setText(selectedFile.getAbsolutePath());
                    restOfCommand[0] = inputFile.getText() + " ";
                    jframe.getContentPane().revalidate();
                    jframe.getContentPane().repaint();

                }

            }

        });

        outputData.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                JFileChooser outputData = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                int returnValue = outputData.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {

                    File selectedFile = outputData.getSelectedFile();

                    outputFile.setText(selectedFile.getAbsolutePath());
                    restOfCommand[1] = outputFile.getText() + " ";
                    jframe.getContentPane().revalidate();
                    jframe.getContentPane().repaint();

                }

            }

        });

        helpButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                JFrame helpFrame = new JFrame("Help!");
                helpFrame.setIconImage(img.getImage());
                JTextArea text = new JTextArea();
                JScrollPane helpScroll = new JScrollPane(text);

                helpFrame.setSize(500, 600);
                helpFrame.setResizable(false);
                helpFrame.setLocationRelativeTo(null);

                text.setBounds(25, 25, 100, 100);
                text.setText("	-How Should I Set Up My Input and Output Files?\n\n	     First of all, what is \"input\" and \"output?\"\n	          Well... this is the training data, the data that shows the network\n	          how to learn. For the nth position of the input data, the nth position\n	          of the output file is the answer to the \"question\" of the input data.\n\n	          The following example is of two 1 digit binary numbers (a zero or\n	          one) as the inputs, and their sum as the output (0 + 0 = 0, 1 + 0\n	          = 1, 0 + 1 = 1, 1 + 1 = 2).\n\n	     Input File:\n	          {zero, one, two};\n	          {0, 0}; \n	          {1, 0}; \n	          {0, 1}; \n	          {1, 1}\n\n	     Output File:\n	          {zero, one, two};\n	          {1, 0, 0};\n	          {0, 1, 0};\n	          {0, 1, 0};\n	          {0, 0, 1}\n\n	     So the words (zero, one, two) are the answers, in order, from top to\n	     bottom. As you can see, the output file is the answers to the input.\n	     For the first \"question\"  0 + 0 has the answer 0, that is why the \n	     answer in the output file the first answer array has a 1 in the first \n	     position, which as seen in the answer names, is \"zero,\" and the\n	     rest of the positions are marked as 0s.\n\n	     Just one more example with the previous one, to drive home how it\n	     works. For the 3rd \"question,\" it is 0 and 1, which will equal 1. so\n	     in the 3rd \"answer,\" the only position with a 1 is the center, since\n	     the first position is zero, second is one, and third is two.\n\n	     Also, the data does not have to be added together as the answer, you\n	     can put in abstract data, such as entering in all the pixels of a\n	     picture (the picture either containing an apple or orange), and\n	     having there be two answers, orange or apple, meaning the top line\n	     of each file (input and output) will start with this:\n\n	     {Orange, Apple};\n\n	     It is also important to note that each line is seperated by a semi-\n	     colon, EXCEPT the last line, which does NOT have a semicolon at the\n	     end of the line. Also, spaces and new lines have no meaning in the\n	     file, just the digits, curly brackets, commas, semicolons, and words\n	     in the first line of each file (those too have to be seperated by\n	     commas, enclosed in curly brackets, and have a semicolon at the end\n	     of the line.");
                text.setCaretPosition(0);
                text.setEditable(false);

                helpScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                helpFrame.add(helpScroll);
                helpFrame.setVisible(true);

            }

        });

        submitButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                boolean filledOut = true;
                JFrame errorframe = new JFrame("Error!");
                errorframe.setIconImage(img.getImage());

                restOfCommand[2] = hiddenNeuronInput.getText();
                restOfCommand[3] = learningRateInput.getText();
                restOfCommand[4] = repsInput.getText();

                for (int i = 0; i < 5; i++) {

                    if (restOfCommand[i] == null || restOfCommand[i].equals("")) {
                    
                        filledOut = false;

                    }

                }

                if (filledOut) {

                    synchronized (obj) {

                        obj.notify();
    
                    }

                } else {

                    JOptionPane.showMessageDialog(errorframe, "Please Fill Out All Fields");
                    
                }

            }

        });

        returnToHome.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                hiddenNeuronInput.setText("");
                isReturn = true;

                synchronized (obj) {

                    obj.notify();

                }

            }

        });

        jframe.getContentPane().revalidate();
        jframe.getContentPane().repaint();
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        synchronized (obj) {

            try {

                obj.wait();

            } catch (InterruptedException e) {



            }

        }

        restOfCommand[2] = hiddenNeuronInput.getText() + " ";
        restOfCommand[3] = learningRateInput.getText() + " ";
        restOfCommand[4] = repsInput.getText();
        jframe.dispose();

    }

    public static void trainNetScreen() {

        Object obj = new Object();

        jframe.getContentPane().removeAll();

        jframe.setSize(430, 450);

        returnToHome.setBounds(158, 365, 100, 30);
        jframe.add(returnToHome);

        newTitle = new JTextArea();
        newTitle.setBounds(25, 25, 375, 40);
        newTitle.setText("             Continue Training Neural Network Page\n   Press \"help\" button for information about this page.");
        newTitle.setFont(new Font("Times New Roman", Font.BOLD, 15));
        newTitle.setEditable(false);
        newTitle.setOpaque(false);
        jframe.add(newTitle);

        trainData = new JButton("Neural Net");
        trainData.setBounds(25, 90, 105, 40);
        jframe.add(trainData);

        trainFile = new JTextArea();
        trainFile.setEditable(false);
        trainFileScroll = new JScrollPane(trainFile);
        trainFileScroll.setBounds(155, 93, 235, 34);
        trainFileScroll.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        jframe.add(trainFileScroll);

        inputData = new JButton("Input Data");
        inputData.setBounds(25, 90+70, 105, 40);
        jframe.add(inputData);

        inputFile = new JTextArea();
        inputFile.setEditable(false);
        inputFileScroll = new JScrollPane(inputFile);
        inputFileScroll.setBounds(155, 93+70, 235, 34);
        inputFileScroll.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        jframe.add(inputFileScroll);
        
        outputData = new JButton("Output Data");
        outputData.setBounds(25, 160+70, 105, 40);
        jframe.add(outputData);

        outputFile = new JTextArea();
        outputFile.setEditable(false);
        outputFileScroll = new JScrollPane(outputFile);
        outputFileScroll.setBounds(155, 163+70, 235, 34);
        outputFileScroll.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        jframe.add(outputFileScroll);

        hiddenNeuronTitle = new JTextArea();
        hiddenNeuronTitle.setBounds(25, 225+60, 105, 40);
        hiddenNeuronTitle.setFont(new Font("Times New Roman", Font.BOLD, 12));
        hiddenNeuronTitle.setText("Hidden Neurons:");
        hiddenNeuronTitle.setEditable(false);
        hiddenNeuronTitle.setOpaque(false);
        jframe.add(hiddenNeuronTitle);

        hiddenNeuronInput = new JTextArea();
        hiddenNeuronInputScroll = new JScrollPane(hiddenNeuronInput);
        hiddenNeuronInputScroll.setBounds(25, 250+60, 105, 40);
        hiddenNeuronInput.setFont(new Font("Times New Roman", Font.BOLD, 27));
        jframe.add(hiddenNeuronInputScroll);

        repsTitle = new JTextArea();
        repsTitle.setBounds(155, 225+60, 105, 40);
        repsTitle.setFont(new Font("Times New Roman", Font.BOLD, 12));
        repsTitle.setText("Repetitions/Epochs:");
        repsTitle.setEditable(false);
        repsTitle.setOpaque(false);
        jframe.add(repsTitle);

        repsInput = new JTextArea();
        repsInputScroll = new JScrollPane(repsInput);
        repsInputScroll.setBounds(155, 250+60, 105, 40);
        repsInput.setFont(new Font("Times New Roman", Font.BOLD, 27));
        jframe.add(repsInputScroll);

        learningRateTitle = new JTextArea();
        learningRateTitle.setBounds(285, 225+60, 105, 40);
        learningRateTitle.setFont(new Font("Times New Roman", Font.BOLD, 12));
        learningRateTitle.setText("Learnging Rate:");
        learningRateTitle.setEditable(false);
        learningRateTitle.setOpaque(false);
        jframe.add(learningRateTitle);

        learningRateInput = new JTextArea();
        learningRateInputScroll = new JScrollPane(learningRateInput);
        learningRateInputScroll.setBounds(285, 250+60, 105, 40);
        learningRateInput.setFont(new Font("Times New Roman", Font.BOLD, 27));
        jframe.add(learningRateInputScroll);

        helpButton = new JButton("Help!");
        helpButton.setBounds(332, 365, 60, 30);
        helpButton.setFont(new Font("Times New Roman", Font.PLAIN, 9));
        jframe.add(helpButton);

        submitButton = new JButton("Submit");
        submitButton.setBounds(25, 365, 60, 30);
        submitButton.setFont(new Font("Times New Roman", Font.PLAIN, 9));
        jframe.add(submitButton);

        // 417, 415

        inputData.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                JFileChooser inputData = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                int returnValue = inputData.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {

                    File selectedFile = inputData.getSelectedFile();

                    inputFile.setText(selectedFile.getAbsolutePath());
                    restOfCommand[0] = inputFile.getText() + " ";
                    jframe.getContentPane().revalidate();
                    jframe.getContentPane().repaint();

                }

            }

        });

        outputData.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                JFileChooser outputData = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                int returnValue = outputData.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {

                    File selectedFile = outputData.getSelectedFile();

                    outputFile.setText(selectedFile.getAbsolutePath());
                    restOfCommand[1] = outputFile.getText() + " ";
                    jframe.getContentPane().revalidate();
                    jframe.getContentPane().repaint();

                }

            }

        });

        trainData.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                JFileChooser trainData = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                int returnValue = trainData.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {

                    File selectedFile = trainData.getSelectedFile();

                    trainFile.setText(selectedFile.getAbsolutePath());
                    restOfCommand[5] = trainFile.getText() + " ";
                    jframe.getContentPane().revalidate();
                    jframe.getContentPane().repaint();

                }

            }

        });

        helpButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                JFrame helpFrame = new JFrame("Help!");
                helpFrame.setIconImage(img.getImage());
                JTextArea text = new JTextArea();
                JScrollPane helpScroll = new JScrollPane(text);

                helpFrame.setSize(500, 600);
                helpFrame.setResizable(false);
                helpFrame.setLocationRelativeTo(null);

                text.setBounds(25, 25, 100, 100);
                text.setText("	-How Should I Set Up My Input and Output Files?\n\n	     First of all, what is \"input\" and \"output?\"\n	          Well... this is the training data, the data that shows the network\n	          how to learn. For the nth position of the input data, the nth position\n	          of the output file is the answer to the \"question\" of the input data.\n\n	          The following example is of two 1 digit binary numbers (a zero or\n	          one) as the inputs, and their sum as the output (0 + 0 = 0, 1 + 0\n	          = 1, 0 + 1 = 1, 1 + 1 = 2).\n\n	     Input File:\n	          {zero, one, two};\n	          {0, 0}; \n	          {1, 0}; \n	          {0, 1}; \n	          {1, 1}\n\n	     Output File:\n	          {zero, one, two};\n	          {1, 0, 0};\n	          {0, 1, 0};\n	          {0, 1, 0};\n	          {0, 0, 1}\n\n	     So the words (zero, one, two) are the answers, in order, from top to\n	     bottom. As you can see, the output file is the answers to the input.\n	     For the first \"question\"  0 + 0 has the answer 0, that is why the \n	     answer in the output file the first answer array has a 1 in the first \n	     position, which as seen in the answer names, is \"zero,\" and the\n	     rest of the positions are marked as 0s.\n\n	     Just one more example with the previous one, to drive home how it\n	     works. For the 3rd \"question,\" it is 0 and 1, which will equal 1. so\n	     in the 3rd \"answer,\" the only position with a 1 is the center, since\n	     the first position is zero, second is one, and third is two.\n\n	     Also, the data does not have to be added together as the answer, you\n	     can put in abstract data, such as entering in all the pixels of a\n	     picture (the picture either containing an apple or orange), and\n	     having there be two answers, orange or apple, meaning the top line\n	     of each file (input and output) will start with this:\n\n	     {Orange, Apple};\n\n	     It is also important to note that each line is seperated by a semi-\n	     colon, EXCEPT the last line, which does NOT have a semicolon at the\n	     end of the line. Also, spaces and new lines have no meaning in the\n	     file, just the digits, curly brackets, commas, semicolons, and words\n	     in the first line of each file (those too have to be seperated by\n	     commas, enclosed in curly brackets, and have a semicolon at the end\n	     of the line.");
                text.setCaretPosition(0);
                text.setEditable(false);

                helpScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                helpFrame.add(helpScroll);
                helpFrame.setVisible(true);

            }

        });

        submitButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                boolean filledOut = true;
                JFrame errorframe = new JFrame("Error!");
                errorframe.setIconImage(img.getImage());

                restOfCommand[2] = hiddenNeuronInput.getText();
                restOfCommand[3] = learningRateInput.getText();
                restOfCommand[4] = repsInput.getText();

                for (int i = 0; i < 6; i++) {

                    if (restOfCommand[i] == null || restOfCommand[i].equals("")) {
                    
                        filledOut = false;

                    }

                }

                if (filledOut) {

                    synchronized (obj) {

                        obj.notify();
    
                    }

                } else {

                    JOptionPane.showMessageDialog(errorframe, "Please Fill Out All Fields");
                    
                }

            }

        });

        returnToHome.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                hiddenNeuronInput.setText("");
                isReturn = true;

                synchronized (obj) {

                    obj.notify();

                }

            }

        });
        
        jframe.getContentPane().revalidate();
        jframe.getContentPane().repaint();
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        synchronized (obj) {

            try {

                obj.wait();

            } catch (InterruptedException e) {



            }

        }

        restOfCommand[2] = hiddenNeuronInput.getText() + " ";
        restOfCommand[3] = learningRateInput.getText() + " ";
        restOfCommand[4] = repsInput.getText();
        jframe.dispose();

    }

    public static void runNetScreen() {

        Object obj = new Object();

        jframe.getContentPane().removeAll();

        jframe.setSize(430, 450);

        returnToHome.setBounds(158, 365, 100, 30);
        jframe.add(returnToHome);

        newTitle = new JTextArea();
        newTitle.setBounds(25, 25, 375, 40);
        newTitle.setText("         Run Neural Network Page; Please Enter Info\n     Press \"help\" button for information about this page.");
        newTitle.setFont(new Font("Times New Roman", Font.BOLD, 15));
        newTitle.setEditable(false);
        newTitle.setOpaque(false);
        jframe.add(newTitle);

        inputData = new JButton("Neural Net");
        inputData.setBounds(25, 90, 105, 40);
        jframe.add(inputData);

        inputFile = new JTextArea();
        inputFile.setEditable(false);
        inputFileScroll = new JScrollPane(inputFile);
        inputFileScroll.setBounds(155, 93, 235, 34);
        inputFileScroll.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        jframe.add(inputFileScroll);

        inputData.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                JFileChooser inputData = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                int returnValue = inputData.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {

                    File selectedFile = inputData.getSelectedFile();

                    inputFile.setText(selectedFile.getAbsolutePath());
                    jframe.getContentPane().revalidate();
                    jframe.getContentPane().repaint();

                }

            }

        });

        helpButton = new JButton("Help!");
        helpButton.setBounds(332, 365, 60, 30);
        helpButton.setFont(new Font("Times New Roman", Font.PLAIN, 9));
        jframe.add(helpButton);

        submitButton = new JButton("Submit");
        submitButton.setBounds(25, 365, 60, 30);
        submitButton.setFont(new Font("Times New Roman", Font.PLAIN, 9));
        jframe.add(submitButton);

        // 417, 415

        helpButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                JFrame helpFrame = new JFrame("Help!");
                helpFrame.setIconImage(img.getImage());
                JTextArea text = new JTextArea();
                JScrollPane helpScroll = new JScrollPane(text);

                helpFrame.setSize(500, 600);
                helpFrame.setResizable(false);
                helpFrame.setLocationRelativeTo(null);

                text.setBounds(25, 25, 100, 100);
                text.setText("	-How Should I Set Up My Input and Output Files?\n\n	     First of all, what is \"input\" and \"output?\"\n	          Well... this is the training data, the data that shows the network\n	          how to learn. For the nth position of the input data, the nth position\n	          of the output file is the answer to the \"question\" of the input data.\n\n	          The following example is of two 1 digit binary numbers (a zero or\n	          one) as the inputs, and their sum as the output (0 + 0 = 0, 1 + 0\n	          = 1, 0 + 1 = 1, 1 + 1 = 2).\n\n	     Input File:\n	          {zero, one, two};\n	          {0, 0}; \n	          {1, 0}; \n	          {0, 1}; \n	          {1, 1}\n\n	     Output File:\n	          {zero, one, two};\n	          {1, 0, 0};\n	          {0, 1, 0};\n	          {0, 1, 0};\n	          {0, 0, 1}\n\n	     So the words (zero, one, two) are the answers, in order, from top to\n	     bottom. As you can see, the output file is the answers to the input.\n	     For the first \"question\"  0 + 0 has the answer 0, that is why the \n	     answer in the output file the first answer array has a 1 in the first \n	     position, which as seen in the answer names, is \"zero,\" and the\n	     rest of the positions are marked as 0s.\n\n	     Just one more example with the previous one, to drive home how it\n	     works. For the 3rd \"question,\" it is 0 and 1, which will equal 1. so\n	     in the 3rd \"answer,\" the only position with a 1 is the center, since\n	     the first position is zero, second is one, and third is two.\n\n	     Also, the data does not have to be added together as the answer, you\n	     can put in abstract data, such as entering in all the pixels of a\n	     picture (the picture either containing an apple or orange), and\n	     having there be two answers, orange or apple, meaning the top line\n	     of each file (input and output) will start with this:\n\n	     {Orange, Apple};\n\n	     It is also important to note that each line is seperated by a semi-\n	     colon, EXCEPT the last line, which does NOT have a semicolon at the\n	     end of the line. Also, spaces and new lines have no meaning in the\n	     file, just the digits, curly brackets, commas, semicolons, and words\n	     in the first line of each file (those too have to be seperated by\n	     commas, enclosed in curly brackets, and have a semicolon at the end\n	     of the line.");
                text.setCaretPosition(0);
                text.setEditable(false);

                helpScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                helpFrame.add(helpScroll);
                helpFrame.setVisible(true);

            }

        });

        submitButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                boolean filledOut = true;
                JFrame errorframe = new JFrame("Error!");
                errorframe.setIconImage(img.getImage());

                restOfCommand[0] = inputFile.getText();

                if (restOfCommand[0].equals("")) {

                    filledOut = false;

                }

                if (filledOut) {

                    synchronized (obj) {

                        obj.notify();
    
                    }

                } else {

                    JOptionPane.showMessageDialog(errorframe, "Please Fill Out All Fields");
                    
                }

            }

        });

        returnToHome.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                inputFile.setText("");
                isReturn = true;

                synchronized (obj) {

                    obj.notify();

                }

            }

        });

        jframe.getContentPane().revalidate();
        jframe.getContentPane().repaint();
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        synchronized (obj) {

            try {

                obj.wait();

            } catch (InterruptedException e) {



            }

        }

        restOfCommand[0] = inputFile.getText();

        jframe.dispose();

    }

    public static void saveScreen() {

        JFrame save = new JFrame("Train A Brain");
        save.setIconImage(img.getImage());
        JTextArea title = new JTextArea();
        JButton yes = new JButton("Yes");
        JButton no = new JButton ("No");
        Object obj = new Object();
        saveNetwork = true;

        save.setSize(380, 130);
        save.setResizable(false);
        save.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        save.setLocationRelativeTo(null);

        title.setEditable(false);
        title.setBounds(15, 15, 350, 25);
        title.setText(" Would You Like To Save Your Neural Network?");
        title.setFont(new Font("Times New Roman", Font.BOLD, 16));
        title.setOpaque(false);
        save.add(title);

        yes.setBounds(26, 55, 150, 30);
        save.add(yes);

        no.setBounds(190, 55, 150, 30);
        save.add(no);

        yes.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                synchronized (obj) {

                    obj.notify();

                }

            }

        });

        no.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                saveNetwork = false;

                synchronized (obj) {

                    obj.notify();

                }

            }

        });

        save.setLayout(null);
        save.setVisible(true);

        synchronized (obj) {

            try {

                obj.wait();

            } catch (InterruptedException e) {



            }

        }

        save.dispose();

    }

    public static String getSaveFilePath() {

        JFrame savePath = new JFrame("Train A Brain");
        savePath.setIconImage(img.getImage());
        JButton button = new JButton();
        JButton submit = new JButton("Submit!");
        JTextArea textArea = new JTextArea();
        JScrollPane scroll = new JScrollPane(textArea);
        Object obj = new Object();

        savePath.setSize(200, 160);
        savePath.setResizable(false);
        savePath.setLocationRelativeTo(null);

        button.setBounds(15, 15, 160, 25);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        savePath.add(button);

        textArea.setEditable(false);
        textArea.setText("Click Here To Choose Save File");
        scroll.setBounds(15, 15, 160, 40);
        scroll.setFont(new Font("Times New Roman", Font.BOLD, 16));
        scroll.setOpaque(false);
        savePath.add(scroll);

        submit.setBounds(15, 70, 160, 40);
        savePath.add(submit);

        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                JFileChooser file = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                int returnValue = file.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {

                    File selectedFile = file.getSelectedFile();

                    textArea.setText(selectedFile.getAbsolutePath());

                }

            }

        });

        submit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                synchronized (obj) {

                    obj.notify();

                }

            }

        });

        savePath.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        savePath.setLayout(null);
        savePath.setVisible(true);

        synchronized (obj) {

            try {

                obj.wait();

            } catch (InterruptedException e) {



            }

        }

        savePath.dispose();

        return textArea.getText();

    }
    
    public static boolean fileNotEmptyOverwrite() {

        JFrame save = new JFrame("Train A Brain");
        save.setIconImage(img.getImage());
        JTextArea title = new JTextArea();
        JButton yes = new JButton("Yes");
        JButton no = new JButton ("No");
        Object obj = new Object();
        yesOrNo = true;

        save.setSize(380, 130);
        save.setResizable(false);
        save.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        save.setLocationRelativeTo(null);

        title.setEditable(false);
        title.setBounds(15, 15, 350, 25);
        title.setText("File Not Empty, Would You Like To Overwrite?");
        title.setFont(new Font("Times New Roman", Font.BOLD, 16));
        title.setOpaque(false);
        save.add(title);

        yes.setBounds(26, 55, 150, 30);
        save.add(yes);

        no.setBounds(190, 55, 150, 30);
        save.add(no);

        yes.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                synchronized (obj) {

                    obj.notify();

                }

            }

        });

        no.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                yesOrNo = false;

                saveNetwork = false;

                synchronized (obj) {

                    obj.notify();

                }

            }

        });

        save.setLayout(null);
        save.setVisible(true);

        synchronized (obj) {

            try {

                obj.wait();

            } catch (InterruptedException e) {



            }

        }

        save.dispose();

        return yesOrNo;

    }

    public static boolean error() {

        JFrame error = new JFrame("Train A Brain");
        error.setIconImage(img.getImage());
        JTextArea title = new JTextArea();
        JButton yes = new JButton("Yes");
        JButton no = new JButton ("No");
        Object obj = new Object();
        yesOrNo = true;

        error.setSize(380, 130);
        error.setResizable(false);
        error.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        error.setLocationRelativeTo(null);

        title.setEditable(false);
        title.setBounds(15, 15, 350, 25);
        title.setText("Error Saving File, Would You Like To Try Again?");
        title.setFont(new Font("Times New Roman", Font.BOLD, 16));
        title.setOpaque(false);
        error.add(title);

        yes.setBounds(26, 55, 150, 30);
        error.add(yes);

        no.setBounds(190, 55, 150, 30);
        error.add(no);

        yes.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                synchronized (obj) {

                    obj.notify();

                }

            }

        });

        no.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                yesOrNo = false;

                synchronized (obj) {

                    obj.notify();

                }

            }

        });

        error.setLayout(null);
        error.setVisible(true);

        synchronized (obj) {

            try {

                obj.wait();

            } catch (InterruptedException e) {



            }

        }

        error.dispose();

        return yesOrNo;

    }

    public static void saved() {

        JFrame saved = new JFrame("Train A Brain");
        saved.setIconImage(img.getImage());
        JTextArea title = new JTextArea();
        JButton exit = new JButton ("Exit");
        Object obj = new Object();

        saved.setSize(380, 130);
        saved.setResizable(false);
        saved.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        saved.setLocationRelativeTo(null);

        title.setEditable(false);
        title.setBounds(15, 15, 350, 25);
        title.setText("                       Neural Network Saved!");
        title.setFont(new Font("Times New Roman", Font.BOLD, 16));
        title.setOpaque(false);
        saved.add(title);

        exit.setBounds(140, 55, 100, 30);
        saved.add(exit);

        exit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                synchronized (obj) {

                    obj.notify();

                }

            }

        });

        saved.setLayout(null);
        saved.setVisible(true);

        synchronized (obj) {

            try {

                obj.wait();

            } catch (InterruptedException e) {



            }

        }

        saved.dispose();
        System.exit(0);

    }
    
    public static String[] runNetDataInputPage() {

        Object obj = new Object();
        JFrame jframe1 = new JFrame("Train A Brain");
        jframe1.setIconImage(img.getImage());
        JTextArea[] inputs = new JTextArea[inputNeurons.length];
        String[] inputStrings = new String[inputs.length];
        JToggleButton toggle = new JToggleButton("Press To Input As File");
        JTextArea inputFileForRun = new JTextArea();
        JButton inputFileForRunButton = new JButton();
        boolean submit = true;
        filepath = "";
        String file = "";
        inputTxt = false;
        done = false;

        for (int i = 0; i < inputs.length; i++) {

            inputs[i] = new JTextArea();
            inputs[i].setBounds(25, 70 + (55 * i), 100, 30);
            inputs[i].setText("Input #" + i);
            inputs[i].setFont(new Font("Times New Roman", Font.PLAIN, 12));

        }

        inputFileForRun.setBounds(25, 70, 125, 30);
        inputFileForRun.setText("Click To Select Filepath");
        inputFileForRun.setEditable(false);
        jframe1.add(inputFileForRun);

        inputFileForRunButton.setBounds(25, 70, 125, 30);
        inputFileForRunButton.setContentAreaFilled(false);
        inputFileForRunButton.setBorderPainted(false);
        jframe1.add(inputFileForRunButton);

        jframe1.setSize(430, (((inputs.length) * 55) + 109 + 55));
        jframe1.setResizable(false);
        jframe1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe1.setLayout(null);

        newTitle = new JTextArea();
        newTitle.setBounds(25, 25, 375, 40);
        newTitle.setText("                Input Data To Run Neural Network");
        newTitle.setFont(new Font("Times New Roman", Font.BOLD, 15));
        newTitle.setEditable(false);
        newTitle.setOpaque(false);
        jframe1.add(newTitle);

        toggle.setBounds(175, 70, 185, 50);
        toggle.setFont(new Font ("Times New Roman", Font.PLAIN, 12));
        jframe1.add(toggle);

        helpButton = new JButton("Help!");
        helpButton.setBounds(270, 70 + 55, 90, 30);
        helpButton.setFont(new Font("Times New Roman", Font.PLAIN, 9));
        jframe1.add(helpButton);

        submitButton = new JButton("Submit");
        submitButton.setBounds(175, 70 + 55, 90, 30);
        submitButton.setFont(new Font("Times New Roman", Font.PLAIN, 9));
        jframe1.add(submitButton);

        toggle.addActionListener(new ActionListener() { 
  
            public void actionPerformed(ActionEvent e) { 
      
                inputTxt = !inputTxt;

                if (!inputTxt) {

                    toggle.setText("Press To Input As File");

                } else {

                    toggle.setText("Press To Input As Text Boxes");

                }

                jframe1.getContentPane().revalidate();
                jframe1.getContentPane().repaint();

                synchronized (obj) {

                    obj.notify();

                }

            } 

        });

        inputFileForRunButton.addActionListener(new ActionListener() { 
  
            public void actionPerformed(ActionEvent e) { 
      
                JFileChooser inputData = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                int returnValue = inputData.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {

                    File selectedFile = inputData.getSelectedFile();

                    inputFileForRun.setText(selectedFile.getAbsolutePath());
                    filepath = selectedFile.getAbsolutePath();
                    jframe1.getContentPane().revalidate();
                    jframe1.getContentPane().repaint();

                }

            } 

        });

        helpButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                JFrame helpFrame = new JFrame("Help!");
                helpFrame.setIconImage(img.getImage());
                JTextArea text = new JTextArea();
                JScrollPane helpScroll = new JScrollPane(text);

                helpFrame.setSize(500, 600);
                helpFrame.setResizable(false);
                helpFrame.setLocationRelativeTo(null);

                text.setBounds(25, 25, 100, 100);
                text.setText("	-How Should I Set Up My Input and Output Files?\n\n	     First of all, what is \"input\" and \"output?\"\n	          Well... this is the training data, the data that shows the network\n	          how to learn. For the nth position of the input data, the nth position\n	          of the output file is the answer to the \"question\" of the input data.\n\n	          The following example is of two 1 digit binary numbers (a zero or\n	          one) as the inputs, and their sum as the output (0 + 0 = 0, 1 + 0\n	          = 1, 0 + 1 = 1, 1 + 1 = 2).\n\n	     Input File:\n	          {zero, one, two};\n	          {0, 0}; \n	          {1, 0}; \n	          {0, 1}; \n	          {1, 1}\n\n	     Output File:\n	          {zero, one, two};\n	          {1, 0, 0};\n	          {0, 1, 0};\n	          {0, 1, 0};\n	          {0, 0, 1}\n\n	     So the words (zero, one, two) are the answers, in order, from top to\n	     bottom. As you can see, the output file is the answers to the input.\n	     For the first \"question\"  0 + 0 has the answer 0, that is why the \n	     answer in the output file the first answer array has a 1 in the first \n	     position, which as seen in the answer names, is \"zero,\" and the\n	     rest of the positions are marked as 0s.\n\n	     Just one more example with the previous one, to drive home how it\n	     works. For the 3rd \"question,\" it is 0 and 1, which will equal 1. so\n	     in the 3rd \"answer,\" the only position with a 1 is the center, since\n	     the first position is zero, second is one, and third is two.\n\n	     Also, the data does not have to be added together as the answer, you\n	     can put in abstract data, such as entering in all the pixels of a\n	     picture (the picture either containing an apple or orange), and\n	     having there be two answers, orange or apple, meaning the top line\n	     of each file (input and output) will start with this:\n\n	     {Orange, Apple};\n\n	     It is also important to note that each line is seperated by a semi-\n	     colon, EXCEPT the last line, which does NOT have a semicolon at the\n	     end of the line. Also, spaces and new lines have no meaning in the\n	     file, just the digits, curly brackets, commas, semicolons, and words\n	     in the first line of each file (those too have to be seperated by\n	     commas, enclosed in curly brackets, and have a semicolon at the end\n	     of the line.");
                text.setCaretPosition(0);
                text.setEditable(false);

                helpScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                helpFrame.add(helpScroll);
                helpFrame.setVisible(true);

            }

        });

        submitButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                
                done = true;
                
                synchronized (obj) {

                    obj.notify();

                }

            }

        });

        jframe1.setVisible(true);
        jframe1.setLocationRelativeTo(null);

        inputTxt = !inputTxt;

        if (!inputTxt) {

            toggle.setText("Press To Input As File");

        } else {

            toggle.setText("Press To Input As Text Boxes");

        }

        inputTxt = !inputTxt;

        while (submit) {

            synchronized (obj) {

                try {
    
                    obj.wait();
    
                } catch (InterruptedException e) {
    
    
    
                }
    
            }

            if (done) {

                submit = false;

            }

            if (!done) {

                if (inputTxt) {

                    jframe1.remove(inputFileForRun);
                    jframe1.remove(inputFileForRunButton);
    
                    for (int i = 0; i < inputs.length; i++) {
    
                        jframe1.add(inputs[i]);
            
                    }
    
                    jframe1.getContentPane().revalidate();
                    jframe1.getContentPane().repaint();
    
                } else {
    
                    for (int i = 0; i < inputs.length; i++) {
    
                        jframe1.remove(inputs[i]);
            
                    }
    
                    jframe1.add(inputFileForRunButton);
                    jframe1.add(inputFileForRun);
    
                    jframe1.getContentPane().revalidate();
                    jframe1.getContentPane().repaint();
    
                }

            }

        }

        jframe1.dispose();

        if (inputTxt) {

            for (int i = 0; i < inputs.length; i++) {

                inputStrings[i] = inputs[i].getText();
                inputStrings[i] = inputStrings[i].replaceAll(" ","");
                inputStrings[i] = inputStrings[i].replaceAll(",","");
                inputStrings[i] = inputStrings[i].replaceAll("\n","");
    
            }

        } else {

            try {

                file = Scan.scan(filepath);
                
            } catch (IOException e1) {
                
    
    
            }

            file = file.replaceAll("\\{","");
            file = file.replaceAll("\\}","");
            file = file.replaceAll(" ","");
            
            inputStrings = file.split(",");

        }

        return inputStrings;

    }

    public static void showOutputs(ArrayList<Integer> arr) {

        JFrame jframe1 = new JFrame("Train A Brain");
        jframe1.setIconImage(img.getImage());
        JTextArea[] outputs = new JTextArea[arr.size()];
        JToggleButton toggle = new JToggleButton("Press To See Percentages");
        JButton exitButton = new JButton("Exit");
        JButton helpButton = new JButton("Help!");
        JButton copy = new JButton("Copy Percentages");
        JTextArea percentagesText = new JTextArea();
        JScrollPane percentagesTextScroll = new JScrollPane(percentagesText);
        Object obj = new Object();
        boolean submit = true;
        String s = "";
        done = false;
        inputTxt = false;

        jframe1.setSize(430, 110 + (arr.size() * 55) + ((arr.size() == 1) ? 55 : 0) + 109);
        jframe1.setResizable(false);
        jframe1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe1.setLayout(null);
        jframe1.setLocationRelativeTo(null);

        newTitle = new JTextArea();
        newTitle.setBounds(75, 25, 275, 40);
        newTitle.setText("                   Get Data Output(s)");
        newTitle.setFont(new Font("Times New Roman", Font.BOLD, 15));
        newTitle.setEditable(false);
        newTitle.setOpaque(false);
        jframe1.add(newTitle);

        for (int i = 0; i < outputs.length; i++) {

            outputs[i] = new JTextArea();
            outputs[i].setBounds(25, 70 + (55 * i), 190, 30);
            outputs[i].setText("Output: " + outputNames[arr.get(i)]);
            outputs[i].setFont(new Font("Times New Roman", Font.PLAIN, 20));
            jframe1.add(outputs[i]);

        }

        toggle.setBounds(235, 70, 160, 50);
        toggle.setFont(new Font ("Times New Roman", Font.PLAIN, 12));
        jframe1.add(toggle);

        percentagesText.setEditable(false);
        percentagesTextScroll.setBounds(25, 70, 190, (jframe1.getHeight() - 224));
        percentagesTextScroll.setFont(new Font("Times New Roman", Font.BOLD, 16));

        for (int i = 0; i < outputNeurons.length; i++) {

            double percent = outputNeurons[i];
            DecimalFormat decimalFormat = new DecimalFormat("#.#########");
            String percentString = decimalFormat.format(percent);

            s = s + "Neuron " + i + ", " + outputNames[i] + ", = " + percentString + "%";

            if (i != outputNeurons.length - 1) {

                s = s + "\n";

            }

        }

        percentagesText.setText(s);

        copy.setBounds(235, percentagesTextScroll.getY() + percentagesTextScroll.getHeight() - 50, 160, 50);
        copy.setFont(new Font ("Times New Roman", Font.PLAIN, 12));

        helpButton.setBounds(332, 190 + (55 * highestNeurons.size()), 60, 30);
        helpButton.setFont(new Font("Times New Roman", Font.PLAIN, 9));
        jframe1.add(helpButton);

        exitButton.setBounds(25, 190 + (55 * highestNeurons.size()), 60, 30);
        exitButton.setFont(new Font("Times New Roman", Font.PLAIN, 9));
        jframe1.add(exitButton);

        toggle.addActionListener(new ActionListener() { 
  
            public void actionPerformed(ActionEvent e) { 
      
                inputTxt = !inputTxt;

                if (!inputTxt) {

                    toggle.setText("Press To See Percentages");

                } else {

                    toggle.setText("Press To See Outputs");

                }

                jframe1.getContentPane().revalidate();
                jframe1.getContentPane().repaint();

                synchronized (obj) {

                    obj.notify();

                }

            } 

        });

        copy.addActionListener(new ActionListener() { 
  
            public void actionPerformed(ActionEvent e) { 
      
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Clipboard clipboard = toolkit.getSystemClipboard();
                StringSelection stringselection = new StringSelection(percentagesText.getText());
                clipboard.setContents(stringselection, null);

            } 

        });

        helpButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                JFrame helpFrame = new JFrame("Help!");
                helpFrame.setIconImage(img.getImage());
                JTextArea text = new JTextArea();
                JScrollPane helpScroll = new JScrollPane(text);

                helpFrame.setSize(500, 600);
                helpFrame.setResizable(false);
                helpFrame.setLocationRelativeTo(null);

                text.setBounds(25, 25, 100, 100);
                text.setText("	-How Should I Set Up My Input and Output Files?\n\n	     First of all, what is \"input\" and \"output?\"\n	          Well... this is the training data, the data that shows the network\n	          how to learn. For the nth position of the input data, the nth position\n	          of the output file is the answer to the \"question\" of the input data.\n\n	          The following example is of two 1 digit binary numbers (a zero or\n	          one) as the inputs, and their sum as the output (0 + 0 = 0, 1 + 0\n	          = 1, 0 + 1 = 1, 1 + 1 = 2).\n\n	     Input File:\n	          {zero, one, two};\n	          {0, 0}; \n	          {1, 0}; \n	          {0, 1}; \n	          {1, 1}\n\n	     Output File:\n	          {zero, one, two};\n	          {1, 0, 0};\n	          {0, 1, 0};\n	          {0, 1, 0};\n	          {0, 0, 1}\n\n	     So the words (zero, one, two) are the answers, in order, from top to\n	     bottom. As you can see, the output file is the answers to the input.\n	     For the first \"question\"  0 + 0 has the answer 0, that is why the \n	     answer in the output file the first answer array has a 1 in the first \n	     position, which as seen in the answer names, is \"zero,\" and the\n	     rest of the positions are marked as 0s.\n\n	     Just one more example with the previous one, to drive home how it\n	     works. For the 3rd \"question,\" it is 0 and 1, which will equal 1. so\n	     in the 3rd \"answer,\" the only position with a 1 is the center, since\n	     the first position is zero, second is one, and third is two.\n\n	     Also, the data does not have to be added together as the answer, you\n	     can put in abstract data, such as entering in all the pixels of a\n	     picture (the picture either containing an apple or orange), and\n	     having there be two answers, orange or apple, meaning the top line\n	     of each file (input and output) will start with this:\n\n	     {Orange, Apple};\n\n	     It is also important to note that each line is seperated by a semi-\n	     colon, EXCEPT the last line, which does NOT have a semicolon at the\n	     end of the line. Also, spaces and new lines have no meaning in the\n	     file, just the digits, curly brackets, commas, semicolons, and words\n	     in the first line of each file (those too have to be seperated by\n	     commas, enclosed in curly brackets, and have a semicolon at the end\n	     of the line.");
                text.setCaretPosition(0);
                text.setEditable(false);

                helpScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                helpFrame.add(helpScroll);
                helpFrame.setVisible(true);

            }

        });
    
       exitButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                done = true;

                synchronized (obj) {

                    obj.notify();

                }

            }

        });

        jframe1.setVisible(true);

        while (submit) {

            synchronized (obj) {

                try {
    
                    obj.wait();
    
                } catch (InterruptedException e) {
    
    
    
                }
    
            }

            if (done) {

                submit = false;

            }

            if (!done) {

                if (inputTxt) {

                    jframe1.add(percentagesTextScroll);
                    jframe1.add(copy);
    
                    for (int i = 0; i < outputs.length; i++) {
    
                        jframe1.remove(outputs[i]);
            
                    }
    
                    jframe1.getContentPane().revalidate();
                    jframe1.getContentPane().repaint();
    
                } else {
    
                    for (int i = 0; i < outputs.length; i++) {
    
                        jframe1.add(outputs[i]);
            
                    }
    
                    jframe1.remove(percentagesTextScroll);
                    jframe1.remove(copy);
    
                    jframe1.getContentPane().revalidate();
                    jframe1.getContentPane().repaint();
    
                }

            }

        }

        jframe1.dispose();

    }

    public static boolean doYouWantAnotherInput() {

        bool = true;

        JFrame save = new JFrame("Train A Brain");
        save.setIconImage(img.getImage());
        JTextArea title = new JTextArea();
        JButton yes = new JButton("Yes");
        JButton no = new JButton ("No");
        Object obj = new Object();
        saveNetwork = true;

        save.setSize(380, 130);
        save.setResizable(false);
        save.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        save.setLocationRelativeTo(null);

        title.setEditable(false);
        title.setBounds(15, 15, 350, 25);
        title.setText("Would You Like To Run Another Set Of Inputs?");
        title.setFont(new Font("Times New Roman", Font.BOLD, 16));
        title.setOpaque(false);
        save.add(title);

        yes.setBounds(26, 55, 150, 30);
        save.add(yes);

        no.setBounds(190, 55, 150, 30);
        save.add(no);

        yes.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                synchronized (obj) {

                    obj.notify();

                }

            }

        });

        no.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                bool = false;

                synchronized (obj) {

                    obj.notify();

                }

            }

        });

        save.setLayout(null);
        save.setVisible(true);

        synchronized (obj) {

            try {

                obj.wait();

            } catch (InterruptedException e) {



            }

        }

        save.dispose();

        return bool;

    }
    
}