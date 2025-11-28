package com.comp2042.ui.panels;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.comp2042.utils.GameConstants;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Start menu panel displayed when the game launches.
 * Features animated falling Tetris pieces in the background.
 */
public class StartMenuPanel extends StackPane {

    private final Button playButton;
    private final Button twoPlayerButton;
    private final Button controlsButton;
    private final Button quitButton;
    private final Pane backgroundPane;
    private final List<FallingPiece> fallingPieces;
    private final Random random;
    private Timeline animationTimeline;
    private MediaPlayer backgroundMusic;

    public StartMenuPanel() {
        this.setPrefSize(800, 600);
        this.random = new Random();
        this.fallingPieces = new ArrayList<>();

        // Create background with image
        backgroundPane = new Pane();
        backgroundPane.setPrefSize(800, 600);

        // Set background image
        try {
            String imagePath = getClass().getResource("/home_bg.png").toExternalForm();
            BackgroundImage bgImage = new BackgroundImage(
                new javafx.scene.image.Image(imagePath),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(800, 600, false, false, false, false)
            );
            backgroundPane.setBackground(new Background(bgImage));
        } catch (Exception e) {
            System.err.println("Failed to load home background image: " + e.getMessage());
            // Fallback to gradient if image fails to load
            Rectangle gradientRect = new Rectangle(800, 600);
            LinearGradient gradient = new LinearGradient(
                0, 0, 0, 1, true, null,
                new Stop(0, Color.rgb(10, 20, 50)),
                new Stop(1, Color.rgb(0, 0, 0))
            );
            gradientRect.setFill(gradient);
            backgroundPane.getChildren().add(gradientRect);
        }

        // Create menu content
        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.BOTTOM_CENTER);
        menuBox.setMaxSize(800, 600);
        menuBox.setTranslateY(-75); // Move menu up from bottom to leave space

        playButton = new Button("1 PLAYER");
        playButton.getStyleClass().add("ipad-dark-grey");
        playButton.setPrefWidth(200);

        twoPlayerButton = new Button("2 PLAYER");
        twoPlayerButton.getStyleClass().add("ipad-dark-grey");
        twoPlayerButton.setPrefWidth(200);

        controlsButton = new Button("CONTROLS");
        controlsButton.getStyleClass().add("ipad-dark-grey");
        controlsButton.setPrefWidth(200);

        quitButton = new Button("QUIT");
        quitButton.getStyleClass().add("ipad-dark-grey");
        quitButton.setPrefWidth(200);

        menuBox.getChildren().addAll(playButton, twoPlayerButton, controlsButton, quitButton);

        // Stack background behind menu
        this.getChildren().addAll(backgroundPane, menuBox);

        // Initialize falling pieces
        initializeFallingPieces();
        startAnimation();

        // Initialize background music
        initializeBackgroundMusic();
    }

    private void initializeBackgroundMusic() {
        try {
            String musicPath = getClass().getResource("/audio/main_menu.mp3").toExternalForm();
            Media media = new Media(musicPath);
            backgroundMusic = new MediaPlayer(media);
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE); // Loop forever
            backgroundMusic.setVolume(0.5); // 50% volume
            backgroundMusic.play();
        } catch (Exception e) {
            System.err.println("Failed to load background music: " + e.getMessage());
        }
    }

    private void initializeFallingPieces() {
        // Create initial pieces spread evenly across the screen
        int numPieces = 25;
        for (int i = 0; i < numPieces; i++) {
            FallingPiece piece = createRandomPiece();
            // Spread pieces evenly across the screen height plus some above
            piece.y = -100 + (i * (610.0 / numPieces)) + random.nextDouble() * 30;
            piece.pane.setLayoutY(piece.y);
            fallingPieces.add(piece);
            backgroundPane.getChildren().add(piece.pane);
        }
    }

    private FallingPiece createRandomPiece() {
        int type = random.nextInt(GameConstants.TETROMINOES.length);
        int[][] shape = GameConstants.TETROMINOES[type];
        Color baseColor = GameConstants.TETROMINO_COLORS[type];

        // Random depth (0 = far/small, 1 = close/large)
        double depth = random.nextDouble();

        // Scale based on depth (10-25 pixels per block) - larger pieces
        double blockSize = 10 + depth * 15;

        // Opacity based on depth (more visible overall)
        double opacity = 0.3 + (1 - depth) * 0.4;

        Pane piecePane = new Pane();

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    Rectangle block = new Rectangle(blockSize, blockSize);
                    block.setFill(baseColor.deriveColor(0, 1, 1, opacity));
                    block.setArcWidth(blockSize * 0.3);
                    block.setArcHeight(blockSize * 0.3);
                    block.setX(col * (blockSize + 1));
                    block.setY(row * (blockSize + 1));
                    piecePane.getChildren().add(block);
                }
            }
        }

        // Apply blur for foreground pieces (larger = closer = more blur)
        if (depth > 0.6) {
            piecePane.setEffect(new GaussianBlur(depth * 3));
        }

        FallingPiece piece = new FallingPiece();
        piece.pane = piecePane;
        piece.x = random.nextDouble() * 750;  // Updated for 800px width
        piece.y = -50 - random.nextDouble() * 100; // Start above screen
        piece.speed = 0.3 + depth * 0.7; // Faster when closer (parallax)
        piece.depth = depth;

        piecePane.setLayoutX(piece.x);
        piecePane.setLayoutY(piece.y);

        return piece;
    }

    private void startAnimation() {
        animationTimeline = new Timeline(new KeyFrame(
            Duration.millis(50),
            e -> updatePieces()
        ));
        animationTimeline.setCycleCount(Animation.INDEFINITE);
        animationTimeline.play();
    }

    private void updatePieces() {
        for (int i = 0; i < fallingPieces.size(); i++) {
            FallingPiece piece = fallingPieces.get(i);
            piece.y += piece.speed;
            piece.pane.setLayoutY(piece.y);

            // Reset piece when it goes off screen (updated for 600px height)
            if (piece.y > 610) {
                backgroundPane.getChildren().remove(piece.pane);
                FallingPiece newPiece = createRandomPiece();
                fallingPieces.set(i, newPiece);
                backgroundPane.getChildren().add(newPiece.pane);
            }
        }
    }

    public void stopAnimation() {
        if (animationTimeline != null) {
            animationTimeline.stop();
        }
        if (backgroundMusic != null) {
            backgroundMusic.pause();
        }
    }

    public void resumeAnimation() {
        if (animationTimeline != null) {
            animationTimeline.play();
        }
        if (backgroundMusic != null) {
            backgroundMusic.play();
        }
    }

    public void stopMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }

    public Button getPlayButton() {
        return playButton;
    }

    public Button getTwoPlayerButton() {
        return twoPlayerButton;
    }

    public Button getControlsButton() {
        return controlsButton;
    }

    public Button getQuitButton() {
        return quitButton;
    }

    // Inner class to track falling piece data
    private static class FallingPiece {
        Pane pane;
        double x;
        double y;
        double speed;
        double depth;
    }
}
