package com.comp2042;

import java.net.URL;
import java.util.ResourceBundle;

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
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize audio manager
        audioManager = new AudioManager();

        FontLoader.loadFont();
        Font digitalFont = FontLoader.getFont(28);

        if (scoreText1 != null && digitalFont != null) {
            scoreText1.setFont(digitalFont);
            levelText1.setFont(digitalFont);
            linesText1.setFont(digitalFont);
        }

        if (scoreText2 != null && digitalFont != null) {
            scoreText2.setFont(digitalFont);
            levelText2.setFont(digitalFont);
            linesText2.setFont(digitalFont);
        }

        rootPane.setFocusTraversable(true);
        rootPane.requestFocus();
        rootPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
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

        // Initialize player game states
        Board board1 = new SimpleBoard(25, 10);
        Board board2 = new SimpleBoard(25, 10);
        player1 = new PlayerGameState(1, board1, gamePanel1, brickPanel1, nextBrickPanel1);
        player2 = new PlayerGameState(2, board2, gamePanel2, brickPanel2, nextBrickPanel2);

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
        int[][] boardMatrix = board.getBoardMatrix();
        ViewData brick = board.getViewData();

        Rectangle[][] displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(GameConstants.BRICK_SIZE, GameConstants.BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        if (player == 1) {
            player1.setDisplayMatrix(displayMatrix);
        } else {
            player2.setDisplayMatrix(displayMatrix);
        }

        Rectangle[][] rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(GameConstants.BRICK_SIZE, GameConstants.BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }

        double baseX = (player == 1) ? GameConstants.PLAYER1_BASE_X : GameConstants.PLAYER2_BASE_X;
        double baseY = (player == 1) ? GameConstants.PLAYER1_BASE_Y : GameConstants.PLAYER2_BASE_Y;

        brickPanel.setLayoutX(baseX + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * GameConstants.BRICK_SIZE);
        brickPanel.setLayoutY(-42 + baseY + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * GameConstants.BRICK_SIZE);

        if (player == 1) {
            player1.setRectangles(rectangles);
        } else {
            player2.setRectangles(rectangles);
        }

        GridPane ghostBrickPanel = new GridPane();
        ghostBrickPanel.setHgap(1);
        ghostBrickPanel.setVgap(1);
        rootPane.getChildren().add(0, ghostBrickPanel);

        Rectangle[][] ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(GameConstants.BRICK_SIZE, GameConstants.BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setArcHeight(9);
                rectangle.setArcWidth(9);
                ghostRectangles[i][j] = rectangle;
                ghostBrickPanel.add(rectangle, j, i);
            }
        }

        if (player == 1) {
            player1.setGhostBrickPanel(ghostBrickPanel);
            player1.setGhostRectangles(ghostRectangles);
        } else {
            player2.setGhostBrickPanel(ghostBrickPanel);
            player2.setGhostRectangles(ghostRectangles);
        }

        Rectangle[][] nextBrickRectangles = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rectangle = new Rectangle(GameConstants.BRICK_SIZE, GameConstants.BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                nextBrickRectangles[i][j] = rectangle;
                nextBrickPanel.add(rectangle, j, i);
            }
        }
        updateNextBrickPanel(player, brick.getNextBrickData(), nextBrickRectangles);

        if (player == 1) {
            player1.setNextBrickRectangles(nextBrickRectangles);
        } else {
            player2.setNextBrickRectangles(nextBrickRectangles);
        }

        Timeline timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(player, new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();

        if (player == 1) {
            player1.setTimeline(timeLine);
        } else {
            player2.setTimeline(timeLine);
        }
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
        Board board = playerState.getBoard();
        ViewData brick = board.getViewData();
        GridPane brickPanel = (player == 1) ? brickPanel1 : brickPanel2;
        Rectangle[][] rectangles = playerState.getRectangles();

        double baseX = playerState.getBaseX();
        double baseY = playerState.getBaseY();

        int xPos = brick.getxPosition();
        int yPos = brick.getyPosition();
        brickPanel.setLayoutX(baseX + xPos * brickPanel.getVgap() + xPos * GameConstants.BRICK_SIZE);
        brickPanel.setLayoutY(-42 + baseY + yPos * brickPanel.getHgap() + yPos * GameConstants.BRICK_SIZE);

        int[][] brickData = brick.getBrickData();
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                setRectangleData(brickData[i][j], rectangles[i][j]);
            }
        }

        updateGhostBrick(player, brick);
        updateNextBrickPanel(player, brick.getNextBrickData(), playerState.getNextBrickRectangles());
    }

    private void updateGhostBrick(int player, ViewData brick) {
        PlayerGameState playerState = getPlayerState(player);
        GridPane ghostBrickPanel = playerState.getGhostBrickPanel();
        Rectangle[][] ghostRectangles = playerState.getGhostRectangles();

        double baseX = playerState.getBaseX();
        double baseY = playerState.getBaseY();

        int xPos = brick.getxPosition();
        int ghostYPos = brick.getGhostYPosition();
        ghostBrickPanel.setLayoutX(baseX + xPos * ghostBrickPanel.getVgap() + xPos * GameConstants.BRICK_SIZE);
        ghostBrickPanel.setLayoutY(-42 + baseY + ghostYPos * ghostBrickPanel.getHgap() + ghostYPos * GameConstants.BRICK_SIZE);

        int[][] brickData = brick.getBrickData();
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                int cellValue = brickData[i][j];
                if (cellValue != 0) {
                    Paint color = getFillColor(cellValue);
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

    private void updateNextBrickPanel(int player, int[][] nextBrickData, Rectangle[][] nextBrickRectangles) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                nextBrickRectangles[i][j].setFill(Color.TRANSPARENT);
                nextBrickRectangles[i][j].setArcHeight(0);
                nextBrickRectangles[i][j].setArcWidth(0);
            }
        }

        if (nextBrickData != null && nextBrickData.length > 0) {
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

            if (maxRow >= 0) {
                int brickHeight = maxRow - minRow + 1;
                int brickWidth = maxCol - minCol + 1;
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

    private void refreshGameBackground(int player) {
        PlayerGameState playerState = getPlayerState(player);
        Board board = playerState.getBoard();
        Rectangle[][] displayMatrix = playerState.getDisplayMatrix();
        int[][] boardMatrix = board.getBoardMatrix();

        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                setRectangleData(boardMatrix[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    private Paint getFillColor(int i) {
        switch (i) {
            case 0: return Color.TRANSPARENT;
            case 1: return Color.AQUA;
            case 2: return Color.BLUEVIOLET;
            case 3: return Color.DARKGREEN;
            case 4: return Color.YELLOW;
            case 5: return Color.RED;
            case 6: return Color.BEIGE;
            case 7: return Color.BURLYWOOD;
            default: return Color.WHITE;
        }
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
}
