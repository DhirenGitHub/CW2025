package com.comp2042;

import java.io.*;

/**
 * Manages saving and loading high scores from a file
 */
public class HighScoreManager {

    private int highScore;

    public HighScoreManager() {
        loadHighScore();
    }

    /**
     * Loads the high score from file
     */
    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(GameConstants.HIGH_SCORE_FILE))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                highScore = Integer.parseInt(line.trim());
            } else {
                highScore = 0;
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, start with 0
            highScore = 0;
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading high score: " + e.getMessage());
            highScore = 0;
        }
    }

    /**
     * Saves the high score to file
     */
    public void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GameConstants.HIGH_SCORE_FILE))) {
            writer.write(String.valueOf(highScore));
        } catch (IOException e) {
            System.err.println("Error saving high score: " + e.getMessage());
        }
    }

    /**
     * Gets the current high score
     * @return The high score
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * Checks if the given score beats the high score and updates if so
     * @param score The score to check
     * @return true if a new high score was set, false otherwise
     */
    public boolean checkAndUpdateHighScore(int score) {
        System.out.println("HighScoreManager - Checking score: " + score + " against highScore: " + highScore);
        if (score > highScore) {
            System.out.println("NEW HIGH SCORE! Updating from " + highScore + " to " + score);
            highScore = score;
            saveHighScore();
            return true;
        }
        System.out.println("Not a new high score");
        return false;
    }

    /**
     * Resets the high score to 0
     */
    public void resetHighScore() {
        highScore = 0;
        saveHighScore();
    }
}
