package de.SRH.stadtradeln.view;

import de.SRH.stadtradeln.controller.StadtradelnController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class StadtradelnView {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextArea feedbackArea;

    private StadtradelnController controller;

    public StadtradelnView() {
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Stadtradeln");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Tabellenmodell erstellen und Editierbarkeit deaktivieren duch das Überschreiben der Methode isCellEditable
        tableModel = new DefaultTableModel(new String[]{"Gruppe", "Kilometer"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Zellen sind nicht editierbar
            }
        };

        // Tabelle erstellen
        table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);

        feedbackArea = new JTextArea(10, 50);
        feedbackArea.setEditable(false);
        JScrollPane feedbackScroll = new JScrollPane(feedbackArea);

        JButton addGroupButton = new JButton("Gruppe hinzufügen");
        JButton addDriverButton = new JButton("Fahrer hinzufügen");
        JButton addRideButton = new JButton("Fahrt hinzufügen");
        JButton exitButton = new JButton("Beenden");

        // Aktionen für Buttons hinzufügen
        addGroupButton.addActionListener(e -> controller.addGruppeDialog());
        addDriverButton.addActionListener(e -> controller.addFahrerDialog());
        addRideButton.addActionListener(e -> controller.addFahrtDialog());
        exitButton.addActionListener(e -> System.exit(0));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addGroupButton);
        buttonPanel.add(addDriverButton);
        buttonPanel.add(addRideButton);
        buttonPanel.add(exitButton);

        frame.add(tableScroll, BorderLayout.CENTER);
        frame.add(feedbackScroll, BorderLayout.SOUTH);
        frame.add(buttonPanel, BorderLayout.NORTH);

        frame.setVisible(true);
    }

    public void updateTable(Map<String, Integer> data) {
        tableModel.setRowCount(0);
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            tableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
    }

    public void addFeedbackMessage(String message) {
        feedbackArea.append(message + "\n");
    }

    public void setController(StadtradelnController controller) {
        this.controller = controller;
    }
}
