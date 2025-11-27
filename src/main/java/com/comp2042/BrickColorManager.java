package com.comp2042;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Centralized color management for Tetris bricks.
 * Maps brick type identifiers to their corresponding colors.
 * This eliminates code duplication across multiple renderer classes.
 */
public class BrickColorManager {

    private BrickColorManager() {
        // Private constructor to prevent instantiation
    }

    /**
     * Gets the fill color for a brick type.
     *
     * @param brickType The brick type identifier (0-7)
     * @return The Paint color corresponding to the brick type
     */
    public static Paint getColor(int brickType) {
        switch (brickType) {
            case 0: return Color.TRANSPARENT;
            case 1: return Color.AQUA;
            case 2: return Color.BLUEVIOLET;
            case 3: return Color.DARKGREEN;
            case 4: return Color.YELLOW;
            case 5: return Color.RED;
            case 6: return Color.BEIGE;
            case 7: return Color.BURLYWOOD;
            default: return Color.WHITE;
        }
    }
}
