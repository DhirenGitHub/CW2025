package com.comp2042;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Start menu panel displayed when the game launches.
 */
public class StartMenuPanel extends BorderPane {

    private final Button playButton;

    public StartMenuPanel() {
        // Set size to match window and center content
        this.setPrefSize(350, 510);

        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);

        final Label titleLabel = new Label("TETRIS");
        titleLabel.getStyleClass().add("startMenuStyle");

        playButton = new Button("PLAY");
        playButton.getStyleClass().add("ipad-dark-grey");
        playButton.setPrefWidth(150);

        menuBox.getChildren().addAll(titleLabel, playButton);
        setCenter(menuBox);
    }

    public Button getPlayButton() {
        return playButton;
    }
}
