package com.comp2042.ui.controllers;

import com.comp2042.audio.AudioManager;
import com.comp2042.game.events.*;
import com.comp2042.game.models.ViewData;
import com.comp2042.ui.panels.*;
import com.comp2042.ui.renderers.GameRenderer;
import com.comp2042.utils.*;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import com.comp2042.game.models.ViewData;

public class GuiController implements Initializable {

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private Text scoreText;

    @FXML
    private GridPane nextBrickPanel;

    @FXML
    private javafx.scene.layout.VBox sidebarContainer;

    @FXML
    private Text highScoreText;

    @FXML
    private Text linesClearedText;

    @FXML
    private Text levelText;

    @FXML
    private javafx.scene.layout.BorderPane gameBoard;

    @FXML
    private javafx.scene.layout.Pane rootPane;

    private HighScoreManager highScoreManager;
    private InputEventListener eventListener;
    private GameController gameController;
    private Timeline timeLine;
    private Runnable modeSwitch;

    private boolean gameInitialized = false;
    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private AudioManager audioManager;
    private GameRenderer gameRenderer;
    private MenuManager menuManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize audio manager
        audioManager = new AudioManager();

        // Load digital font using FontLoader
        FontLoader.loadFont();
        setTextFont(scoreText, 38);

        // Initialize high score manager and display
        highScoreManager = new HighScoreManager();
        setTextFont(highScoreText, 28);
        if (highScoreText != null) {
            highScoreText.setText(String.valueOf(highScoreManager.getHighScore()));
        }

        // Initialize lines cleared text with digital font
        setTextFont(linesClearedText, 28);

        // Initialize level text with digital font
        setTextFont(levelText, 28);

        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gameOverPanel.setVisible(false);

        // Initialize GameRenderer
        gameRenderer = new GameRenderer(gamePanel, brickPanel, nextBrickPanel, rootPane);

        // Initialize pause panel with full-screen overlay
        PausePanel pausePanel = new PausePanel();
        pausePanel.setVisible(false);
        pausePanel.setLayoutX(0);
        pausePanel.setLayoutY(0);
        rootPane.getChildren().add(pausePanel);

        // Initialize start menu with full-screen centering
        StartMenuPanel startMenuPanel = new StartMenuPanel();
        startMenuPanel.setLayoutX(0);
        startMenuPanel.setLayoutY(0);
        rootPane.getChildren().add(startMenuPanel);

        // Initialize controls panel
        ControlsPanel controlsPanel = new ControlsPanel();
        controlsPanel.setLayoutX(0);
        controlsPanel.setLayoutY(0);
        controlsPanel.setVisible(false);
        rootPane.getChildren().add(controlsPanel);

        // Initialize MenuManager
        menuManager = new MenuManager(startMenuPanel, pausePanel, controlsPanel, gameOverPanel,
                                     brickPanel, nextBrickPanel, scoreText, sidebarContainer, gameBoard);

        // Wire up game over panel buttons
        gameOverPanel.getNewGameButton().setOnAction(e -> newGame(null));
        gameOverPanel.getHomeButton().setOnAction(e -> {
            audioManager.playButtonSound();
            menuManager.returnToHome(isPause, audioManager);
        });

        // Wire up pause panel buttons
        pausePanel.getResumeButton().setOnAction(e -> {
            audioManager.playButtonSound();
            togglePause();
        });
        pausePanel.getNewGameButton().setOnAction(e -> newGame(null));
        pausePanel.getHomeButton().setOnAction(e -> {
            audioManager.playButtonSound();
            menuManager.returnToHome(isPause, audioManager);
        });

        // Wire up start menu buttons
        startMenuPanel.getPlayButton().setOnAction(e -> {
            audioManager.playButtonSound();
            if (!gameInitialized) {
                // First time - initialize the game (sets up displayMatrix, etc.)
                if (gameController != null) {
                    gameController.initializeGame();
                }
                gameInitialized = true;
            } else {
                // Already initialized - just create a new game
                if (gameController != null) {
                    gameController.createNewGame();
                }
            }
            audioManager.playOnePlayerMusic();
            menuManager.showGameElements(isPause, isGameOver, gamePanel);
        });
        startMenuPanel.getTwoPlayerButton().setOnAction(e -> {
            audioManager.playButtonSound();
            startMenuPanel.stopMusic();
            if (modeSwitch != null) {
                modeSwitch.run();
            }
        });
        startMenuPanel.getControlsButton().setOnAction(e -> {
            audioManager.playButtonSound();
            menuManager.showControls();
        });
        startMenuPanel.getQuitButton().setOnAction(e -> {
            audioManager.playButtonSound();
            javafx.application.Platform.exit();
        });

