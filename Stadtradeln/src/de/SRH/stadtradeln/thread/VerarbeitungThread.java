package de.SRH.stadtradeln.thread;

import de.SRH.stadtradeln.model.StadtradelnModel;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VerarbeitungThread extends Thread {
    private static final Logger logger = Logger.getLogger(VerarbeitungThread.class.getName());
    private final StadtradelnModel model;
    private final String neueFahrtenPfad = "C:\\Users\\startklar\\IdeaProjects\\Stadtradeln\\src\\de\\SRH\\stadtradeln\\neuefahrten.csv";

    public VerarbeitungThread(StadtradelnModel model) {
        this.model = model;
    }

    @Override
    public void run() {
        logger.info("VerarbeitungThread gestartet."); // Log-Nachricht: Thread gestartet
        // Solange der Thread nicht unterbrochen wird, läuft die Schleife weiter
        while (!Thread.currentThread().isInterrupted()) {
            try {
                verarbeiteNeueFahrten(); // Verarbeitet neue Fahrten aus der Datei
                Thread.sleep(60000); // Thread pausiert für 60 Sekunden
            } catch (InterruptedException e) {
                // Wenn der Thread unterbrochen wird, wird die Unterbrechung als Warnung geloggt und der Interrupt-Status wird wieder gesetzt
                logger.log(Level.WARNING, "Thread wurde unterbrochen.", e);
                Thread.currentThread().interrupt(); // Status wiederherstellen
                break; // Schleife wird beendet
            } catch (Exception e) {
                // Bei unerwarteten Fehlern wird der Fehler mit hoher Priorität geloggt
                logger.log(Level.SEVERE, "Unerwarteter Fehler im VerarbeitungThread.", e);
            }
        }
        logger.info("VerarbeitungThread wurde sauber beendet."); // Log-Nachricht: Der Thread wurde sauber beendet
    }

    private void verarbeiteNeueFahrten() {
        File file = new File(neueFahrtenPfad);
        if (!file.exists()) {  // Prüft, ob die Datei existiert. Andernfalls wird ein Warn-Log ausgegeben und die Methode beendet
            logger.warning("Datei neuefahrten.csv existiert nicht.");
            return;
        }

        try {
            List<String[]> neueFahrten = model.ladeFahrten(); // Lädt die Fahrten aus der Datei in eine Liste von String-Arrays
            neueFahrten.forEach(fahrt -> { // Iteriert durch jede Fahrt in der Liste
                try {
                    String nickname = fahrt[0]; // Extrahiert Nickname aus der Zeile
                    int kilometer = Integer.parseInt(fahrt[1]); // Extrahiert Kilometer aus der Zeile
                    model.addFahrt(nickname, kilometer); // Fügt die Fahrt dem Modell hinzu
                    logger.info("Fahrt hinzugefügt: " + nickname + ", " + kilometer + " km"); // Loggt das erfolgreiche Hinzufügen der Fahrt
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    logger.log(Level.WARNING, "Fehlerhafte Daten in der Datei: " + String.join(",", fahrt), e); // Loggt einen Warnhinweis bei fehlerhaften Daten
                }
            });
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Fehler beim Verarbeiten der Datei neuefahrten.csv.", e); // Loggt schwerwiegende Fehler, die beim Laden oder Verarbeiten der Datei auftreten
        }

        if (file.delete()) {
            logger.info("Datei neuefahrten.csv wurde erfolgreich gelöscht."); // Erfolgreiches Löschen wird geloggt
        } else {
            logger.warning("Datei neuefahrten.csv konnte nicht gelöscht werden."); // Falls die Datei nicht gelöscht werden konnte, wird ein Warnhinweis geloggt
        }
    }
}
