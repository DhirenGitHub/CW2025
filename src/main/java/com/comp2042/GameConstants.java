package com.comp2042;

import javafx.scene.paint.Color;

/**
 * Central location for all game constants.
 * Contains UI dimensions, file paths, and Tetromino definitions.
 */
public final class GameConstants {

    // Prevent instantiation
    private GameConstants() {
        throw new AssertionError("Cannot instantiate GameConstants");
    }

    // ===== UI Dimensions =====

    /** Size of each brick/block in pixels */
    public static final int BRICK_SIZE = 20;

    // Single Player Layout
    /** X coordinate base position for single player game board */
    public static final double GAME_BASE_X = 237.0;  // 225 (gameBoard) + 12 (border)

    /** Y coordinate base position for single player game board */
    public static final double GAME_BASE_Y = 65.0;   // 45 (gameBoard) + 12 (border)

    // Two Player Layout
    /** X coordinate base position for player 1 game board */
    public static final double PLAYER1_BASE_X = 32.0;  // 20 (BorderPane) + 12 (border)

    /** Y coordinate base position for player 1 game board */
    public static final double PLAYER1_BASE_Y = 65.0;  // 45 (BorderPane) + 12 (border)

    /** X coordinate base position for player 2 game board */
    public static final double PLAYER2_BASE_X = 568.0; // 556 (BorderPane) + 12 (border)

    /** Y coordinate base position for player 2 game board */
    public static final double PLAYER2_BASE_Y = 65.0;  // 45 (BorderPane) + 12 (border)

    // ===== File Paths =====

    /** Filename for storing high score data */
    public static final String HIGH_SCORE_FILE = "highscore.txt";

    // ===== Tetromino Definitions =====

    /** Tetromino shapes (I, O, T, S, Z, J, L) */
    public static final int[][][] TETROMINOES = {
        // I piece
        {{1, 1, 1, 1}},
        // O piece
        {{1, 1}, {1, 1}},
        // T piece
        {{0, 1, 0}, {1, 1, 1}},
        // S piece
        {{0, 1, 1}, {1, 1, 0}},
        // Z piece
        {{1, 1, 0}, {0, 1, 1}},
        // J piece
        {{1, 0, 0}, {1, 1, 1}},
        // L piece
        {{0, 0, 1}, {1, 1, 1}}
    };

    /** Colors for each tetromino type */
    public static final Color[] TETROMINO_COLORS = {
        Color.CYAN,      // I
        Color.YELLOW,    // O
        Color.PURPLE,    // T
        Color.GREEN,     // S
        Color.RED,       // Z
        Color.BLUE,      // J
        Color.ORANGE     // L
    };
}
