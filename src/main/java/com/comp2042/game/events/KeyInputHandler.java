package com.comp2042.game.events;
import com.comp2042.ui.controllers.GuiController;

import javafx.beans.property.BooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Handles keyboard input for the game.
 * Processes player controls (movement, rotation, drop) and game controls (pause, new game).
 */
public class KeyInputHandler implements EventHandler<KeyEvent> {

    private final BooleanProperty isPause;
    private final BooleanProperty isGameOver;
    private final GuiController guiController;
    private final InputEventListener eventListener;
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    public KeyInputHandler(BooleanProperty isPause,
                          BooleanProperty isGameOver,
                          GuiController guiController,
                          InputEventListener eventListener) {
        this.isPause = isPause;
        this.isGameOver = isGameOver;
        this.guiController = guiController;
        this.eventListener = eventListener;
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();

        // Track key releases to allow re-triggering
        if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED) {
            pressedKeys.remove(code);
            return;
        }

        // Ignore auto-repeat events (when key is held down)
        if (pressedKeys.contains(code)) {
            return;
        }

        // Mark this key as pressed
        pressedKeys.add(code);

        // Game controls (movement, rotation, drop) - only when game is active
        if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
            handleGameplayInput(keyEvent);
        }

        // Menu controls (work anytime)
        handleMenuInput(keyEvent);
    }

    /**
     * Handles gameplay inputs (movement, rotation, drops)
     */
    private void handleGameplayInput(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();

        if (code == KeyCode.LEFT || code == KeyCode.A) {
            guiController.refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
            keyEvent.consume();
        } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
            guiController.refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
            keyEvent.consume();
        } else if (code == KeyCode.UP || code == KeyCode.W) {
            guiController.refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
            keyEvent.consume();
        } else if (code == KeyCode.DOWN || code == KeyCode.S) {
            guiController.moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
            keyEvent.consume();
        } else if (code == KeyCode.SPACE) {
            guiController.hardDrop();
            keyEvent.consume();
        }
    }

    /**
     * Handles menu and control inputs (pause, new game)
     */
    private void handleMenuInput(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();

        if (code == KeyCode.N) {
            guiController.newGame(null);
        } else if (code == KeyCode.ESCAPE) {
            guiController.togglePause();
            keyEvent.consume();
        }
    }
}
