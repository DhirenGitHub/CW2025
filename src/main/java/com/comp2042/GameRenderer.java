package com.comp2042;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * Handles all rendering operations for the game including bricks, boards, and ghost pieces.
 */
public class GameRenderer {

    private final GridPane gamePanel;
    private final GridPane brickPanel;
    private final GridPane nextBrickPanel;
    private final Pane rootPane;

    private Rectangle[][] displayMatrix;
    private Rectangle[][] rectangles;
    private Rectangle[][] nextBrickRectangles;
    private GridPane ghostBrickPanel;
    private Rectangle[][] ghostRectangles;

    public GameRenderer(GridPane gamePanel, GridPane brickPanel, GridPane nextBrickPanel, Pane rootPane) {
        this.gamePanel = gamePanel;
        this.brickPanel = brickPanel;
        this.nextBrickPanel = nextBrickPanel;
        this.rootPane = rootPane;
    }

    /**
     * Initializes the game view with board matrix and initial brick
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        initDisplayMatrix(boardMatrix);
        initBrickRectangles(brick);
        initGhostBrickPanel(brick);
        initNextBrickPanel(brick.getNextBrickData());
    }

    /**
     * Initializes the display matrix for the game board
     */
    private void initDisplayMatrix(int[][] boardMatrix) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(GameConstants.BRICK_SIZE, GameConstants.BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }
    }

    /**
     * Initializes the brick rectangles for the current piece
     */
    private void initBrickRectangles(ViewData brick) {
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(GameConstants.BRICK_SIZE, GameConstants.BRICK_SIZE);
                rectangle.setFill(BrickColorManager.getColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.setLayoutX(GameConstants.GAME_BASE_X + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * GameConstants.BRICK_SIZE);
        brickPanel.setLayoutY(-42 + GameConstants.GAME_BASE_Y + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * GameConstants.BRICK_SIZE);
    }

    /**
     * Initializes the ghost brick panel
     */
    private void initGhostBrickPanel(ViewData brick) {
        ghostBrickPanel = new GridPane();
        ghostBrickPanel.setHgap(1);
        ghostBrickPanel.setVgap(1);
        rootPane.getChildren().add(0, ghostBrickPanel); // Add at index 0 so it's behind other elements

        ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
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
    }

    /**
     * Initializes the next brick preview panel
     */
    private void initNextBrickPanel(int[][] nextBrickData) {
        // Create a 4x4 grid for the next brick preview
        nextBrickRectangles = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rectangle = new Rectangle(GameConstants.BRICK_SIZE, GameConstants.BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                nextBrickRectangles[i][j] = rectangle;
                nextBrickPanel.add(rectangle, j, i);
            }
        }
        updateNextBrickPanel(nextBrickData);
    }

    /**
     * Updates the next brick preview panel with new brick data
     */
    public void updateNextBrickPanel(int[][] nextBrickData) {
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
                                rect.setFill(BrickColorManager.getColor(nextBrickData[i][j]));
                                rect.setArcHeight(9);
                                rect.setArcWidth(9);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Refreshes the brick display with new position and data
     */
    public void refreshBrick(ViewData brick) {
        brickPanel.setLayoutX(GameConstants.GAME_BASE_X + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * GameConstants.BRICK_SIZE);
        brickPanel.setLayoutY(-42 + GameConstants.GAME_BASE_Y + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * GameConstants.BRICK_SIZE);
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

    /**
     * Updates the ghost brick position and appearance
     */
    private void updateGhostBrick(ViewData brick) {
        // Position ghost brick at the landing position (same X as brickPanel, Y at ghost position)
        ghostBrickPanel.setLayoutX(GameConstants.GAME_BASE_X + brick.getxPosition() * ghostBrickPanel.getVgap() + brick.getxPosition() * GameConstants.BRICK_SIZE);
        ghostBrickPanel.setLayoutY(-42 + GameConstants.GAME_BASE_Y + brick.getGhostYPosition() * ghostBrickPanel.getHgap() + brick.getGhostYPosition() * GameConstants.BRICK_SIZE);

        // Update ghost brick appearance - semi-transparent version of the brick
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                if (brick.getBrickData()[i][j] != 0) {
                    Paint color = BrickColorManager.getColor(brick.getBrickData()[i][j]);
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

    /**
     * Refreshes the game background board
     */
    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    /**
     * Sets rectangle color and styling
     */
    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(BrickColorManager.getColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }
}
