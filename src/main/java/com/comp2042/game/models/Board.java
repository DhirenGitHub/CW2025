package com.comp2042.game.models;
import com.comp2042.game.models.Board;
import com.comp2042.game.events.ClearRow;
import com.comp2042.game.models.ViewData;

public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();

    void newGame();

    int getGhostYPosition();

    void hardDrop();

    /**
     * Lands the current brick and clears any completed rows.
     * This encapsulates the common operation of merging a brick to the background
     * and clearing rows, updating the score accordingly.
     *
     * @return ClearRow object containing information about cleared rows and score bonus
     */
    ClearRow landBrickAndClearRows();
}
