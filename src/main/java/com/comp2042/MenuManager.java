package com.comp2042;

import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Manages all menu-related operations including start menu, pause menu, controls, and game over screen.
 */
public class MenuManager {

    private final StartMenuPanel startMenuPanel;
    private final PausePanel pausePanel;
    private final ControlsPanel controlsPanel;
    private final GameOverPanel gameOverPanel;

    private final GridPane brickPanel;
    private final GridPane nextBrickPanel;
    private final Text scoreText;
    private final VBox sidebarContainer;
    private final BorderPane gameBoard;

    private GridPane ghostBrickPanel;
    private Timeline timeLine;

    public MenuManager(StartMenuPanel startMenuPanel, PausePanel pausePanel,
                      ControlsPanel controlsPanel, GameOverPanel gameOverPanel,
                      GridPane brickPanel, GridPane nextBrickPanel,
                      Text scoreText, VBox sidebarContainer,
                      BorderPane gameBoard) {
        this.startMenuPanel = startMenuPanel;
        this.pausePanel = pausePanel;
        this.controlsPanel = controlsPanel;
        this.gameOverPanel = gameOverPanel;
        this.brickPanel = brickPanel;
        this.nextBrickPanel = nextBrickPanel;
        this.scoreText = scoreText;
        this.sidebarContainer = sidebarContainer;
        this.gameBoard = gameBoard;
    }

    /**
     * Sets the ghost brick panel reference
     */
    public void setGhostBrickPanel(GridPane ghostBrickPanel) {
        this.ghostBrickPanel = ghostBrickPanel;
    }

    /**
     * Sets the timeline reference
     */
    public void setTimeline(Timeline timeLine) {
        this.timeLine = timeLine;
    }

    /**
     * Shows the start menu and hides game elements
     */
    public void showStartMenu() {
        startMenuPanel.setVisible(true);
        startMenuPanel.resumeAnimation();
        gameOverPanel.setVisible(false);
        pausePanel.setVisible(false);

        // Hide game elements
        if (brickPanel != null) {
            brickPanel.setVisible(false);
        }
        if (nextBrickPanel != null) {
            nextBrickPanel.setVisible(false);
        }
        if (scoreText != null) {
            scoreText.setVisible(false);
        }
        if (ghostBrickPanel != null) {
            ghostBrickPanel.setVisible(false);
        }
        if (sidebarContainer != null) {
            sidebarContainer.setVisible(false);
        }
        if (gameBoard != null) {
            gameBoard.setVisible(false);
        }
        if (timeLine != null) {
            timeLine.stop();
        }
    }

    /**
     * Shows the controls panel
     */
    public void showControls() {
        controlsPanel.setVisible(true);
        startMenuPanel.setVisible(false);
    }

    /**
     * Hides the controls panel
     */
    public void hideControls() {
        controlsPanel.setVisible(false);
        startMenuPanel.setVisible(true);
    }

    /**
     * Shows game elements and hides menus (called when starting/resuming game)
     */
    public void showGameElements(BooleanProperty isPause, BooleanProperty isGameOver, GridPane gamePanel) {
        startMenuPanel.stopMusic();
        startMenuPanel.setVisible(false);

        if (brickPanel != null) {
            brickPanel.setVisible(true);
        }
        if (nextBrickPanel != null) {
            nextBrickPanel.setVisible(true);
        }
        if (scoreText != null) {
            scoreText.setVisible(true);
        }
        if (ghostBrickPanel != null) {
            ghostBrickPanel.setVisible(true);
        }
        if (sidebarContainer != null) {
            sidebarContainer.setVisible(true);
        }
        if (gameBoard != null) {
            gameBoard.setVisible(true);
        }

        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);

        if (timeLine != null) {
            timeLine.play();
        }
        gamePanel.requestFocus();
    }

    /**
     * Shows the pause menu
     */
    public void showPauseMenu() {
        pausePanel.setVisible(true);
        if (timeLine != null) {
            timeLine.pause();
        }
    }

    /**
     * Hides the pause menu
     */
    public void hidePauseMenu() {
        pausePanel.setVisible(false);
        if (timeLine != null) {
            timeLine.play();
        }
    }

    /**
     * Shows the game over panel
     */
    public void showGameOver() {
        gameOverPanel.setVisible(true);
        if (timeLine != null) {
            timeLine.stop();
        }
    }

    /**
     * Returns to home menu (stops game and shows start menu)
     */
    public void returnToHome(BooleanProperty isPause, AudioManager audioManager) {
        if (timeLine != null) {
            timeLine.stop();
        }
        audioManager.stopAllAudio();
        isPause.setValue(Boolean.FALSE);
        pausePanel.setVisible(false);
        showStartMenu();
    }

    /**
     * Stops the timeline
     */
    public void stopTimeline() {
        if (timeLine != null) {
            timeLine.stop();
        }
    }

    /**
     * Gets the start menu panel
     */
    public StartMenuPanel getStartMenuPanel() {
        return startMenuPanel;
    }

    /**
     * Gets the pause panel
     */
    public PausePanel getPausePanel() {
        return pausePanel;
    }

    /**
     * Gets the controls panel
     */
    public ControlsPanel getControlsPanel() {
        return controlsPanel;
    }

    /**
     * Gets the game over panel
     */
    public GameOverPanel getGameOverPanel() {
        return gameOverPanel;
    }
}
