package de.SRH.stadtradeln.model;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class DateiManager {
    public Map<String, Object> ladeDaten() {
        File file = new File("stadtradeln.dat");
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<String, Object>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public void speichereDaten(Map<String, Object> daten) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("stadtradeln.dat"))) {
            oos.writeObject(daten);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void speichereFahrt(String nickname, int kilometer, LocalDate datum) {
        File file = new File("fahrten.dat");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(nickname + ";" + kilometer + ";" + datum);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
            e.printStackTrace();
        }
        return fahrten;
    }
}
