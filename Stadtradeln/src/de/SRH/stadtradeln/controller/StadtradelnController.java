package de.SRH.stadtradeln.controller;

import de.SRH.stadtradeln.model.StadtradelnModel;
import de.SRH.stadtradeln.view.StadtradelnView;

import javax.swing.*;
import java.util.List;

public class StadtradelnController {
    private final StadtradelnModel model;
    private final StadtradelnView view;

    public StadtradelnController(StadtradelnModel model, StadtradelnView view) {
        this.model = model;
        this.view = view;
        this.view.setController(this);
        view.updateTable(model.getGruppenKilometer());
    }

    public void addGruppeDialog() {
        String gruppe = JOptionPane.showInputDialog("Gruppenname eingeben:");
        if (gruppe != null && !gruppe.isEmpty()) {
            String verantwortlicherName = JOptionPane.showInputDialog("Name des Verantwortlichen:");
            String email = JOptionPane.showInputDialog("E-Mail-Adresse des Verantwortlichen:");
            try {
                model.addGruppe(gruppe, verantwortlicherName, email);
                model.speichereDaten();
                view.updateTable(model.getGruppenKilometer());
                view.addFeedbackMessage("Gruppe hinzugefügt: " + gruppe);
            } catch (IllegalArgumentException e) {
                view.addFeedbackMessage(e.getMessage());
            }
        }
    }

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
                view.addFeedbackMessage(e.getMessage());
            }
        }
    }

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
                model.addFahrt(nickname, kilometer);
                model.speichereDaten();
                view.updateTable(model.getGruppenKilometer());
                view.addFeedbackMessage(kilometer + " Kilometer hinzugefügt für Fahrer: " + nickname);
            } catch (NumberFormatException e) {
                view.addFeedbackMessage("Ungültige Kilometerangabe.");
            } catch (IllegalArgumentException e) {
                view.addFeedbackMessage(e.getMessage());
            }
        }
    }
}
