package de.SRH.stadtradeln.thread;

import de.SRH.stadtradeln.model.DateiManager;
import de.SRH.stadtradeln.model.StadtradelnModel;
import de.SRH.stadtradeln.view.StadtradelnView;

import java.io.*;
import java.nio.file.*;
import java.util.*;


 // Ein Thread, der jede Minute prüft, ob eine Datei "neuefahrten.csv" existiert und diese verarbeitet.
public class VerarbeitungThread extends Thread {
    private static final Path NEUE_FAHRTEN_DATEI = Paths.get("neuefahrten.csv");
    private final StadtradelnModel model;
    private final StadtradelnView view;
    private boolean running = true;

    public VerarbeitungThread(StadtradelnModel model, DateiManager dateiManager, StadtradelnView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void run() {
        while (running) {
            try {
                if (Files.exists(NEUE_FAHRTEN_DATEI)) {
                    verarbeiteNeueFahrten();
                }
                Thread.sleep(60000); // 1 Minute warten
            } catch (InterruptedException e) {
                System.err.println("Thread wurde unterbrochen.");
                running = false;
            }
        }
    }

    private void verarbeiteNeueFahrten() {
        List<String> fehlerhafteZeilen = new ArrayList<>();
        List<String> gueltigeZeilen = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(NEUE_FAHRTEN_DATEI)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] teile = line.split(",");
                if (teile.length != 2) {
                    fehlerhafteZeilen.add(line);
                    continue;
                }

                String nickname = teile[0].trim();
                int kilometer;

                try {
                    kilometer = Integer.parseInt(teile[1].trim());
                    if (kilometer < 0) {
                        throw new IllegalArgumentException("Kilometeranzahl darf nicht negativ sein.");
                    }
                    model.addFahrt(nickname, kilometer);
                    gueltigeZeilen.add(line);
                } catch (IllegalArgumentException e) {
                    fehlerhafteZeilen.add(line);
                }
            }
        } catch (IOException e) {
            view.addFeedbackMessage("Fehler beim Lesen der Datei: " + e.getMessage());
        }

        // Falls keine fehlerhaften Daten vorhanden sind, Datei löschen
        if (fehlerhafteZeilen.isEmpty()) {
            try {
                Files.delete(NEUE_FAHRTEN_DATEI);
                view.addFeedbackMessage("Datei 'neuefahrten.csv' erfolgreich verarbeitet und gelöscht.");
            } catch (IOException e) {
                view.addFeedbackMessage("Fehler beim Löschen der Datei: " + e.getMessage());
            }
        } else {
            view.addFeedbackMessage("Fehlerhafte Zeilen in 'neuefahrten.csv' gefunden. Datei bleibt erhalten.");
        }

        // GUI aktualisieren
        view.updateTable(model.getGruppenKilometer());
        model.speichereDaten();
    }

    public void stopThread() {
        running = false;
        this.interrupt();
    }
}
