package com.comp2042.ui.panels;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class GameOverPanel extends BorderPane {

    private final Button newGameButton;
    private final Button homeButton;
    private final Text highScoreText;

    public GameOverPanel() {
        // Add translucent background overlay
        this.getStyleClass().add("pauseOverlay");
        this.setPrefSize(800, 600);

        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);

        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");

        highScoreText = new Text("NEW HIGHSCORE");
        highScoreText.getStyleClass().add("newHighScoreStyle");
        highScoreText.setVisible(false);
        highScoreText.setManaged(false);

        newGameButton = new Button("NEW GAME");
        newGameButton.getStyleClass().add("ipad-dark-grey");
        newGameButton.setPrefWidth(150);

        homeButton = new Button("HOME");
        homeButton.getStyleClass().add("ipad-dark-grey");
        homeButton.setPrefWidth(150);

        menuBox.getChildren().addAll(gameOverLabel, highScoreText, newGameButton, homeButton);
        setCenter(menuBox);
    }

    public Button getNewGameButton() {
        return newGameButton;
    }

    public Button getHomeButton() {
        return homeButton;
    }

    public void setNewHighScore(boolean isNewHighScore) {
        System.out.println("GameOverPanel.setNewHighScore called with: " + isNewHighScore);
        System.out.println("highScoreText is null: " + (highScoreText == null));
        highScoreText.setVisible(isNewHighScore);
        highScoreText.setManaged(isNewHighScore);
        System.out.println("After setting - visible: " + highScoreText.isVisible() + ", managed: " + highScoreText.isManaged());
    }
}
