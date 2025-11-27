package com.comp2042;

import javafx.animation.Timeline;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

/**
 * Encapsulates all game state for a single player in two-player mode.
 * This class groups together all the components needed to manage one player's game board,
 * including the board logic, UI panels, rendering matrices, and timeline.
 */
public class PlayerGameState {
    private final int playerNumber;
    private final Board board;
    private final GridPane gamePanel;
    private final GridPane brickPanel;
    private final GridPane nextBrickPanel;

    private Rectangle[][] displayMatrix;
    private Rectangle[][] rectangles;
    private Rectangle[][] nextBrickRectangles;
    private GridPane ghostBrickPanel;
    private Rectangle[][] ghostRectangles;
    private Timeline timeline;

    /**
     * Creates a new player game state.
     *
     * @param playerNumber The player number (1 or 2)
     * @param board The game board for this player
     * @param gamePanel The grid panel displaying the game board
     * @param brickPanel The grid panel displaying the current brick
     * @param nextBrickPanel The grid panel displaying the next brick preview
     */
    public PlayerGameState(int playerNumber, Board board, GridPane gamePanel,
                          GridPane brickPanel, GridPane nextBrickPanel) {
        this.playerNumber = playerNumber;
        this.board = board;
        this.gamePanel = gamePanel;
        this.brickPanel = brickPanel;
        this.nextBrickPanel = nextBrickPanel;
    }

    // Getters
    public int getPlayerNumber() { return playerNumber; }
    public Board getBoard() { return board; }
    public GridPane getGamePanel() { return gamePanel; }
    public GridPane getBrickPanel() { return brickPanel; }
    public GridPane getNextBrickPanel() { return nextBrickPanel; }
    public Rectangle[][] getDisplayMatrix() { return displayMatrix; }
    public Rectangle[][] getRectangles() { return rectangles; }
    public Rectangle[][] getNextBrickRectangles() { return nextBrickRectangles; }
    public GridPane getGhostBrickPanel() { return ghostBrickPanel; }
    public Rectangle[][] getGhostRectangles() { return ghostRectangles; }
    public Timeline getTimeline() { return timeline; }

    // Setters
    public void setDisplayMatrix(Rectangle[][] displayMatrix) {
        this.displayMatrix = displayMatrix;
    }

    public void setRectangles(Rectangle[][] rectangles) {
        this.rectangles = rectangles;
    }

    public void setNextBrickRectangles(Rectangle[][] nextBrickRectangles) {
        this.nextBrickRectangles = nextBrickRectangles;
    }

    public void setGhostBrickPanel(GridPane ghostBrickPanel) {
        this.ghostBrickPanel = ghostBrickPanel;
    }

    public void setGhostRectangles(Rectangle[][] ghostRectangles) {
        this.ghostRectangles = ghostRectangles;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    /**
     * Gets the base X coordinate for this player's board based on game constants.
     */
    public double getBaseX() {
        return (playerNumber == 1) ? GameConstants.PLAYER1_BASE_X : GameConstants.PLAYER2_BASE_X;
    }

    /**
     * Gets the base Y coordinate for this player's board based on game constants.
     */
    public double getBaseY() {
        return (playerNumber == 1) ? GameConstants.PLAYER1_BASE_Y : GameConstants.PLAYER2_BASE_Y;
    }

    /**
     * Stops the timeline if it's running.
     */
    public void stopTimeline() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    /**
     * Pauses the timeline if it's running.
     */
    public void pauseTimeline() {
        if (timeline != null) {
            timeline.pause();
        }
    }

    /**
     * Plays/resumes the timeline if it exists.
     */
    public void playTimeline() {
        if (timeline != null) {
            timeline.play();
        }
    }
}
