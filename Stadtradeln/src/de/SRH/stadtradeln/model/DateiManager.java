package de.SRH.stadtradeln.model;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class DateiManager {
    // Lädt die gespeicherten Daten aus "stadtradeln.dat"
    public Map<String, Object> ladeDaten() {
        File file = new File("stadtradeln.dat");
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                return (Map<String, Object>) obj;
            } else {
                System.err.println("Fehler: Unerwarteter Datentyp in stadtradeln.dat");
                return new HashMap<>();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Fehler beim Laden der Daten: " + e.getMessage());
            return new HashMap<>();
        }
    }

    // Speichert die aktuellen Daten in "stadtradeln.dat"
    public void speichereDaten(Map<String, Object> daten) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("stadtradeln.dat"))) {
            oos.writeObject(daten);
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Daten: " + e.getMessage());
        }
    }

    // Speichert eine neue Fahrt in "fahrten.dat"
    public void speichereFahrt(String nickname, int kilometer, LocalDate datum) {
        File file = new File("fahrten.dat");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(nickname + ";" + kilometer + ";" + datum);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Fahrt: " + e.getMessage());
        }
    }

    // Lädt alle gespeicherten Fahrten aus "fahrten.dat"
    public List<String[]> ladeFahrten() {
        File file = new File("fahrten.dat");
        List<String[]> fahrten = new ArrayList<>();
        if (!file.exists()) {
            return fahrten;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fahrt = line.split(";");
                if (fahrt.length == 3) {
                    fahrten.add(fahrt);
                }
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Fahrten: " + e.getMessage());
        }
        return fahrten;
    }

    // Lädt neue Fahrten aus einer CSV-Datei (z. B. "neuefahrten.csv")
    public List<String[]> ladeNeueFahrten(String filePath) {
        File file = new File(filePath);
        List<String[]> neueFahrten = new ArrayList<>();

        if (!file.exists()) {
            System.err.println("Die Datei " + filePath + " existiert nicht.");
            return neueFahrten;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] daten = line.split(",");
                if (daten.length == 3) { // Validierung: 3 Spalten erwartet
                    neueFahrten.add(daten);
                } else {
                    System.err.println("Ungültige Zeile in neuefahrten.csv: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Laden neuer Fahrten: " + e.getMessage());
        }

        return neueFahrten;
    }

    // Löscht eine Datei nach der Verarbeitung
    public boolean loescheDatei(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                System.err.println("Fehler beim Löschen der Datei: " + filePath);
            }
            return deleted;
        }
        return false;
    }
}
