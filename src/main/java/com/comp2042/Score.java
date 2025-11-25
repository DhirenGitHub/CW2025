package com.comp2042;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty linesCleared = new SimpleIntegerProperty(0);
    private final IntegerProperty level = new SimpleIntegerProperty(1);

    public IntegerProperty scoreProperty() {
        return score;
    }

    public IntegerProperty linesClearedProperty() {
        return linesCleared;
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    public void add(int i){
        score.setValue(score.getValue() + i);
    }

    public void addLinesCleared(int lines) {
        linesCleared.setValue(linesCleared.getValue() + lines);
        updateLevel();
    }

    private void updateLevel() {
        // Level = (lines cleared / 10) + 1
        int newLevel = (linesCleared.getValue() / 10) + 1;
        level.setValue(newLevel);
    }

    public void reset() {
        score.setValue(0);
        linesCleared.setValue(0);
        level.setValue(1);
    }
}
