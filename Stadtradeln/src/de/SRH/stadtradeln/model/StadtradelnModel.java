package de.SRH.stadtradeln.model;

import java.util.*;

/**
 * Verwaltet die Gruppen, Fahrer und gefahrenen Kilometer.
 */
public class StadtradelnModel {
    private Map<String, List<String>> gruppenMitglieder;
    private Map<String, Integer> gruppenKilometer;
    private Map<String, String[]> gruppenVerantwortliche;
    private final DateiManager dateiManager;

    public StadtradelnModel(DateiManager dateiManager) {
        this.dateiManager = dateiManager;
        this.gruppenMitglieder = new HashMap<>();
        this.gruppenKilometer = new HashMap<>();
        this.gruppenVerantwortliche = new HashMap<>();
        ladeDaten();
    }

    // Lädt die gespeicherten Daten aus der Datei
    @SuppressWarnings("unchecked")
    private void ladeDaten() {
        Map<String, Object> daten = dateiManager.ladeDaten();
        if (daten != null) {
            if (daten.get("gruppenMitglieder") instanceof Map) {
                this.gruppenMitglieder = (Map<String, List<String>>) daten.get("gruppenMitglieder");
            } else {
                System.err.println("Fehler: Unerwarteter Datentyp für Gruppenmitglieder.");
            }

            if (daten.get("gruppenKilometer") instanceof Map) {
                this.gruppenKilometer = (Map<String, Integer>) daten.get("gruppenKilometer");
            } else {
                System.err.println("Fehler: Unerwarteter Datentyp für GruppenKilometer.");
            }

            if (daten.get("gruppenVerantwortliche") instanceof Map) {
                this.gruppenVerantwortliche = (Map<String, String[]>) daten.get("gruppenVerantwortliche");
            } else {
                System.err.println("Fehler: Unerwarteter Datentyp für GruppenVerantwortliche.");
            }
        }
    }

    // Speichert die aktuellen Daten in der Datei
    public void speichereDaten() {
        Map<String, Object> daten = new HashMap<>();
        daten.put("gruppenMitglieder", gruppenMitglieder);
        daten.put("gruppenKilometer", gruppenKilometer);
        daten.put("gruppenVerantwortliche", gruppenVerantwortliche);
        dateiManager.speichereDaten(daten);
    }

    // Fügt eine neue Gruppe hinzu
    public void addGruppe(String gruppenName, String verantwortlicherName, String email) {
        if (gruppenName == null || gruppenName.isEmpty()) {
            throw new IllegalArgumentException("Gruppenname darf nicht leer sein.");
        }
        if (gruppenVerantwortliche.containsKey(gruppenName)) {
            throw new IllegalArgumentException("Gruppe existiert bereits.");
        }
        gruppenVerantwortliche.put(gruppenName, new String[]{verantwortlicherName, email});
        gruppenMitglieder.put(gruppenName, new ArrayList<>());
        gruppenKilometer.put(gruppenName, 0);
    }

    // Fügt einen neuen Fahrer zur Gruppe hinzu
    public void addFahrer(String gruppe, String nickname) {
        if (!gruppenMitglieder.containsKey(gruppe)) {
            throw new IllegalArgumentException("Gruppe existiert nicht.");
        }
        if (gruppenMitglieder.get(gruppe).contains(nickname)) {
            throw new IllegalArgumentException("Fahrer existiert bereits in dieser Gruppe.");
        }
        gruppenMitglieder.get(gruppe).add(nickname);
    }

    // Fügt eine neue Fahrt hinzu
    public void addFahrt(String nickname, int kilometer) {
        if (kilometer < 0) {
            throw new IllegalArgumentException("Kilometeranzahl darf nicht negativ sein.");
        }
        for (String gruppe : gruppenMitglieder.keySet()) {
            if (gruppenMitglieder.get(gruppe).contains(nickname)) {
                gruppenKilometer.put(gruppe, gruppenKilometer.getOrDefault(gruppe, 0) + kilometer);
                return;
            }
        }
        throw new IllegalArgumentException("Fahrer nicht gefunden.");
    }

    // Berechnet die Gesamtkilometer mit Streams
    public int getGesamtKilometer() {
        return gruppenKilometer.values().stream().mapToInt(Integer::intValue).sum();
    }

    // Gibt alle Gruppen mit deren Kilometerzahl zurück (sortiert absteigend)
    public Map<String, Integer> getGruppenKilometer() {
        return new TreeMap<>(gruppenKilometer);
    }

    // Gibt die Gruppenmitglieder zurück
    public Map<String, List<String>> getGruppenMitglieder() {
        return gruppenMitglieder;
    }
}
