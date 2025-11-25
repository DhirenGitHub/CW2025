package com.comp2042;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class NotificationPanel extends StackPane {

    public NotificationPanel(String text) {
        // Set size to match game board area
        setPrefSize(230, 100);
        setAlignment(Pos.CENTER);

        final Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");
        score.setEffect(new Glow(0.8));
        score.setTextFill(Color.WHITE);

        getChildren().add(score);
    }

    public void showScore(ObservableList<Node> list) {
        FadeTransition ft = new FadeTransition(Duration.millis(1500), this);
        TranslateTransition tt = new TranslateTransition(Duration.millis(1500), this);
        tt.setFromY(0);
        tt.setToY(-50);
        ft.setFromValue(1);
        ft.setToValue(0);

        ParallelTransition transition = new ParallelTransition(tt, ft);
        transition.setOnFinished(event -> list.remove(NotificationPanel.this));
        transition.play();
    }
}
