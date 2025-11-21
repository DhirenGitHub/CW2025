package com.comp2042;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class VictoryPanel extends VBox {

    private Text victoryText;
    private Button newGameButton;
    private Button homeButton;

    public VictoryPanel() {
        setAlignment(Pos.CENTER);
        setSpacing(30);
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");
        setPrefSize(800, 600);

        victoryText = new Text("PLAYER 1 WINS!");
        victoryText.getStyleClass().add("victoryStyle");
        victoryText.setFill(Color.GOLD);

        newGameButton = new Button("NEW GAME");
        newGameButton.getStyleClass().add("ipad-dark-grey");
        newGameButton.setPrefWidth(200);

        homeButton = new Button("HOME");
        homeButton.getStyleClass().add("ipad-dark-grey");
        homeButton.setPrefWidth(200);

        getChildren().addAll(victoryText, newGameButton, homeButton);
    }

    public void setWinner(int playerNumber) {
        victoryText.setText("PLAYER " + playerNumber + " WINS!");
    }

    public Button getNewGameButton() {
        return newGameButton;
    }

    public Button getHomeButton() {
        return homeButton;
    }
}
