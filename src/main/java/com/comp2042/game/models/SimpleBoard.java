package com.comp2042.game.models;

import com.comp2042.game.events.ClearRow;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.utils.BrickRotator;
import com.comp2042.utils.MatrixOperations;
import com.comp2042.utils.NextShapeInfo;

import java.awt.Point;

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }


    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();

        // Try rotation at current position first
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(),
                                                     (int) currentOffset.getX(), (int) currentOffset.getY());
        if (!conflict) {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }

        // Wall kick: Try shifting down (for pieces that overflow at top)
        Point downKick = new Point(currentOffset);
        downKick.translate(0, 1);
        conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(),
                                             (int) downKick.getX(), (int) downKick.getY());
        if (!conflict) {
            currentOffset = downKick;
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }

        // Wall kick: Try shifting down 2 units (for I-piece at top)
        Point downKick2 = new Point(currentOffset);
        downKick2.translate(0, 2);
        conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(),
                                             (int) downKick2.getX(), (int) downKick2.getY());
        if (!conflict) {
            currentOffset = downKick2;
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }

        // Wall kick: Try shifting left
        Point leftKick = new Point(currentOffset);
        leftKick.translate(-1, 0);
        conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(),
                                             (int) leftKick.getX(), (int) leftKick.getY());
        if (!conflict) {
            currentOffset = leftKick;
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }

        // Wall kick: Try shifting right
        Point rightKick = new Point(currentOffset);
        rightKick.translate(1, 0);
        conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(),
                                             (int) rightKick.getX(), (int) rightKick.getY());
        if (!conflict) {
            currentOffset = rightKick;
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }

        // Wall kick: Try shifting two units left (for I-piece)
        Point leftKick2 = new Point(currentOffset);
        leftKick2.translate(-2, 0);
        conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(),
                                             (int) leftKick2.getX(), (int) leftKick2.getY());
        if (!conflict) {
            currentOffset = leftKick2;
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }

        // Wall kick: Try shifting two units right (for I-piece)
        Point rightKick2 = new Point(currentOffset);
        rightKick2.translate(2, 0);
        conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(),
                                             (int) rightKick2.getX(), (int) rightKick2.getY());
        if (!conflict) {
            currentOffset = rightKick2;
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }

        // All kick attempts failed
        return false;
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(4, 1); // starting position fixed to top instead of earlier middle
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(),
                           brickGenerator.getNextBrick().getShapeMatrix().get(0), getGhostYPosition());
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    @Override
    public Score getScore() {
        return score;
    }


    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        createNewBrick();
    }

    @Override
    public int getGhostYPosition() {
        // Calculate where the brick would land if dropped straight down
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point ghostPosition = new Point(currentOffset);

        // Keep moving down until we hit a conflict
        while (true) {
            Point nextPosition = new Point(ghostPosition);
            nextPosition.translate(0, 1);
            boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(),
                                                         (int) nextPosition.getX(), (int) nextPosition.getY());
            if (conflict) {
                break;
            }
            ghostPosition = nextPosition;
        }

        return (int) ghostPosition.getY();
    }

    @Override
    public void hardDrop() {
        // Move brick to ghost position instantly
        currentOffset.y = getGhostYPosition();
        // Award +30 points for hard drop
        score.add(30);
    }

    @Override
    public ClearRow landBrickAndClearRows() {
        // Merge the brick to the background
        mergeBrickToBackground();

        // Clear any completed rows
        ClearRow clearRow = clearRows();

        // Update score if rows were cleared
        if (clearRow.getLinesRemoved() > 0) {
            score.add(clearRow.getScoreBonus());
            score.addLinesCleared(clearRow.getLinesRemoved());
        }

        return clearRow;
    }
}
