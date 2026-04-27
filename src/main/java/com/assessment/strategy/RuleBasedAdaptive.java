package com.assessment.strategy;

import com.assessment.model.QuizSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RuleBasedAdaptive Class - Implements AdaptiveStrategy interface
 * 
 * OOP Concepts:
 * - Polymorphism: Implements AdaptiveStrategy interface
 * - Method Overriding: Implements all abstract methods
 * - Encapsulation: Private constants and logic
 * 
 * Adaptive Rules:
 * - 3 consecutive correct answers → increase difficulty (max 3)
 * - 2 consecutive wrong answers → decrease difficulty (min 1)
 */
public class RuleBasedAdaptive implements AdaptiveStrategy {

    private static final Logger logger = LoggerFactory.getLogger(RuleBasedAdaptive.class);

    // ---- Private Constants (Encapsulation) ----
    private static final int MIN_DIFFICULTY = 1;
    private static final int MAX_DIFFICULTY = 3;
    private static final int CORRECT_STREAK_THRESHOLD = 3;  // Correct answers to level up
    private static final int WRONG_STREAK_THRESHOLD = 2;    // Wrong answers to level down

    // ---- Implementing Interface Methods (Polymorphism) ----

    /**
     * Applies rule-based adaptive difficulty adjustment.
     * 
     * Logic:
     * 1. If answer was correct: increment consecutive correct, reset wrong
     * 2. If answer was wrong: increment consecutive wrong, reset correct
     * 3. Check if thresholds are met and adjust difficulty accordingly
     * 
     * @param session Current quiz session
     * @param wasCorrect Whether the latest answer was correct
     * @return Updated difficulty level
     */
    @Override
    public int adjustDifficulty(QuizSession session, boolean wasCorrect) {
        int currentDifficulty = session.getCurrentDifficulty();
        int consecutiveCorrect = session.getConsecutiveCorrect();
        int consecutiveWrong = session.getConsecutiveWrong();

        if (wasCorrect) {
            // Correct answer: update streaks
            consecutiveCorrect++;
            consecutiveWrong = 0;

            // Check if we should increase difficulty
            if (consecutiveCorrect >= CORRECT_STREAK_THRESHOLD) {
                if (currentDifficulty < MAX_DIFFICULTY) {
                    currentDifficulty++;
                    logger.debug("[Adaptive] Difficulty INCREASED to {}", currentDifficulty);
                }
                consecutiveCorrect = 0; // Reset streak after adjustment
            }
        } else {
            // Wrong answer: update streaks
            consecutiveWrong++;
            consecutiveCorrect = 0;

            // Check if we should decrease difficulty
            if (consecutiveWrong >= WRONG_STREAK_THRESHOLD) {
                if (currentDifficulty > MIN_DIFFICULTY) {
                    currentDifficulty--;
                    logger.debug("[Adaptive] Difficulty DECREASED to {}", currentDifficulty);
                }
                consecutiveWrong = 0; // Reset streak after adjustment
            }
        }

        // Update session with new values
        session.setCurrentDifficulty(currentDifficulty);
        session.setConsecutiveCorrect(consecutiveCorrect);
        session.setConsecutiveWrong(consecutiveWrong);

        return currentDifficulty;
    }

    @Override
    public String getStrategyName() {
        return "Rule-Based Adaptive Strategy";
    }

    @Override
    public int getMinDifficulty() {
        return MIN_DIFFICULTY;
    }

    @Override
    public int getMaxDifficulty() {
        return MAX_DIFFICULTY;
    }
}
