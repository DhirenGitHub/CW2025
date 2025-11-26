package com.comp2042;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Manages all audio for the game including background music and sound effects.
 * Handles menu music, gameplay music, game over music, and various sound effects.
 */
public class AudioManager {

    private MediaPlayer gameBackgroundMusic;
    private MediaPlayer stageClearSound;
    private MediaPlayer breakSound;
    private MediaPlayer gameOverMusic;
    private MediaPlayer buttonSound;

    /**
     * Plays the background music for single-player gameplay
     */
    public void playOnePlayerMusic() {
        try {
            stopGameMusic();
            String musicPath = getClass().getResource("/audio/one_player_bg.mp3").toExternalForm();
            Media media = new Media(musicPath);
            gameBackgroundMusic = new MediaPlayer(media);
            gameBackgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
            gameBackgroundMusic.setVolume(0.5);
            gameBackgroundMusic.play();
        } catch (Exception e) {
            System.err.println("Failed to load one player background music: " + e.getMessage());
        }
    }

    /**
     * Plays the background music for two-player gameplay
     */
    public void playTwoPlayerMusic() {
        try {
            stopGameMusic();
            String musicPath = getClass().getResource("/audio/two_player_bg.mp3").toExternalForm();
            Media media = new Media(musicPath);
            gameBackgroundMusic = new MediaPlayer(media);
            gameBackgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
            gameBackgroundMusic.setVolume(0.5);
            gameBackgroundMusic.play();
        } catch (Exception e) {
            System.err.println("Failed to load two player background music: " + e.getMessage());
        }
    }

    /**
     * Stops the currently playing game background music
     */
    public void stopGameMusic() {
        if (gameBackgroundMusic != null) {
            gameBackgroundMusic.stop();
        }
    }

    /**
     * Plays the stage clear sound effect when leveling up
     */
    public void playStageClearSound() {
        try {
            if (stageClearSound != null) {
                stageClearSound.stop();
            }
            String soundPath = getClass().getResource("/audio/stage_clear.mp3").toExternalForm();
            Media media = new Media(soundPath);
            stageClearSound = new MediaPlayer(media);
            stageClearSound.setVolume(0.7);
            stageClearSound.play();
        } catch (Exception e) {
            System.err.println("Failed to load stage clear sound: " + e.getMessage());
        }
    }

    /**
     * Plays the break sound effect when a line is cleared
     */
    public void playBreakSound() {
        try {
            if (breakSound != null) {
                breakSound.stop();
            }
            String soundPath = getClass().getResource("/audio/break.wav").toExternalForm();
            Media media = new Media(soundPath);
            breakSound = new MediaPlayer(media);
            breakSound.setVolume(0.6);
            breakSound.play();
        } catch (Exception e) {
            System.err.println("Failed to load break sound: " + e.getMessage());
        }
    }

    /**
     * Plays the appropriate game over music based on whether a new high score was achieved
     * @param isNewHighScore true if the player achieved a new high score
     */
    public void playGameOverMusic(boolean isNewHighScore) {
        try {
            stopGameOverMusic();
            String musicFile = isNewHighScore ? "highscore.mp3" : "one_player_gameover.mp3";
            String musicPath = getClass().getResource("/audio/" + musicFile).toExternalForm();
            Media media = new Media(musicPath);
            gameOverMusic = new MediaPlayer(media);
            gameOverMusic.setVolume(0.5);
            gameOverMusic.play();
        } catch (Exception e) {
            System.err.println("Failed to load game over music: " + e.getMessage());
        }
    }

    /**
     * Plays the two-player game over music
     */
    public void playTwoPlayerGameOverMusic() {
        try {
            stopGameOverMusic();
            String musicPath = getClass().getResource("/audio/two_player_gameover.mp3").toExternalForm();
            Media media = new Media(musicPath);
            gameOverMusic = new MediaPlayer(media);
            gameOverMusic.setVolume(0.5);
            gameOverMusic.play();
        } catch (Exception e) {
            System.err.println("Failed to load two player game over music: " + e.getMessage());
        }
    }

    /**
     * Stops the currently playing game over music
     */
    public void stopGameOverMusic() {
        if (gameOverMusic != null) {
            gameOverMusic.stop();
        }
    }

    /**
     * Plays the button click sound effect
     */
    public void playButtonSound() {
        try {
            if (buttonSound != null) {
                buttonSound.stop();
            }
            String soundPath = getClass().getResource("/audio/button.wav").toExternalForm();
            Media media = new Media(soundPath);
            buttonSound = new MediaPlayer(media);
            buttonSound.setVolume(0.5);
            buttonSound.play();
        } catch (Exception e) {
            System.err.println("Failed to load button sound: " + e.getMessage());
        }
    }

    /**
     * Stops all audio (game music and game over music)
     */
    public void stopAllAudio() {
        stopGameMusic();
        stopGameOverMusic();
    }
}
