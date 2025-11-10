package com.comp2042;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * Visual panel displayed when the game is paused.
 */
public class PausePanel extends BorderPane {

    public PausePanel() {
        final Label pauseLabel = new Label("PAUSED");
        pauseLabel.getStyleClass().add("pauseStyle");
        setCenter(pauseLabel);
    }
}
