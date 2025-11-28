package com.comp2042.ui.renderers;

import com.comp2042.game.models.ViewData;
import com.comp2042.utils.BrickColorManager;
import com.comp2042.utils.GameConstants;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import java.util.List;

/**
 * Handles all rendering operations for two-player mode.
 * Encapsulates the logic for initializing and updating game visuals including
 * brick panels, ghost bricks, next brick previews, and game backgrounds.
 */
public class TwoPlayerRenderer {

    private final Pane rootPane;
    private final GridPane brickPanel1;
    private final GridPane brickPanel2;

    public TwoPlayerRenderer(Pane rootPane, GridPane brickPanel1, GridPane brickPanel2) {
        this.rootPane = rootPane;
        this.brickPanel1 = brickPanel1;
        this.brickPanel2 = brickPanel2;
    }

    /**
     * Initializes the display matrix for the game board
     */
    public Rectangle[][] initializeDisplayMatrix(GridPane gamePanel, int[][] boardMatrix) {
        Rectangle[][] displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(GameConstants.BRICK_SIZE, GameConstants.BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }
        return displayMatrix;
    }

    /**
     * Initializes the brick panel rectangles
     */
    public Rectangle[][] initializeBrickPanel(GridPane brickPanel, ViewData brick, double baseX, double baseY) {
        Rectangle[][] rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(GameConstants.BRICK_SIZE, GameConstants.BRICK_SIZE);
                rectangle.setFill(BrickColorManager.getColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }

        // Calculate cell dimensions (brick size + gap)
        double cellWidth = brickPanel.getHgap() + GameConstants.BRICK_SIZE;
        double cellHeight = brickPanel.getVgap() + GameConstants.BRICK_SIZE;

        // Calculate offset for hidden rows at the top
        double hiddenRowsOffset = GameConstants.HIDDEN_ROWS * cellHeight;

        brickPanel.setLayoutX(baseX + brick.getxPosition() * cellWidth);
        brickPanel.setLayoutY(baseY - hiddenRowsOffset + brick.getyPosition() * cellHeight);

        return rectangles;
    }

    /**
     * Initializes the ghost brick panel
     */
    public GridPane initializeGhostBrickPanel(ViewData brick) {
        GridPane ghostBrickPanel = new GridPane();
        ghostBrickPanel.setHgap(1);
        ghostBrickPanel.setVgap(1);
        rootPane.getChildren().add(0, ghostBrickPanel);
        return ghostBrickPanel;
    }

    /**
     * Initializes ghost brick rectangles
     */
    public Rectangle[][] initializeGhostRectangles(GridPane ghostBrickPanel, ViewData brick) {
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
        return ghostRectangles;
    }

