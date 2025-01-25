package de.SRH.stadtradeln.main;

import de.SRH.stadtradeln.controller.StadtradelnController;
import de.SRH.stadtradeln.model.StadtradelnModel;
import de.SRH.stadtradeln.view.StadtradelnView;

public class Main {
    public static void main(String[] args) {
        StadtradelnModel model = new StadtradelnModel();
        StadtradelnView view = new StadtradelnView();
        StadtradelnController controller = new StadtradelnController(model, view);

        // Daten beim Beenden speichern
        Runtime.getRuntime().addShutdownHook(new Thread(model::speichereDaten));
    }
}
