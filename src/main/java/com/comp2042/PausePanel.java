package com.comp2042;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Visual panel displayed when the game is paused.
 */
public class PausePanel extends BorderPane {

    private final Button resumeButton;
    private final Button newGameButton;
    private final Button homeButton;

    public PausePanel() {
        // Add translucent background overlay
        this.getStyleClass().add("pauseOverlay");
        this.setPrefSize(350, 510); // Match window size

        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);

        final Label pauseLabel = new Label("PAUSED");
        pauseLabel.getStyleClass().add("pauseStyle");

        resumeButton = new Button("RESUME");
        resumeButton.getStyleClass().add("ipad-dark-grey");
        resumeButton.setPrefWidth(150);

        newGameButton = new Button("NEW GAME");
        newGameButton.getStyleClass().add("ipad-dark-grey");
        newGameButton.setPrefWidth(150);

        homeButton = new Button("HOME");
        homeButton.getStyleClass().add("ipad-dark-grey");
        homeButton.setPrefWidth(150);

        menuBox.getChildren().addAll(pauseLabel, resumeButton, newGameButton, homeButton);
        setCenter(menuBox);
    }

    public Button getResumeButton() {
        return resumeButton;
    }

    public Button getNewGameButton() {
        return newGameButton;
    }

    public Button getHomeButton() {
        return homeButton;
    }
}
