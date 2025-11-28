package com.comp2042.ui.panels;

import com.comp2042.utils.FontLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Panel that displays game controls and instructions
 */
public class ControlsPanel extends StackPane {

    private final Button backButton;

    public ControlsPanel() {
        this.setPrefSize(800, 600);
        this.setAlignment(Pos.CENTER);

        // Main layout container
        VBox mainLayout = new VBox(15);
        mainLayout.setAlignment(Pos.CENTER);

        // Create styled container with desert theme
        VBox containerBox = new VBox(15);
        containerBox.setAlignment(Pos.CENTER);
        containerBox.setPadding(new Insets(20));
        containerBox.setPrefWidth(550);
        containerBox.setMaxWidth(550);
        containerBox.getStyleClass().add("controlsContainer");

        // Create content container
        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER);

        // Load digital font
        FontLoader.loadFont();
        Font digitalFont = FontLoader.getFont(48);
        Font digitalFontMedium = FontLoader.getFont(28);
        Font digitalFontSmall = FontLoader.getFont(18);

        // Title
        Label titleLabel = new Label("CONTROLS");
        if (digitalFont != null) {
            titleLabel.setFont(digitalFont);
        }
        titleLabel.setTextFill(Color.CYAN);

        // One Player Controls
        Text onePlayerTitle = new Text("1 PLAYER MODE");
        if (digitalFontMedium != null) {
            onePlayerTitle.setFont(digitalFontMedium);
        }
        onePlayerTitle.setFill(Color.YELLOW);
        onePlayerTitle.setTextAlignment(TextAlignment.CENTER);

        Text onePlayerControls = new Text(
            "Move Left: LEFT ARROW or A\n" +
            "Move Right: RIGHT ARROW or D\n" +
            "Rotate: UP ARROW or W\n" +
            "Soft Drop: DOWN ARROW or S\n" +
            "Hard Drop: SPACE\n" +
            "Pause: ESC"
        );
        if (digitalFontSmall != null) {
            onePlayerControls.setFont(digitalFontSmall);
        }
        onePlayerControls.setFill(Color.WHITE);
        onePlayerControls.setTextAlignment(TextAlignment.CENTER);

        // Two Player Controls
        Text twoPlayerTitle = new Text("2 PLAYER MODE");
        if (digitalFontMedium != null) {
            twoPlayerTitle.setFont(digitalFontMedium);
        }
        twoPlayerTitle.setFill(Color.YELLOW);
        twoPlayerTitle.setTextAlignment(TextAlignment.CENTER);

        Text twoPlayerControls = new Text(
            "PLAYER 1:\n" +
            "Move Left: A   |   Move Right: D\n" +
            "Rotate: W   |   Soft Drop: S   |   Hard Drop: Q\n\n" +
            "PLAYER 2:\n" +
            "Move Left: LEFT   |   Move Right: RIGHT\n" +
            "Rotate: UP   |   Soft Drop: DOWN   |   Hard Drop: SPACE\n\n" +
            "Pause: ESC"
        );
        if (digitalFontSmall != null) {
            twoPlayerControls.setFont(digitalFontSmall);
        }
        twoPlayerControls.setFill(Color.WHITE);
        twoPlayerControls.setTextAlignment(TextAlignment.CENTER);

        // Back button
        backButton = new Button("BACK");
        backButton.getStyleClass().add("ipad-dark-grey");
        backButton.setPrefWidth(200);

        contentBox.getChildren().addAll(
            titleLabel,
            onePlayerTitle,
            onePlayerControls,
            twoPlayerTitle,
            twoPlayerControls
        );

        containerBox.getChildren().add(contentBox);
        mainLayout.getChildren().addAll(containerBox, backButton);
        this.getChildren().add(mainLayout);
    }

    public Button getBackButton() {
        return backButton;
    }
}
