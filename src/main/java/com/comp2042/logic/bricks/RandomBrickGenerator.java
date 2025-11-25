package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * 7-Bag Random Generator for Tetris pieces.
 * Uses the standard Tetris randomization algorithm where all 7 unique pieces
 * appear once in a shuffled bag before any piece repeats.
 */
public class RandomBrickGenerator implements BrickGenerator {

    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    public RandomBrickGenerator() {
        // Initialize with two bags to ensure we always have preview available
        fillBag();
        fillBag();
    }

    /**
     * Fills the bag with one of each tetromino type in random order
     */
    private void fillBag() {
        List<Brick> bag = new ArrayList<>();
        bag.add(new IBrick());
        bag.add(new JBrick());
        bag.add(new LBrick());
        bag.add(new OBrick());
        bag.add(new SBrick());
        bag.add(new TBrick());
        bag.add(new ZBrick());

        // Shuffle the bag to randomize piece order
        Collections.shuffle(bag);

        // Add all pieces from the shuffled bag to the queue
        nextBricks.addAll(bag);
    }

    @Override
    public Brick getBrick() {
        // Refill bag when we're running low (less than 7 pieces remaining)
        if (nextBricks.size() < 7) {
            fillBag();
        }
        return nextBricks.poll();
    }

    @Override
    public Brick getNextBrick() {
        return nextBricks.peek();
    }
}