    /**
     * Initializes next brick panel rectangles
     */
    public Rectangle[][] initializeNextBrickPanel(GridPane nextBrickPanel, int[][] nextBrickData) {
        Rectangle[][] nextBrickRectangles = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rectangle = new Rectangle(GameConstants.BRICK_SIZE, GameConstants.BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                nextBrickRectangles[i][j] = rectangle;
                nextBrickPanel.add(rectangle, j, i);
            }
        }
        updateNextBrickPanel(nextBrickData, nextBrickRectangles);
        return nextBrickRectangles;
    }

    /**
     * Refreshes the brick position and appearance
     */
    public void refreshBrick(int player, ViewData brick, Rectangle[][] rectangles, double baseX, double baseY) {
        GridPane brickPanel = (player == 1) ? brickPanel1 : brickPanel2;

        // Calculate cell dimensions (brick size + gap)
        double cellWidth = brickPanel.getHgap() + GameConstants.BRICK_SIZE;
        double cellHeight = brickPanel.getVgap() + GameConstants.BRICK_SIZE;

        // Calculate offset for hidden rows at the top
        double hiddenRowsOffset = GameConstants.HIDDEN_ROWS * cellHeight;

        int xPos = brick.getxPosition();
        int yPos = brick.getyPosition();
        brickPanel.setLayoutX(baseX + xPos * cellWidth);
        brickPanel.setLayoutY(baseY - hiddenRowsOffset + yPos * cellHeight);

        int[][] brickData = brick.getBrickData();
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                setRectangleData(brickData[i][j], rectangles[i][j]);
            }
        }
    }

    /**
     * Updates the ghost brick position and appearance
     */
    public void updateGhostBrick(ViewData brick, GridPane ghostBrickPanel, Rectangle[][] ghostRectangles, GridPane brickPanel) {
        // Calculate cell dimensions (brick size + gap)
        double cellHeight = ghostBrickPanel.getVgap() + GameConstants.BRICK_SIZE;

        // Position ghost brick relative to current brick position
        ghostBrickPanel.setLayoutX(brickPanel.getLayoutX());
        ghostBrickPanel.setLayoutY(brickPanel.getLayoutY() + (brick.getGhostYPosition() - brick.getyPosition()) * cellHeight);

        int[][] brickData = brick.getBrickData();
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                int cellValue = brickData[i][j];
                if (cellValue != 0) {
                    Paint color = BrickColorManager.getColor(cellValue);
                    // Show only outline with no fill for ghost effect
                    ghostRectangles[i][j].setFill(Color.TRANSPARENT);
                    if (color instanceof Color solidColor) {
                        // Use a semi-transparent version of the brick color for the outline
                        Color outlineColor = new Color(solidColor.getRed(), solidColor.getGreen(), solidColor.getBlue(), 0.6);
                        ghostRectangles[i][j].setStroke(outlineColor);
                        ghostRectangles[i][j].setStrokeWidth(2);
                        ghostRectangles[i][j].setStrokeType(StrokeType.INSIDE);
                    }
                } else {
                    ghostRectangles[i][j].setFill(Color.TRANSPARENT);
                    ghostRectangles[i][j].setStroke(null);
                }
            }
        }
    }

    /**
     * Updates the next brick preview panel
     */
    public void updateNextBrickPanel(int[][] nextBrickData, Rectangle[][] nextBrickRectangles) {
        // Clear all rectangles first
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                nextBrickRectangles[i][j].setFill(Color.TRANSPARENT);
                nextBrickRectangles[i][j].setArcHeight(0);
                nextBrickRectangles[i][j].setArcWidth(0);
                nextBrickRectangles[i][j].setStroke(null);
            }
        }

        if (nextBrickData != null && nextBrickData.length > 0) {
            int minRow = nextBrickData.length, maxRow = -1;
            int minCol = nextBrickData[0].length, maxCol = -1;

            // Find bounds of the brick
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

                // Center the brick in the 4x4 grid
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
                                // Add retro shadow effect to next brick preview
                                rect.setStroke(BrickColorManager.getShadowColor(nextBrickData[i][j]));
                                rect.setStrokeWidth(2);
                                rect.setStrokeType(StrokeType.INSIDE);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Refreshes the game background
     */
    public void refreshGameBackground(Rectangle[][] displayMatrix, int[][] boardMatrix) {
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                setRectangleData(boardMatrix[i][j], displayMatrix[i][j]);
            }
        }
    }

    /**
     * Animates row clearing with a flash effect for a specific player
     */
    public void animateRowClear(int player, Rectangle[][] displayMatrix, List<Integer> rowIndices, Runnable onFinished) {
        if (rowIndices == null || rowIndices.isEmpty()) {
            if (onFinished != null) {
                onFinished.run();
            }
            return;
        }

        ParallelTransition flashAnimation = new ParallelTransition();

        // Flash each cleared row white
        for (int rowIndex : rowIndices) {
            // Adjust for hidden rows (rows 0-1 are hidden)
            if (rowIndex >= 2 && rowIndex < displayMatrix.length) {
                for (Rectangle rect : displayMatrix[rowIndex]) {
                    Paint originalColor = rect.getFill();

                    // Create flash animation: white -> original -> white -> fade out
                    SequentialTransition rowAnimation = new SequentialTransition();

                    // Flash to white
                    FadeTransition flash1 = new FadeTransition(Duration.millis(80), rect);
                    flash1.setOnFinished(e -> rect.setFill(Color.WHITE));

                    // Flash back
                    FadeTransition flash2 = new FadeTransition(Duration.millis(80), rect);
                    flash2.setOnFinished(e -> rect.setFill(originalColor));

                    // Flash to white again
                    FadeTransition flash3 = new FadeTransition(Duration.millis(80), rect);
                    flash3.setOnFinished(e -> rect.setFill(Color.WHITE));

                    rowAnimation.getChildren().addAll(flash1, flash2, flash3);
                    flashAnimation.getChildren().add(rowAnimation);
                }
            }
        }

        flashAnimation.setOnFinished(e -> {
            if (onFinished != null) {
                onFinished.run();
            }
        });

        flashAnimation.play();
    }

    /**
     * Sets rectangle color and styling with retro shadow effect
     */
    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(BrickColorManager.getColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);

        // Add retro-style shadow border (darker outline)
        if (color != 0) {
            rectangle.setStroke(BrickColorManager.getShadowColor(color));
            rectangle.setStrokeWidth(2);
            rectangle.setStrokeType(StrokeType.INSIDE);
        } else {
            rectangle.setStroke(null);
        }
    }
}
