package com.comp2042;

import java.net.URL;

import javafx.scene.text.Font;

/**
 * Utility class to load and manage custom fonts
 */
public class FontLoader {
    
    private static String fontFamilyName = null;
    private static boolean fontLoaded = false;
    
    /**
     * Loads the digital.ttf font and returns its family name
     * @return The font family name, or null if loading failed
     */
    public static String loadFont() {
        if (fontFamilyName != null) {
            return fontFamilyName; // Already loaded
        }

        // Try loading font using InputStream (more reliable)
        try (java.io.InputStream fontStream = FontLoader.class.getClassLoader().getResourceAsStream("digital.ttf")) {
            if (fontStream != null) {
                Font loadedFont = Font.loadFont(fontStream, 12);
                if (loadedFont != null) {
                    fontFamilyName = loadedFont.getFamily();
                    fontLoaded = true;
                    System.out.println("Font loaded successfully. Family name: '" + fontFamilyName + "'");
                    // Verify the font is in the available families
                    boolean isAvailable = javafx.scene.text.Font.getFamilies().contains(fontFamilyName);
                    System.out.println("Font is available in system: " + isAvailable);
                    if (!isAvailable) {
                        System.err.println("Warning: Font loaded but not found in available families!");
                        System.err.println("Trying to find similar font names...");
                        for (String family : Font.getFamilies()) {
                            if (family.toLowerCase().contains("digital")) {
                                System.out.println("Found similar font: " + family);
                            }
                        }
                    }
                    return fontFamilyName;
                } else {
                    System.err.println("Failed to load font - Font.loadFont returned null");
                }
            } else {
                System.err.println("Font resource stream is null - digital.ttf not found");
            }
        } catch (Exception e) {
            System.err.println("Exception loading font: " + e.getMessage());
            e.printStackTrace();
        }

        // Fallback: Try URL method
        try {
            URL fontUrl = FontLoader.class.getClassLoader().getResource("digital.ttf");
            if (fontUrl != null) {
                Font loadedFont = Font.loadFont(fontUrl.toExternalForm(), 12);
                if (loadedFont != null) {
                    fontFamilyName = loadedFont.getFamily();
                    fontLoaded = true;
                    System.out.println("Font loaded successfully via URL. Family name: '" + fontFamilyName + "'");
                    return fontFamilyName;
                }
            }
        } catch (Exception e) {
            System.err.println("Exception loading font via URL: " + e.getMessage());
        }

        return null;
    }
    
    /**
     * Gets the font family name if the font has been loaded
     * @return The font family name, or "System" as fallback
     */
    public static String getFontFamily() {
        if (fontFamilyName == null) {
            loadFont();
        }
        if (fontFamilyName != null) {
            return fontFamilyName;
        }
        // Try to find the font by checking available families
        for (String family : Font.getFamilies()) {
            if (family.toLowerCase().contains("digital")) {
                fontFamilyName = family;
                System.out.println("Found font by name matching: " + family);
                return family;
            }
        }
        return "System"; // Fallback to system font
    }
    
    /**
     * Creates a Font object with the custom font family
     * @param size The font size
     * @return A Font object, or null if font is not loaded
     */
    public static Font getFont(double size) {
        String family = getFontFamily();
        if (family != null && !family.equals("System")) {
            return Font.font(family, size);
        }
        return Font.font(size); // Return default font if custom font not available
    }
    
    /**
     * Checks if the font was successfully loaded
     * @return true if font is loaded, false otherwise
     */
    public static boolean isFontLoaded() {
        return fontLoaded && fontFamilyName != null;
    }
}