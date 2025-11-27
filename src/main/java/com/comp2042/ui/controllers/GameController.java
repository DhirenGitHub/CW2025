package com.comp2042.ui.controllers;

import com.comp2042.game.events.*;
import com.comp2042.game.models.*;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10);

    private final GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        viewGuiController.setEventListener(this);
        viewGuiController.setGameController(this);
    }

    public void initializeGame() {
        board.createNewBrick();
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.bindLinesCleared(board.getScore().linesClearedProperty());
        viewGuiController.bindLevel(board.getScore().levelProperty());
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            clearRow = board.landBrickAndClearRows();
            if (board.createNewBrick()) {
                viewGuiController.gameOver(board.getScore().scoreProperty().get());
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());

        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    public DownData onHardDropEvent() {
        // Perform hard drop
        board.hardDrop();
        // Immediately trigger the merge and check for game over
        ClearRow clearRow = board.landBrickAndClearRows();
        if (board.createNewBrick()) {
            viewGuiController.gameOver(board.getScore().scoreProperty().get());
        }
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        viewGuiController.refreshBrick(board.getViewData());
    }
}
