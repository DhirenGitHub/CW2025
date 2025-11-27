package com.comp2042;

/**
 * Utility class for calculating game speed based on difficulty level.
 * Implements the standard Tetris speed progression formula.
 */
public final class GameSpeedCalculator {

    // Speed configuration constants
    private static final double BASE_SPEED_MS = 400.0;      // Starting speed at level 1
    private static final double SPEED_DECREASE_MS = 30.0;   // Speed decrease per level
    private static final double MIN_SPEED_MS = 100.0;       // Fastest possible speed

    // Prevent instantiation
    private GameSpeedCalculator() {
        throw new AssertionError("Cannot instantiate GameSpeedCalculator");
    }

    /**
     * Calculate the delay in milliseconds based on level.
     * Formula: Base speed decreases as level increases
     * Level 1: 400ms, Level 2: 370ms, Level 3: 340ms, etc.
     * Minimum speed: 100ms (at level 11+)
     *
     * @param level The current game level (1-based)
     * @return The delay in milliseconds between automatic brick drops
     */
    public static double calculateSpeed(int level) {
        double speed = BASE_SPEED_MS - ((level - 1) * SPEED_DECREASE_MS);
        return Math.max(speed, MIN_SPEED_MS);
    }
}
