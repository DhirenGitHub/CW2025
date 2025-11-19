package com.comp2042;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class GameOverPanel extends BorderPane {

    private final Button newGameButton;
    private final Button homeButton;

    public GameOverPanel() {
        // Add translucent background overlay
        this.getStyleClass().add("pauseOverlay");
        this.setPrefSize(350, 510);

        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);

        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");

        newGameButton = new Button("NEW GAME");
        newGameButton.getStyleClass().add("ipad-dark-grey");
        newGameButton.setPrefWidth(150);

        homeButton = new Button("HOME");
        homeButton.getStyleClass().add("ipad-dark-grey");
        homeButton.setPrefWidth(150);

        menuBox.getChildren().addAll(gameOverLabel, newGameButton, homeButton);
        setCenter(menuBox);
    }

    public Button getNewGameButton() {
        return newGameButton;
    }

    public Button getHomeButton() {
        return homeButton;
    }
}
