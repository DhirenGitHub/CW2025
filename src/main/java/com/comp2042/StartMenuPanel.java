package com.comp2042;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Start menu panel displayed when the game launches.
 * Features animated falling Tetris pieces in the background.
 */
public class StartMenuPanel extends StackPane {

    private final Button playButton;
    private final Pane backgroundPane;
    private final List<FallingPiece> fallingPieces;
    private final Random random;
    private Timeline animationTimeline;
    private MediaPlayer backgroundMusic;

    // Tetromino shapes (I, O, T, S, Z, J, L)
    private static final int[][][] TETROMINOES = {
        // I piece
        {{1, 1, 1, 1}},
        // O piece
        {{1, 1}, {1, 1}},
        // T piece
        {{0, 1, 0}, {1, 1, 1}},
        // S piece
        {{0, 1, 1}, {1, 1, 0}},
        // Z piece
        {{1, 1, 0}, {0, 1, 1}},
        // J piece
        {{1, 0, 0}, {1, 1, 1}},
        // L piece
        {{0, 0, 1}, {1, 1, 1}}
    };

    // Colors for each tetromino
    private static final Color[] TETROMINO_COLORS = {
        Color.CYAN,      // I
        Color.YELLOW,    // O
        Color.PURPLE,    // T
        Color.GREEN,     // S
        Color.RED,       // Z
        Color.BLUE,      // J
        Color.ORANGE     // L
    };

    public StartMenuPanel() {
        this.setPrefSize(350, 510);
        this.random = new Random();
        this.fallingPieces = new ArrayList<>();

        // Create gradient background
        backgroundPane = new Pane();
        backgroundPane.setPrefSize(350, 510);

        // Navy to black gradient
        Rectangle gradientRect = new Rectangle(350, 510);
        LinearGradient gradient = new LinearGradient(
            0, 0, 0, 1, true, null,
            new Stop(0, Color.rgb(10, 20, 50)),
            new Stop(1, Color.rgb(0, 0, 0))
        );
        gradientRect.setFill(gradient);
        backgroundPane.getChildren().add(gradientRect);

        // Create menu content
        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setMaxSize(350, 510);

        final Label titleLabel = new Label("TETRIS");
        titleLabel.getStyleClass().add("startMenuStyle");

        playButton = new Button("PLAY");
        playButton.getStyleClass().add("ipad-dark-grey");
        playButton.setPrefWidth(150);

        menuBox.getChildren().addAll(titleLabel, playButton);

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
            String musicPath = getClass().getResource("/bg_music.mp3").toExternalForm();
            Media media = new Media(musicPath);
            backgroundMusic = new MediaPlayer(media);
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE); // Loop forever
            backgroundMusic.setVolume(0); // 50% volume
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
        int type = random.nextInt(TETROMINOES.length);
        int[][] shape = TETROMINOES[type];
        Color baseColor = TETROMINO_COLORS[type];

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
        piece.x = random.nextDouble() * 300;
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

            // Reset piece when it goes off screen
            if (piece.y > 520) {
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

    // Inner class to track falling piece data
    private static class FallingPiece {
        Pane pane;
        double x;
        double y;
        double speed;
        double depth;
    }
}
