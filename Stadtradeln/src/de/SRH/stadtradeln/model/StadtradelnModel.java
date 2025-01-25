package de.SRH.stadtradeln.model;

import java.time.LocalDate;
import java.util.*;

public class StadtradelnModel {
    private Map<String, List<String>> gruppenMitglieder;
    private Map<String, Integer> gruppenKilometer;
    private Map<String, Integer> fahrerKilometer;
    private Map<String, String[]> gruppenVerantwortliche;
    private final DateiManager dateiManager;

    public StadtradelnModel() {
        dateiManager = new DateiManager();
        Map<String, Object> daten = dateiManager.ladeDaten();
        gruppenMitglieder = (Map<String, List<String>>) daten.getOrDefault("gruppenMitglieder", new HashMap<>());
        gruppenKilometer = (Map<String, Integer>) daten.getOrDefault("gruppenKilometer", new HashMap<>());
        fahrerKilometer = (Map<String, Integer>) daten.getOrDefault("fahrerKilometer", new HashMap<>());
        gruppenVerantwortliche = (Map<String, String[]>) daten.getOrDefault("gruppenVerantwortliche", new HashMap<>());
    }

    public void addGruppe(String gruppe, String verantwortlicherName, String email) {
        if (gruppenMitglieder.containsKey(gruppe)) {
            throw new IllegalArgumentException("Gruppe mit dem Namen '" + gruppe + "' existiert bereits.");
        }
        gruppenMitglieder.put(gruppe, new ArrayList<>());
        gruppenKilometer.put(gruppe, 0);
        gruppenVerantwortliche.put(gruppe, new String[]{verantwortlicherName, email});
        addFahrer(gruppe, verantwortlicherName);
    }

    public void addFahrer(String gruppe, String nickname) {
        if (fahrerKilometer.containsKey(nickname)) {
            throw new IllegalArgumentException("Fahrer mit dem Nickname '" + nickname + "' existiert bereits.");
        }
        if (gruppenMitglieder.containsKey(gruppe)) {
            gruppenMitglieder.get(gruppe).add(nickname);
            fahrerKilometer.putIfAbsent(nickname, 0);
        }
    }

    public void addFahrt(String nickname, int kilometer) {
        if (!fahrerKilometer.containsKey(nickname)) {
            throw new IllegalArgumentException("Fahrer nicht gefunden: " + nickname);
        }
        fahrerKilometer.put(nickname, fahrerKilometer.get(nickname) + kilometer);

        for (Map.Entry<String, List<String>> entry : gruppenMitglieder.entrySet()) {
            if (entry.getValue().contains(nickname)) {
                String gruppe = entry.getKey();
                gruppenKilometer.put(gruppe, gruppenKilometer.get(gruppe) + kilometer);
                break;
            }
        }

        dateiManager.speichereFahrt(nickname, kilometer, LocalDate.now());
    }

    public void speichereDaten() {
        Map<String, Object> daten = new HashMap<>();
        daten.put("gruppenMitglieder", gruppenMitglieder);
        daten.put("gruppenKilometer", gruppenKilometer);
        daten.put("fahrerKilometer", fahrerKilometer);
        daten.put("gruppenVerantwortliche", gruppenVerantwortliche);
        dateiManager.speichereDaten(daten);
    }

    public Map<String, Integer> getGruppenKilometer() {
        return gruppenKilometer;
    }

    public Map<String, List<String>> getGruppenMitglieder() {
        return gruppenMitglieder;
    }

    public List<String[]> ladeFahrten() {
        return dateiManager.ladeFahrten();
    }
}
