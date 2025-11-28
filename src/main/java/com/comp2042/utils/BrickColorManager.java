package com.comp2042.utils;

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
            case 1: return Color.rgb(0, 204, 204);      // Cyan/Aqua - I brick
            case 2: return Color.rgb(153, 51, 255);     // Purple - T brick
            case 3: return Color.rgb(0, 153, 0);        // Green - S brick
            case 4: return Color.rgb(255, 204, 0);      // Yellow - O brick
            case 5: return Color.rgb(255, 51, 102);     // Pink/Magenta - Z brick
            case 6: return Color.rgb(255, 153, 51);     // Orange - L brick
            case 7: return Color.rgb(51, 102, 255);     // Blue - J brick
            default: return Color.WHITE;
        }
    }

    /**
     * Gets the shadow/border color for a brick type (darker version for retro effect).
     *
     * @param brickType The brick type identifier (0-7)
     * @return The darker shadow color for the brick border
     */
    public static Paint getShadowColor(int brickType) {
        switch (brickType) {
            case 0: return Color.TRANSPARENT;
            case 1: return Color.rgb(0, 136, 136);      // Darker Cyan
            case 2: return Color.rgb(102, 34, 170);     // Darker Purple
            case 3: return Color.rgb(0, 102, 0);        // Darker Green
            case 4: return Color.rgb(204, 163, 0);      // Darker Yellow
            case 5: return Color.rgb(204, 34, 68);      // Darker Pink
            case 6: return Color.rgb(204, 102, 34);     // Darker Orange
            case 7: return Color.rgb(34, 68, 204);      // Darker Blue
            default: return Color.DARKGRAY;
        }
    }
}
