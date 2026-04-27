package com.assessment.strategy;

import com.assessment.model.QuizSession;

/**
 * AdaptiveStrategy Interface - Demonstrates ABSTRACTION
 * 
 * OOP Concepts:
 * - Abstraction: Defines a contract for adaptive difficulty logic
 *   without specifying implementation details
 * - Polymorphism: Different strategies can be swapped at runtime
 * 
 * Any class implementing this interface MUST provide logic for
 * adjusting quiz difficulty based on user performance.
 */
public interface AdaptiveStrategy {

    /**
     * Adjust the difficulty level of the quiz session based on performance.
     * 
     * @param session The current quiz session containing streak data
     * @param wasCorrect Whether the last answer was correct
     * @return The new difficulty level (1 = Easy, 2 = Medium, 3 = Hard)
     */
    int adjustDifficulty(QuizSession session, boolean wasCorrect);

    /**
     * Get the name of this adaptive strategy.
     * @return Strategy name
     */
    String getStrategyName();

    /**
     * Get the minimum allowed difficulty level.
     * @return Minimum difficulty
     */
    int getMinDifficulty();

    /**
     * Get the maximum allowed difficulty level.
     * @return Maximum difficulty
     */
    int getMaxDifficulty();
}
