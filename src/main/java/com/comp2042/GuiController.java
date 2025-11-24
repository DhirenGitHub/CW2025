package com.comp2042;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;
    private static final double GAME_BASE_X = 237.0;  // 225 (gameBoard) + 12 (border)
    private static final double GAME_BASE_Y = 65.0;   // 45 (gameBoard) + 12 (border)

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

    private PausePanel pausePanel;

    private HighScoreManager highScoreManager;

    private StartMenuPanel startMenuPanel;

    private Rectangle[][] displayMatrix;

    private boolean gameInitialized = false;

    private Rectangle[][] nextBrickRectangles;

    private GridPane ghostBrickPanel;

    private Rectangle[][] ghostRectangles;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    private Timeline timeLine;

    private GameController gameController;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private Runnable modeSwitch;

    private MediaPlayer gameBackgroundMusic;
    private MediaPlayer stageClearSound;
    private MediaPlayer breakSound;
    private MediaPlayer gameOverMusic;
    private MediaPlayer buttonSound;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load digital font using FontLoader
        FontLoader.loadFont();
        Font digitalFont = FontLoader.getFont(38);
        if (digitalFont != null && scoreText != null) {
            scoreText.setFont(digitalFont);
        }

        // Initialize high score manager and display
        highScoreManager = new HighScoreManager();
        if (highScoreText != null) {
            Font highScoreFont = FontLoader.getFont(28);
            if (highScoreFont != null) {
                highScoreText.setFont(highScoreFont);
            }
            highScoreText.setText(String.valueOf(highScoreManager.getHighScore()));
        }

        // Initialize lines cleared text with digital font
        if (linesClearedText != null) {
            Font linesClearedFont = FontLoader.getFont(28);
            if (linesClearedFont != null) {
                linesClearedText.setFont(linesClearedFont);
            }
        }

        // Initialize level text with digital font
        if (levelText != null) {
            Font levelFont = FontLoader.getFont(28);
            if (levelFont != null) {
                levelText.setFont(levelFont);
            }
        }

        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.SPACE) {
                        hardDrop();
                        keyEvent.consume();
                    }
                }
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    togglePause();
                    keyEvent.consume();
                }
            }
        });
        gameOverPanel.setVisible(false);

        // Wire up game over panel buttons
        gameOverPanel.getNewGameButton().setOnAction(e -> newGame(null));
        gameOverPanel.getHomeButton().setOnAction(e -> {
            playButtonSound();
            returnToHome();
        });

        // Initialize pause panel with full-screen overlay
        pausePanel = new PausePanel();
        pausePanel.setVisible(false);
        pausePanel.setLayoutX(0);
        pausePanel.setLayoutY(0);
        rootPane.getChildren().add(pausePanel);

        // Wire up pause panel buttons
        pausePanel.getResumeButton().setOnAction(e -> {
            playButtonSound();
            togglePause();
        });
        pausePanel.getNewGameButton().setOnAction(e -> newGame(null));
        pausePanel.getHomeButton().setOnAction(e -> {
            playButtonSound();
            returnToHome();
        });

        // Initialize start menu with full-screen centering
        startMenuPanel = new StartMenuPanel();
        startMenuPanel.setLayoutX(0);
        startMenuPanel.setLayoutY(0);
        rootPane.getChildren().add(startMenuPanel);
        startMenuPanel.getPlayButton().setOnAction(e -> {
            playButtonSound();
            startGame();
        });
        startMenuPanel.getTwoPlayerButton().setOnAction(e -> {
            playButtonSound();
            startMenuPanel.stopMusic();
            if (modeSwitch != null) {
                modeSwitch.run();
            }
        });
        startMenuPanel.getQuitButton().setOnAction(e -> {
            playButtonSound();
            javafx.application.Platform.exit();
        });

        // Show start menu initially
        showStartMenu();
    }

    public void setModeSwitch(Runnable modeSwitch) {
        this.modeSwitch = modeSwitch;
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.setLayoutX(GAME_BASE_X + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(-42 + GAME_BASE_Y + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);

        // Initialize ghost brick panel
        ghostBrickPanel = new GridPane();
        ghostBrickPanel.setHgap(1);
        ghostBrickPanel.setVgap(1);
        rootPane.getChildren().add(0, ghostBrickPanel); // Add at index 0 so it's behind other elements

        ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setArcHeight(9);
                rectangle.setArcWidth(9);
                ghostRectangles[i][j] = rectangle;
                ghostBrickPanel.add(rectangle, j, i);
            }
        }

        // Initialize next brick preview panel
        initNextBrickPanel(brick.getNextBrickData());

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    private void initNextBrickPanel(int[][] nextBrickData) {
        // Create a 4x4 grid for the next brick preview
        nextBrickRectangles = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                nextBrickRectangles[i][j] = rectangle;
                nextBrickPanel.add(rectangle, j, i);
            }
        }
        updateNextBrickPanel(nextBrickData);
    }

    private void updateNextBrickPanel(int[][] nextBrickData) {
        // Clear the preview panel
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                nextBrickRectangles[i][j].setFill(Color.TRANSPARENT);
                nextBrickRectangles[i][j].setArcHeight(0);
                nextBrickRectangles[i][j].setArcWidth(0);
            }
        }

        // Display the next brick centered in the 4x4 grid
        if (nextBrickData != null && nextBrickData.length > 0) {
            // Find the actual bounds of the brick (non-zero cells)
            int minRow = nextBrickData.length, maxRow = -1;
            int minCol = nextBrickData[0].length, maxCol = -1;

            for (int i = 0; i < nextBrickData.length; i++) {
                for (int j = 0; j < nextBrickData[i].length; j++) {
                    if (nextBrickData[i][j] != 0) {
                        minRow = Math.min(minRow, i);
                        maxRow = Math.max(maxRow, i);
                        minCol = Math.min(minCol, j);
                        maxCol = Math.max(maxCol, j);
                    }
                }
            }

            // Calculate actual brick dimensions
            if (maxRow >= 0) {
                int brickHeight = maxRow - minRow + 1;
                int brickWidth = maxCol - minCol + 1;

                // Center the brick in the 4x4 grid
                int offsetRow = (4 - brickHeight) / 2;
                int offsetCol = (4 - brickWidth) / 2;

                for (int i = 0; i < nextBrickData.length; i++) {
                    for (int j = 0; j < nextBrickData[i].length; j++) {
                        if (nextBrickData[i][j] != 0) {
                            int targetRow = offsetRow + (i - minRow);
                            int targetCol = offsetCol + (j - minCol);

                            if (targetRow >= 0 && targetRow < 4 && targetCol >= 0 && targetCol < 4) {
                                Rectangle rect = nextBrickRectangles[targetRow][targetCol];
                                rect.setFill(getFillColor(nextBrickData[i][j]));
                                rect.setArcHeight(9);
                                rect.setArcWidth(9);
                            }
                        }
                    }
                }
            }
        }
    }

    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.DARKGREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }


    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            brickPanel.setLayoutX(GAME_BASE_X + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(-42 + GAME_BASE_Y + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
            // Update ghost brick
            updateGhostBrick(brick);
            // Update next brick preview
            updateNextBrickPanel(brick.getNextBrickData());
        }
    }

    private void updateGhostBrick(ViewData brick) {
        // Position ghost brick at the landing position (same X as brickPanel, Y at ghost position)
        ghostBrickPanel.setLayoutX(GAME_BASE_X + brick.getxPosition() * ghostBrickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        ghostBrickPanel.setLayoutY(-42 + GAME_BASE_Y + brick.getGhostYPosition() * ghostBrickPanel.getHgap() + brick.getGhostYPosition() * BRICK_SIZE);

        // Update ghost brick appearance - semi-transparent version of the brick
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                if (brick.getBrickData()[i][j] != 0) {
                    Paint color = getFillColor(brick.getBrickData()[i][j]);
                    // Make it semi-transparent
                    if (color instanceof Color) {
                        Color solidColor = (Color) color;
                        Color ghostColor = new Color(solidColor.getRed(), solidColor.getGreen(), solidColor.getBlue(), 0.3);
                        ghostRectangles[i][j].setFill(ghostColor);
                        ghostRectangles[i][j].setStroke(solidColor.deriveColor(0, 1, 1, 0.5));
                        ghostRectangles[i][j].setStrokeWidth(1);
                    } else {
                        ghostRectangles[i][j].setFill(Color.TRANSPARENT);
                    }
                } else {
                    ghostRectangles[i][j].setFill(Color.TRANSPARENT);
                    ghostRectangles[i][j].setStroke(null);
                }
            }
        }
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                showScoreNotification(downData.getClearRow().getScoreBonus());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    private void hardDrop() {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onHardDropEvent();
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                showScoreNotification(downData.getClearRow().getScoreBonus());
            }
            refreshBrick(downData.getViewData());
            gamePanel.requestFocus();
        }
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
                playBreakSound();
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
                playStageClearSound();
            }
        });
    }

    /**
     * Calculate the delay in milliseconds based on level
     * Formula: Base speed decreases as level increases
     * Level 1: 400ms, Level 2: 360ms, Level 3: 320ms, etc.
     * Minimum speed: 100ms (at level 15+)
     */
    private double calculateSpeed(int level) {
        double baseSpeed = 400.0;
        double speedDecrease = 30.0; // Decrease by 30ms per level
        double minSpeed = 100.0;

        double speed = baseSpeed - ((level - 1) * speedDecrease);
        return Math.max(speed, minSpeed);
    }

    /**
     * Updates the game speed based on the current level
     */
    private void updateGameSpeed(int level) {
        if (timeLine != null) {
            double newSpeed = calculateSpeed(level);
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
        timeLine.stop();
        stopGameMusic();

        // Check if this is a new high score
        boolean isNewHighScore = false;
        if (highScoreManager != null) {
            isNewHighScore = highScoreManager.checkAndUpdateHighScore(currentScore);
            if (isNewHighScore && highScoreText != null) {
                highScoreText.setText(String.valueOf(highScoreManager.getHighScore()));
            }
        }

        System.out.println("Game Over - Current Score: " + currentScore + ", Is New High Score: " + isNewHighScore);
        gameOverPanel.setNewHighScore(isNewHighScore);
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);

        // Play appropriate game over music
        playGameOverMusic(isNewHighScore);
    }

    public void newGame(ActionEvent actionEvent) {
        playButtonSound();
        if (timeLine != null) {
            timeLine.stop();
        }
        stopGameOverMusic();
        initializeGameMusic();
        gameOverPanel.setVisible(false);
        pausePanel.setVisible(false);
        startMenuPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        if (timeLine != null) {
            timeLine.play();
        }
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void pauseGame(ActionEvent actionEvent) {
        playButtonSound();
        togglePause();
    }

    private void togglePause() {
        if (isGameOver.getValue() == Boolean.TRUE) {
            return; // Don't allow pause when game is over
        }

        if (isPause.getValue() == Boolean.FALSE) {
            // Pause the game
            timeLine.pause();
            isPause.setValue(Boolean.TRUE);
            pausePanel.setVisible(true);
        } else {
            // Resume the game
            timeLine.play();
            isPause.setValue(Boolean.FALSE);
            pausePanel.setVisible(false);
        }
        gamePanel.requestFocus();
    }

    private void showStartMenu() {
        startMenuPanel.setVisible(true);
        startMenuPanel.resumeAnimation();
        gameOverPanel.setVisible(false);
        pausePanel.setVisible(false);
        // Hide game elements
        if (brickPanel != null) {
            brickPanel.setVisible(false);
        }
        if (nextBrickPanel != null) {
            nextBrickPanel.setVisible(false);
        }
        if (scoreText != null) {
            scoreText.setVisible(false);
        }
        if (ghostBrickPanel != null) {
            ghostBrickPanel.setVisible(false);
        }
        if (sidebarContainer != null) {
            sidebarContainer.setVisible(false);
        }
        if (gameBoard != null) {
            gameBoard.setVisible(false);
        }
        if (timeLine != null) {
            timeLine.stop();
        }
    }

    private void startGame() {
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
        startMenuPanel.stopMusic();
        startMenuPanel.setVisible(false);
        initializeGameMusic();
        if (brickPanel != null) {
            brickPanel.setVisible(true);
        }
        if (nextBrickPanel != null) {
            nextBrickPanel.setVisible(true);
        }
        if (scoreText != null) {
            scoreText.setVisible(true);
        }
        if (ghostBrickPanel != null) {
            ghostBrickPanel.setVisible(true);
        }
        if (sidebarContainer != null) {
            sidebarContainer.setVisible(true);
        }
        if (gameBoard != null) {
            gameBoard.setVisible(true);
        }
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        if (timeLine != null) {
            timeLine.play();
        }
        gamePanel.requestFocus();
    }

    private void initializeGameMusic() {
        try {
            if (gameBackgroundMusic != null) {
                gameBackgroundMusic.stop();
            }
            String musicPath = getClass().getResource("/audio/one_player_bg.mp3").toExternalForm();
            Media media = new Media(musicPath);
            gameBackgroundMusic = new MediaPlayer(media);
            gameBackgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
            gameBackgroundMusic.setVolume(0.5);
            gameBackgroundMusic.play();
        } catch (Exception e) {
            System.err.println("Failed to load game background music: " + e.getMessage());
        }
    }

    private void stopGameMusic() {
        if (gameBackgroundMusic != null) {
            gameBackgroundMusic.stop();
        }
    }

    private void playStageClearSound() {
        try {
            if (stageClearSound != null) {
                stageClearSound.stop();
            }
            String soundPath = getClass().getResource("/audio/stage_clear.mp3").toExternalForm();
            Media media = new Media(soundPath);
            stageClearSound = new MediaPlayer(media);
            stageClearSound.setVolume(0.7);
            stageClearSound.play();
        } catch (Exception e) {
            System.err.println("Failed to load stage clear sound: " + e.getMessage());
        }
    }

    private void playBreakSound() {
        try {
            if (breakSound != null) {
                breakSound.stop();
            }
            String soundPath = getClass().getResource("/audio/break.wav").toExternalForm();
            Media media = new Media(soundPath);
            breakSound = new MediaPlayer(media);
            breakSound.setVolume(0.6);
            breakSound.play();
        } catch (Exception e) {
            System.err.println("Failed to load break sound: " + e.getMessage());
        }
    }

    private void playGameOverMusic(boolean isNewHighScore) {
        try {
            if (gameOverMusic != null) {
                gameOverMusic.stop();
            }
            String musicFile = isNewHighScore ? "highscore.mp3" : "one_player_gameover.mp3";
            String musicPath = getClass().getResource("/audio/" + musicFile).toExternalForm();
            Media media = new Media(musicPath);
            gameOverMusic = new MediaPlayer(media);
            gameOverMusic.setVolume(0.5);
            gameOverMusic.play();
        } catch (Exception e) {
            System.err.println("Failed to load game over music: " + e.getMessage());
        }
    }

    private void stopGameOverMusic() {
        if (gameOverMusic != null) {
            gameOverMusic.stop();
        }
    }

    private void playButtonSound() {
        try {
            if (buttonSound != null) {
                buttonSound.stop();
            }
            String soundPath = getClass().getResource("/audio/button.wav").toExternalForm();
            Media media = new Media(soundPath);
            buttonSound = new MediaPlayer(media);
            buttonSound.setVolume(0.5);
            buttonSound.play();
        } catch (Exception e) {
            System.err.println("Failed to load button sound: " + e.getMessage());
        }
    }

    private void returnToHome() {
        if (timeLine != null) {
            timeLine.stop();
        }
        stopGameMusic();
        stopGameOverMusic();
        isPause.setValue(Boolean.FALSE);
        pausePanel.setVisible(false);
        showStartMenu();
    }
}