        // Wire up controls panel button
        controlsPanel.getBackButton().setOnAction(e -> {
            audioManager.playButtonSound();
            menuManager.hideControls();
        });

        // Show start menu initially
        menuManager.showStartMenu();
    }

    public void setModeSwitch(Runnable modeSwitch) {
        this.modeSwitch = modeSwitch;
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        // Delegate rendering initialization to GameRenderer
        gameRenderer.initGameView(boardMatrix, brick);

        // Initialize game timeline
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();

        // Set timeline reference in MenuManager
        menuManager.setTimeline(timeLine);
    }

    public void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            gameRenderer.refreshBrick(brick);
        }
    }

    public void refreshGameBackground(int[][] board) {
        gameRenderer.refreshGameBackground(board);
    }

    /**
     * Animates row clearing and then refreshes the background
     */
    public void animateAndRefreshBackground(ClearRow clearRow, int[][] board) {
        gameRenderer.animateRowClear(clearRow.getClearedRowIndices(), () -> {
            gameRenderer.refreshGameBackground(board);
        });
    }

    public void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                showScoreNotification(downData.getClearRow().getScoreBonus());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    public void hardDrop() {
        if (isPause.getValue() == Boolean.FALSE) {
            // Get current brick position before hard drop
            ViewData currentBrick = gameRenderer.getCurrentBrickData();
            if (currentBrick != null) {
                int startY = currentBrick.getyPosition();
                int endY = currentBrick.getGhostYPosition();

                // Only animate if there's a distance to fall
                if (endY > startY) {
                    animateHardDrop(startY, endY);
                }
            }

            // Execute the hard drop logic
            DownData downData = eventListener.onHardDropEvent();
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                showScoreNotification(downData.getClearRow().getScoreBonus());
            }
            refreshBrick(downData.getViewData());
            gamePanel.requestFocus();
        }
    }

    /**
     * Animates the brick falling during a hard drop
     */
    private void animateHardDrop(int startY, int endY) {
        // Get the brick size from the actual rectangle
        double brickSize = 30; // Default brick size
        if (brickPanel.getChildren().size() > 0 && brickPanel.getChildren().get(0) instanceof javafx.scene.shape.Rectangle) {
            javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) brickPanel.getChildren().get(0);
            brickSize = rect.getHeight();
        }

        double cellHeight = brickPanel.getVgap() + brickSize;
        double distance = (endY - startY) * cellHeight;

        // Create a quick drop animation (faster than normal fall)
        TranslateTransition transition = new TranslateTransition(Duration.millis(100), brickPanel);
        transition.setByY(distance);
        transition.setOnFinished(e -> brickPanel.setTranslateY(0)); // Reset translation after animation
        transition.play();
    }

    private void showScoreNotification(int scoreBonus) {
        NotificationPanel notificationPanel = new NotificationPanel("+" + scoreBonus);

        // Center the notification on the game board
        double boardWidth = gameBoard.getWidth();
        double boardHeight = gameBoard.getHeight();
        double notificationWidth = 230; // NotificationPanel preferred width
        double notificationHeight = 100; // NotificationPanel preferred height

        // Calculate center position
        double centerX = (boardWidth - notificationWidth) / 2;
        double centerY = (boardHeight - notificationHeight) / 2;

        notificationPanel.setLayoutX(centerX);
        notificationPanel.setLayoutY(centerY);

        groupNotification.getChildren().add(notificationPanel);
        notificationPanel.showScore(groupNotification.getChildren());
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
        // Initialize KeyInputHandler now that we have the eventListener
        KeyInputHandler keyInputHandler = new KeyInputHandler(isPause, isGameOver, this, eventListener);
        gamePanel.setOnKeyPressed(keyInputHandler);
        gamePanel.setOnKeyReleased(keyInputHandler);
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public void bindScore(IntegerProperty integerProperty) {
        scoreText.textProperty().bind(integerProperty.asString());
    }

    public void bindLinesCleared(IntegerProperty integerProperty) {
        linesClearedText.textProperty().bind(integerProperty.asString());
        // Listen for lines cleared changes to play break sound
        integerProperty.addListener((obs, oldVal, newVal) -> {
            // Play sound when lines are cleared (whenever the value increases)
            if (newVal.intValue() > oldVal.intValue()) {
                audioManager.playBreakSound();
            }
        });
    }

    public void bindLevel(IntegerProperty integerProperty) {
        levelText.textProperty().bind(integerProperty.asString());
        // Listen for level changes to update game speed
        integerProperty.addListener((obs, oldVal, newVal) -> {
            updateGameSpeed(newVal.intValue());
            // Play stage clear sound when leveling up (but not on initial level 1)
            if (oldVal.intValue() > 0 && newVal.intValue() > oldVal.intValue()) {
                audioManager.playStageClearSound();
            }
        });
    }

    /**
     * Updates the game speed based on the current level
     */
    private void updateGameSpeed(int level) {
        if (timeLine != null) {
            double newSpeed = GameSpeedCalculator.calculateSpeed(level);
            timeLine.stop();
            timeLine.getKeyFrames().clear();
            timeLine.getKeyFrames().add(new KeyFrame(
                Duration.millis(newSpeed),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
            ));
            if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                timeLine.play();
            }
        }
    }

    /**
     * Checks if the current score beats the high score and updates display
     */
    public void checkAndUpdateHighScore(int currentScore) {
        if (highScoreManager != null && highScoreManager.checkAndUpdateHighScore(currentScore)) {
            // New high score achieved!
            if (highScoreText != null) {
                highScoreText.setText(String.valueOf(highScoreManager.getHighScore()));
            }
        }
    }

    public void gameOver(int currentScore) {
        stopTimeline();
        audioManager.stopGameMusic();

        // Check if this is a new high score
        boolean isNewHighScore = false;
        if (highScoreManager != null) {
            isNewHighScore = highScoreManager.checkAndUpdateHighScore(currentScore);
            if (isNewHighScore && highScoreText != null) {
                highScoreText.setText(String.valueOf(highScoreManager.getHighScore()));
            }
        }

        gameOverPanel.setNewHighScore(isNewHighScore);
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);

        // Play appropriate game over music
        audioManager.playGameOverMusic(isNewHighScore);
    }

    public void newGame(ActionEvent actionEvent) {
        audioManager.playButtonSound();
        stopTimeline();

        // Only restart music if coming from game over (not from pause menu)
        if (isGameOver.getValue() == Boolean.TRUE) {
            audioManager.stopGameOverMusic();
            audioManager.playOnePlayerMusic();
        }

        // Set game state to active BEFORE creating new game so refreshBrick works
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);

        menuManager.hideAllMenus();
        eventListener.createNewGame();
        gamePanel.requestFocus();
        playTimeline();
    }

    public void pauseGame(ActionEvent actionEvent) {
        audioManager.playButtonSound();
        togglePause();
    }

    public void togglePause() {
        if (isGameOver.getValue() == Boolean.TRUE) {
            return; // Don't allow pause when game is over
        }

        if (isPause.getValue() == Boolean.FALSE) {
            // Pause the game
            isPause.setValue(Boolean.TRUE);
            menuManager.showPauseMenu();
        } else {
            // Resume the game
            isPause.setValue(Boolean.FALSE);
            menuManager.hidePauseMenu();
        }
        gamePanel.requestFocus();
    }

    /**
     * Safely stops the timeline if it exists.
     */
    private void stopTimeline() {
        if (timeLine != null) {
            timeLine.stop();
        }
    }

    /**
     * Safely plays the timeline if it exists.
     */
    private void playTimeline() {
        if (timeLine != null) {
            timeLine.play();
        }
    }

    /**
     * Helper method to set font on a Text element with null checking.
     */
    private void setTextFont(Text textElement, int fontSize) {
        if (textElement != null) {
            Font font = FontLoader.getFont(fontSize);
            if (font != null) {
                textElement.setFont(font);
            }
        }
    }
}
