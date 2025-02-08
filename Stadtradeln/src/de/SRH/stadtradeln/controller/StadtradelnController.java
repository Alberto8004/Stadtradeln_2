package de.SRH.stadtradeln.controller;

import de.SRH.stadtradeln.model.StadtradelnModel;
import de.SRH.stadtradeln.view.StadtradelnView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;

public class StadtradelnController {
    private final StadtradelnModel model;
    private final StadtradelnView view;

    public StadtradelnController(StadtradelnModel model, StadtradelnView view) {
        this.model = model;
        this.view = view;
        this.view.setController(this);
        aktualisiereTabelle();
    }

    // Dialog für das Hinzufügen einer neuen Gruppe
    public void addGruppeDialog() {
        String gruppe = JOptionPane.showInputDialog("Gruppenname eingeben:");
        if (gruppe != null && !gruppe.isEmpty()) {
            String verantwortlicherName = JOptionPane.showInputDialog("Name des Verantwortlichen:");
            String email = JOptionPane.showInputDialog("E-Mail-Adresse des Verantwortlichen:");
            try {
                model.addGruppe(gruppe, verantwortlicherName, email);
                model.speichereDaten();
                aktualisiereTabelle();
                view.addFeedbackMessage("Gruppe hinzugefügt: " + gruppe);
            } catch (IllegalArgumentException e) {
                view.addFeedbackMessage("Fehler: " + e.getMessage());
            }
        }
    }

    // Dialog für das Hinzufügen eines neuen Fahrers
    public void addFahrerDialog() {
        String gruppe = (String) JOptionPane.showInputDialog(
                null, "Wählen Sie eine Gruppe:", "Fahrer hinzufügen",
                JOptionPane.QUESTION_MESSAGE, null,
                model.getGruppenMitglieder().keySet().toArray(), null);
        if (gruppe != null) {
            String nickname = JOptionPane.showInputDialog("Nickname des Fahrers:");
            try {
                model.addFahrer(gruppe, nickname);
                model.speichereDaten();
                view.addFeedbackMessage("Fahrer hinzugefügt: " + nickname + " zu Gruppe " + gruppe);
            } catch (IllegalArgumentException e) {
                view.addFeedbackMessage("Fehler: " + e.getMessage());
            }
        }
    }

    // Dialog für das Hinzufügen einer neuen Fahrt
    public void addFahrtDialog() {
        String nickname = (String) JOptionPane.showInputDialog(
                null, "Wählen Sie einen Fahrer:", "Fahrt hinzufügen",
                JOptionPane.QUESTION_MESSAGE, null,
                model.getGruppenMitglieder().values().stream()
                        .flatMap(List::stream).toArray(), null);
        if (nickname != null) {
            String kilometerStr = JOptionPane.showInputDialog("Gefahrene Kilometer:");
            try {
                int kilometer = Integer.parseInt(kilometerStr);
                if (kilometer < 0) {
                    throw new IllegalArgumentException("Kilometeranzahl darf nicht negativ sein!");
                }
                model.addFahrt(nickname, kilometer);
                model.speichereDaten();
                aktualisiereTabelle();
                view.addFeedbackMessage(kilometer + " Kilometer hinzugefügt für Fahrer: " + nickname);
            } catch (NumberFormatException e) {
                view.addFeedbackMessage("Fehler: Ungültige Kilometerangabe.");
            } catch (IllegalArgumentException e) {
                view.addFeedbackMessage("Fehler: " + e.getMessage());
            }
        }
    }

    // Aktualisiert die GUI-Tabelle mit den neuesten Daten
    public void aktualisiereTabelle() {
        DefaultTableModel tableModel = (DefaultTableModel) view.getTabelle().getModel();
        tableModel.setRowCount(0); // Tabelle leeren

        // Sortierte Liste nach Gesamt-Kilometern
        List<Map.Entry<String, Integer>> gruppenListe = new ArrayList<>(model.getGruppenKilometer().entrySet());
        gruppenListe.sort(Map.Entry.comparingByValue(Collections.reverseOrder()));

        for (Map.Entry<String, Integer> eintrag : gruppenListe) {
            tableModel.addRow(new Object[]{eintrag.getKey(), eintrag.getValue()});
        }
    }

}
