package com.dragos;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.JSpinner;

public class Frame implements ActionListener{
    private final JFrame frame = new JFrame();
    private final JPanel panel = new JPanel();
    private final JLabel label1 = new JLabel();
    private final JLabel label2 = new JLabel();
    private final JLabel label3 = new JLabel();
    private final JLabel label4 = new JLabel();
    private final JLabel label5 = new JLabel();
    private final JButton button = new JButton();
    private final JCheckBox checkBox = new JCheckBox();
    private final JSpinner spinner = new JSpinner();
    private final JTextField textField = new JTextField();

    public Frame() {
        BorderLayout borderLayout = new BorderLayout();
        frame.getContentPane().setLayout(borderLayout);
        frame.setSize(350, 300);
        frame.setTitle("Server STOPPED.");
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);

        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(330, 290));
        frame.add(panel);
        
        label1.setText("Server status:");
        label1.setBounds(20, 20, 120, 20);
        panel.add(label1);

        label2.setText("Server listening port:");
        label2.setBounds(20, 50, 120, 20);
        panel.add(label2);

        label3.setText("stopped");
        label3.setBounds(150, 20, 100, 20);
        panel.add(label3);

        label4.setText("----------");
        label4.setBounds(150, 50, 100, 20);
        panel.add(label4);

        button.setText("START");
        button.setBounds(60, 100, 120, 40);
        button.setActionCommand("START");
        button.addActionListener(this);
        panel.add(button);

        checkBox.setText("Maintanence.");
        checkBox.setBounds(25, 150, 190, 20);
        checkBox.setEnabled(false);
        checkBox.setActionCommand("MAINTENANCE");
        checkBox.addActionListener(this);
        panel.add(checkBox);

        label5.setText("Server is listening on port : ");
        label5.setBounds(20, 200, 160, 20);
        panel.add(label5);

        spinner.setModel(new SpinnerNumberModel(8080, 8000, 10500, 10));
        spinner.setBounds(200, 200, 100, 20);
        panel.add(spinner);
        
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals("START")) {

            HttpServer.setServerSocket((Integer) spinner.getValue());
            HttpServer.setState(1);
            HttpServer.setServerIsRunning(true);
            textField.setForeground(new Color(0,0,0));
            frame.setTitle("Server is RUNNING.");
            label3.setText("running");

            label4.setText("" + spinner.getValue());
            button.setText("STOP");
            button.setActionCommand("STOP");
            checkBox.setEnabled(true);
            spinner.setEnabled(false);
            textField.setEnabled(false);

        } else if (actionEvent.getActionCommand().equals("STOP")) {
            HttpServer.setState(3);
            HttpServer.setServerIsRunning(false);
            frame.setTitle("Server STOPPED.");
            label3.setText("stopped");

            label4.setText("----------");
            button.setText("START");
            button.setActionCommand("START");
            checkBox.setSelected(false);
            checkBox.setEnabled(false);
            spinner.setEnabled(true);
            textField.setEnabled(true);

        } else if (actionEvent.getActionCommand().equals("MAINTENANCE")) {

            HttpServer.setState(2);
            frame.setTitle("Server is in MAINTENANCE.");
            label3.setText("maintenance");
            checkBox.setActionCommand("Stop maintenance");
            textField.setEnabled(true);

        } else if (actionEvent.getActionCommand().equals("Stop maintenance")) {

            HttpServer.setState(1);
            textField.setForeground(new Color(0,0,0));
            frame.setTitle("Server is RUNNING.");
            label3.setText("running");
            checkBox.setActionCommand("MAINTENANCE");
            textField.setEnabled(false);

        }
    }
}
