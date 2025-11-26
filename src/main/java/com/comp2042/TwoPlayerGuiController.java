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

    // Game state
    private Board board1;
    private Board board2;
    private Rectangle[][] displayMatrix1;
    private Rectangle[][] displayMatrix2;
    private Rectangle[][] rectangles1;
    private Rectangle[][] rectangles2;
    private Rectangle[][] nextBrickRectangles1;
    private Rectangle[][] nextBrickRectangles2;
    private GridPane ghostBrickPanel1;
    private GridPane ghostBrickPanel2;
    private Rectangle[][] ghostRectangles1;
    private Rectangle[][] ghostRectangles2;

    private Timeline timeLine1;
    private Timeline timeLine2;

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

        board1 = new SimpleBoard(25, 10);
        board2 = new SimpleBoard(25, 10);

        if (victoryPanel == null) {
            victoryPanel = new VictoryPanel();
            victoryPanel.setLayoutX(0);
            victoryPanel.setLayoutY(0);
            rootPane.getChildren().add(victoryPanel);
        }
        victoryPanel.setVisible(false);
        victoryPanel.getNewGameButton().setOnAction(e -> {
            playButtonSound();
            newGame();
        });
        victoryPanel.getHomeButton().setOnAction(e -> {
            playButtonSound();
            returnToHome();
        });

        pausePanel = new PausePanel();
        pausePanel.setVisible(false);
        pausePanel.setLayoutX(0);
        pausePanel.setLayoutY(0);
        rootPane.getChildren().add(pausePanel);
        pausePanel.getResumeButton().setOnAction(e -> {
            playButtonSound();
            togglePause();
        });
        pausePanel.getNewGameButton().setOnAction(e -> {
            playButtonSound();
            newGame();
        });
        pausePanel.getHomeButton().setOnAction(e -> {
            playButtonSound();
            returnToHome();
        });

        initializeGameMusic();
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
        board1.createNewBrick();
        initGameView(1, gamePanel1, brickPanel1, nextBrickPanel1, board1);
        bindScore(1, board1.getScore());

        board2.createNewBrick();
        initGameView(2, gamePanel2, brickPanel2, nextBrickPanel2, board2);
        bindScore(2, board2.getScore());

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
            displayMatrix1 = displayMatrix;
        } else {
            displayMatrix2 = displayMatrix;
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
            rectangles1 = rectangles;
        } else {
            rectangles2 = rectangles;
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
            ghostBrickPanel1 = ghostBrickPanel;
            ghostRectangles1 = ghostRectangles;
        } else {
            ghostBrickPanel2 = ghostBrickPanel;
            ghostRectangles2 = ghostRectangles;
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
            nextBrickRectangles1 = nextBrickRectangles;
        } else {
            nextBrickRectangles2 = nextBrickRectangles;
        }

        Timeline timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(player, new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();

        if (player == 1) {
            timeLine1 = timeLine;
        } else {
            timeLine2 = timeLine;
        }
    }

    private void moveBrickLeft(int player) {
        Board board = (player == 1) ? board1 : board2;
        board.moveBrickLeft();
        refreshBrick(player);
    }

    private void moveBrickRight(int player) {
        Board board = (player == 1) ? board1 : board2;
        board.moveBrickRight();
        refreshBrick(player);
    }

    private void rotateBrick(int player) {
        Board board = (player == 1) ? board1 : board2;
        board.rotateLeftBrick();
        refreshBrick(player);
    }

    private void moveDown(int player, MoveEvent event) {
        if (isPause.getValue() == Boolean.TRUE || isGameOver.getValue() == Boolean.TRUE) {
            return;
        }

        Board board = (player == 1) ? board1 : board2;
        boolean canMove = board.moveBrickDown();

        if (!canMove) {
            board.mergeBrickToBackground();
            ClearRow clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
                board.getScore().addLinesCleared(clearRow.getLinesRemoved());
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

        Board board = (player == 1) ? board1 : board2;
        board.hardDrop();
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();
        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
            board.getScore().addLinesCleared(clearRow.getLinesRemoved());
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
        Board board = (player == 1) ? board1 : board2;
        ViewData brick = board.getViewData();
        GridPane brickPanel = (player == 1) ? brickPanel1 : brickPanel2;
        Rectangle[][] rectangles = (player == 1) ? rectangles1 : rectangles2;

        double baseX = (player == 1) ? GameConstants.PLAYER1_BASE_X : GameConstants.PLAYER2_BASE_X;
        double baseY = (player == 1) ? GameConstants.PLAYER1_BASE_Y : GameConstants.PLAYER2_BASE_Y;

        brickPanel.setLayoutX(baseX + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * GameConstants.BRICK_SIZE);
        brickPanel.setLayoutY(-42 + baseY + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * GameConstants.BRICK_SIZE);

        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
            }
        }

        updateGhostBrick(player, brick);
        updateNextBrickPanel(player, brick.getNextBrickData(),
            (player == 1) ? nextBrickRectangles1 : nextBrickRectangles2);
    }

    private void updateGhostBrick(int player, ViewData brick) {
        GridPane ghostBrickPanel = (player == 1) ? ghostBrickPanel1 : ghostBrickPanel2;
        Rectangle[][] ghostRectangles = (player == 1) ? ghostRectangles1 : ghostRectangles2;

        double baseX = (player == 1) ? GameConstants.PLAYER1_BASE_X : GameConstants.PLAYER2_BASE_X;
        double baseY = (player == 1) ? GameConstants.PLAYER1_BASE_Y : GameConstants.PLAYER2_BASE_Y;

        ghostBrickPanel.setLayoutX(baseX + brick.getxPosition() * ghostBrickPanel.getVgap() + brick.getxPosition() * GameConstants.BRICK_SIZE);
        ghostBrickPanel.setLayoutY(-42 + baseY + brick.getGhostYPosition() * ghostBrickPanel.getHgap() + brick.getGhostYPosition() * GameConstants.BRICK_SIZE);

        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                if (brick.getBrickData()[i][j] != 0) {
                    Paint color = getFillColor(brick.getBrickData()[i][j]);
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
        Board board = (player == 1) ? board1 : board2;
        Rectangle[][] displayMatrix = (player == 1) ? displayMatrix1 : displayMatrix2;
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

    private double calculateSpeed(int level) {
        double baseSpeed = 400.0;
        double speedDecrease = 30.0;
        double minSpeed = 100.0;
        double speed = baseSpeed - ((level - 1) * speedDecrease);
        return Math.max(speed, minSpeed);
    }

    private void updateGameSpeed(int player, int level) {
        Timeline timeLine = (player == 1) ? timeLine1 : timeLine2;
        if (timeLine != null) {
            double newSpeed = calculateSpeed(level);
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
        if (timeLine1 != null) timeLine1.stop();
        if (timeLine2 != null) timeLine2.stop();
        stopGameMusic();

        int winner = (player == 1) ? 2 : 1;
        victoryPanel.setWinner(winner);
        victoryPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);

        // Play two player game over music
        playGameOverMusic();
    }

    public void newGame() {
        if (timeLine1 != null) timeLine1.stop();
        if (timeLine2 != null) timeLine2.stop();
        stopGameOverMusic();
        initializeGameMusic();

        victoryPanel.setVisible(false);
        pausePanel.setVisible(false);

        board1.newGame();
        board2.newGame();
        refreshGameBackground(1);
        refreshGameBackground(2);

        if (timeLine1 != null) timeLine1.play();
        if (timeLine2 != null) timeLine2.play();

        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        rootPane.requestFocus();
    }

    private void togglePause() {
        if (isGameOver.getValue() == Boolean.TRUE) {
            return;
        }

        if (isPause.getValue() == Boolean.FALSE) {
            if (timeLine1 != null) timeLine1.pause();
            if (timeLine2 != null) timeLine2.pause();
            isPause.setValue(Boolean.TRUE);
            pausePanel.setVisible(true);
        } else {
            if (timeLine1 != null) timeLine1.play();
            if (timeLine2 != null) timeLine2.play();
            isPause.setValue(Boolean.FALSE);
            pausePanel.setVisible(false);
        }
        rootPane.requestFocus();
    }

    public void setModeSwitch(Runnable modeSwitch) {
        this.modeSwitch = modeSwitch;
    }

    private void initializeGameMusic() {
        audioManager.playTwoPlayerMusic();
    }

    private void stopGameMusic() {
        audioManager.stopGameMusic();
    }

    private void playGameOverMusic() {
        audioManager.playTwoPlayerGameOverMusic();
    }

    private void stopGameOverMusic() {
        audioManager.stopGameOverMusic();
    }

    private void playButtonSound() {
        audioManager.playButtonSound();
    }

    private void returnToHome() {
        if (timeLine1 != null) timeLine1.stop();
        if (timeLine2 != null) timeLine2.stop();
        audioManager.stopAllAudio();

        if (modeSwitch != null) {
            modeSwitch.run();
        }
    }
}
