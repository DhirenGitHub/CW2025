package com.comp2042.ui.controllers;

import com.comp2042.audio.AudioManager;
import com.comp2042.game.events.*;
import com.comp2042.game.models.*;
import com.comp2042.ui.panels.*;
import com.comp2042.ui.renderers.TwoPlayerRenderer;
import com.comp2042.utils.*;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class TwoPlayerGuiController implements Initializable {

    // Player 1 components
    @FXML private GridPane gamePanel1;
    @FXML private GridPane brickPanel1;
    @FXML private Text scoreText1;
    @FXML private Text levelText1;
    @FXML private Text linesText1;
    @FXML private GridPane nextBrickPanel1;

    // Player 2 components
    @FXML private GridPane gamePanel2;
    @FXML private GridPane brickPanel2;
    @FXML private Text scoreText2;
    @FXML private Text levelText2;
    @FXML private Text linesText2;
    @FXML private GridPane nextBrickPanel2;

    @FXML private javafx.scene.layout.Pane rootPane;
    @FXML private Group groupNotification;
    @FXML private VictoryPanel victoryPanel;

    private PausePanel pausePanel;

    // Player game states (encapsulates all player-specific data)
    private PlayerGameState player1;
    private PlayerGameState player2;

    private final BooleanProperty isPause = new SimpleBooleanProperty(false);
    private final BooleanProperty isGameOver = new SimpleBooleanProperty(false);
    private boolean gameInitialized = false;

    private Runnable modeSwitch;
    private AudioManager audioManager;
    private TwoPlayerRenderer renderer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize audio manager and renderer
        audioManager = new AudioManager();
        renderer = new TwoPlayerRenderer(rootPane, brickPanel1, brickPanel2);

        // Initialize fonts for all text elements
        initializeFonts();

        rootPane.setFocusTraversable(true);
        rootPane.requestFocus();

        // Track pressed keys to prevent auto-repeat
        Set<KeyCode> pressedKeys = new HashSet<>();

        rootPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                KeyCode code = keyEvent.getCode();

                // Ignore auto-repeat events (when key is held down)
                if (pressedKeys.contains(code)) {
                    return;
                }
                pressedKeys.add(code);

                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    handlePlayer1Input(keyEvent);
                    handlePlayer2Input(keyEvent);
                }
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    togglePause();
                    keyEvent.consume();
                }
            }
        });

        rootPane.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                pressedKeys.remove(keyEvent.getCode());
            }
        });

        // Initialize player game states
        Board board1 = new SimpleBoard(25, 10);
        Board board2 = new SimpleBoard(25, 10);
        player1 = new PlayerGameState(1, board1, gamePanel1, brickPanel1, nextBrickPanel1);
        player2 = new PlayerGameState(2, board2, gamePanel2, brickPanel2, nextBrickPanel2);

        // Initialize panels
        initializePanels();

        audioManager.playTwoPlayerMusic();
        initializeGame();
    }

    private void handlePlayer1Input(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.A) {
            moveBrickLeft(1);
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.D) {
            moveBrickRight(1);
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.W) {
            rotateBrick(1);
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.S) {
            moveDown(1, new MoveEvent(EventType.DOWN, EventSource.USER));
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.SPACE) {
            hardDrop(1);
            keyEvent.consume();
        }
    }

    private void handlePlayer2Input(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.LEFT) {
            moveBrickLeft(2);
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.RIGHT) {
            moveBrickRight(2);
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.UP) {
            rotateBrick(2);
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.DOWN) {
            moveDown(2, new MoveEvent(EventType.DOWN, EventSource.USER));
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.SHIFT) {
            hardDrop(2);
            keyEvent.consume();
        }
    }

    public void initializeGame() {
        player1.getBoard().createNewBrick();
        initGameView(1, gamePanel1, brickPanel1, nextBrickPanel1, player1.getBoard());
        bindScore(1, player1.getBoard().getScore());

        player2.getBoard().createNewBrick();
        initGameView(2, gamePanel2, brickPanel2, nextBrickPanel2, player2.getBoard());
        bindScore(2, player2.getBoard().getScore());

        gameInitialized = true;
    }

    private void initGameView(int player, GridPane gamePanel, GridPane brickPanel,
                              GridPane nextBrickPanel, Board board) {
        PlayerGameState playerState = getPlayerState(player);
        int[][] boardMatrix = board.getBoardMatrix();
        ViewData brick = board.getViewData();

        // Use renderer to initialize all visual components
        Rectangle[][] displayMatrix = renderer.initializeDisplayMatrix(gamePanel, boardMatrix);
        playerState.setDisplayMatrix(displayMatrix);

        double baseX = playerState.getBaseX();
        double baseY = playerState.getBaseY();
        Rectangle[][] rectangles = renderer.initializeBrickPanel(brickPanel, brick, baseX, baseY);
        playerState.setRectangles(rectangles);

        GridPane ghostBrickPanel = renderer.initializeGhostBrickPanel(brick);
        Rectangle[][] ghostRectangles = renderer.initializeGhostRectangles(ghostBrickPanel, brick);
        playerState.setGhostBrickPanel(ghostBrickPanel);
        playerState.setGhostRectangles(ghostRectangles);

        Rectangle[][] nextBrickRectangles = renderer.initializeNextBrickPanel(nextBrickPanel, brick.getNextBrickData());
        playerState.setNextBrickRectangles(nextBrickRectangles);

        // Create and start timeline
        Timeline timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(player, new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
        playerState.setTimeline(timeLine);
    }

    private void moveBrickLeft(int player) {
        getPlayerState(player).getBoard().moveBrickLeft();
        refreshBrick(player);
    }

    private void moveBrickRight(int player) {
        getPlayerState(player).getBoard().moveBrickRight();
        refreshBrick(player);
    }

    private void rotateBrick(int player) {
        getPlayerState(player).getBoard().rotateLeftBrick();
        refreshBrick(player);
    }

    private void moveDown(int player, MoveEvent event) {
        if (isPause.getValue() == Boolean.TRUE || isGameOver.getValue() == Boolean.TRUE) {
            return;
        }

        PlayerGameState playerState = getPlayerState(player);
        Board board = playerState.getBoard();
        boolean canMove = board.moveBrickDown();

        if (!canMove) {
            ClearRow clearRow = board.landBrickAndClearRows();
            if (clearRow.getLinesRemoved() > 0) {
                updateGameSpeed(player, board.getScore().levelProperty().get());
            }

            if (board.createNewBrick()) {
                playerLost(player);
                return;
            }

            refreshGameBackground(player);
        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }
        refreshBrick(player);
        rootPane.requestFocus();
    }

    private void hardDrop(int player) {
        if (isPause.getValue() == Boolean.TRUE || isGameOver.getValue() == Boolean.TRUE) {
            return;
        }

        Board board = getPlayerState(player).getBoard();
        board.hardDrop();
        ClearRow clearRow = board.landBrickAndClearRows();
        if (clearRow.getLinesRemoved() > 0) {
            updateGameSpeed(player, board.getScore().levelProperty().get());
        }

        if (board.createNewBrick()) {
            playerLost(player);
            return;
        }

        refreshGameBackground(player);
        refreshBrick(player);
        rootPane.requestFocus();
    }

    private void refreshBrick(int player) {
        PlayerGameState playerState = getPlayerState(player);
        ViewData brick = playerState.getBoard().getViewData();

        renderer.refreshBrick(player, brick, playerState.getRectangles(),
                            playerState.getBaseX(), playerState.getBaseY());

        GridPane brickPanel = (player == 1) ? brickPanel1 : brickPanel2;
        renderer.updateGhostBrick(brick, playerState.getGhostBrickPanel(),
                                playerState.getGhostRectangles(), brickPanel);
        renderer.updateNextBrickPanel(brick.getNextBrickData(), playerState.getNextBrickRectangles());
    }

    private void refreshGameBackground(int player) {
        PlayerGameState playerState = getPlayerState(player);
        renderer.refreshGameBackground(playerState.getDisplayMatrix(),
                                      playerState.getBoard().getBoardMatrix());
    }

    private void bindScore(int player, Score score) {
        if (player == 1) {
            scoreText1.textProperty().bind(score.scoreProperty().asString());
            levelText1.textProperty().bind(score.levelProperty().asString());
            linesText1.textProperty().bind(score.linesClearedProperty().asString());
            score.levelProperty().addListener((obs, oldVal, newVal) -> {
                updateGameSpeed(1, newVal.intValue());
            });
        } else {
            scoreText2.textProperty().bind(score.scoreProperty().asString());
            levelText2.textProperty().bind(score.levelProperty().asString());
            linesText2.textProperty().bind(score.linesClearedProperty().asString());
            score.levelProperty().addListener((obs, oldVal, newVal) -> {
                updateGameSpeed(2, newVal.intValue());
            });
        }
    }

    private void updateGameSpeed(int player, int level) {
        PlayerGameState playerState = getPlayerState(player);
        Timeline timeLine = playerState.getTimeline();
        if (timeLine != null) {
            double newSpeed = GameSpeedCalculator.calculateSpeed(level);
            timeLine.stop();
            timeLine.getKeyFrames().clear();
            timeLine.getKeyFrames().add(new KeyFrame(
                Duration.millis(newSpeed),
                ae -> moveDown(player, new MoveEvent(EventType.DOWN, EventSource.THREAD))
            ));
            if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                timeLine.play();
            }
        }
    }

    private void playerLost(int player) {
        player1.stopTimeline();
        player2.stopTimeline();
        audioManager.stopGameMusic();

        int winner = (player == 1) ? 2 : 1;
        victoryPanel.setWinner(winner);
        victoryPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);

        // Play two player game over music
        audioManager.playTwoPlayerGameOverMusic();
    }

    public void newGame() {
        player1.stopTimeline();
        player2.stopTimeline();
        audioManager.stopGameOverMusic();
        audioManager.playTwoPlayerMusic();

        victoryPanel.setVisible(false);
        pausePanel.setVisible(false);

        player1.getBoard().newGame();
        player2.getBoard().newGame();
        refreshGameBackground(1);
        refreshGameBackground(2);

        player1.playTimeline();
        player2.playTimeline();

        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        rootPane.requestFocus();
    }

    private void togglePause() {
        if (isGameOver.getValue() == Boolean.TRUE) {
            return;
        }

        if (isPause.getValue() == Boolean.FALSE) {
            player1.pauseTimeline();
            player2.pauseTimeline();
            isPause.setValue(Boolean.TRUE);
            pausePanel.setVisible(true);
        } else {
            player1.playTimeline();
            player2.playTimeline();
            isPause.setValue(Boolean.FALSE);
            pausePanel.setVisible(false);
        }
        rootPane.requestFocus();
    }

    public void setModeSwitch(Runnable modeSwitch) {
        this.modeSwitch = modeSwitch;
    }

    /**
     * Helper method to get the player state based on player number.
     * Encapsulates the repeated (player == 1) ? player1 : player2 pattern.
     */
    private PlayerGameState getPlayerState(int player) {
        return (player == 1) ? player1 : player2;
    }

    private void returnToHome() {
        player1.stopTimeline();
        player2.stopTimeline();
        audioManager.stopAllAudio();

        if (modeSwitch != null) {
            modeSwitch.run();
        }
    }

    /**
     * Initializes fonts for all text elements
     */
    private void initializeFonts() {
        FontLoader.loadFont();
        Font digitalFont = FontLoader.getFont(28);
        if (digitalFont != null) {
            setTextFont(scoreText1, digitalFont);
            setTextFont(levelText1, digitalFont);
            setTextFont(linesText1, digitalFont);
            setTextFont(scoreText2, digitalFont);
            setTextFont(levelText2, digitalFont);
            setTextFont(linesText2, digitalFont);
        }
    }

    /**
     * Sets font on a text element with null checking
     */
    private void setTextFont(Text text, Font font) {
        if (text != null && font != null) {
            text.setFont(font);
        }
    }

    /**
     * Initializes victory and pause panels with their button handlers
     */
    private void initializePanels() {
        if (victoryPanel == null) {
            victoryPanel = new VictoryPanel();
            victoryPanel.setLayoutX(0);
            victoryPanel.setLayoutY(0);
            rootPane.getChildren().add(victoryPanel);
        }
        victoryPanel.setVisible(false);
        victoryPanel.getNewGameButton().setOnAction(e -> {
            audioManager.playButtonSound();
            newGame();
        });
        victoryPanel.getHomeButton().setOnAction(e -> {
            audioManager.playButtonSound();
            returnToHome();
        });

        pausePanel = new PausePanel();
        pausePanel.setVisible(false);
        pausePanel.setLayoutX(0);
        pausePanel.setLayoutY(0);
        rootPane.getChildren().add(pausePanel);
        pausePanel.getResumeButton().setOnAction(e -> {
            audioManager.playButtonSound();
            togglePause();
        });
        pausePanel.getNewGameButton().setOnAction(e -> {
            audioManager.playButtonSound();
            newGame();
        });
        pausePanel.getHomeButton().setOnAction(e -> {
            audioManager.playButtonSound();
            returnToHome();
        });
    }
}
