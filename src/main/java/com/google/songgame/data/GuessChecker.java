package com.google.songgame.data;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;

/**
 * Contains various helper methods revolving around checking a users guess including checking whether a user has already correctly guessed, whether a guess is correct, and updating the game state if a user has guessed correctly
 */
public final class GuessChecker {
  private static final int POINTS_PER_ROUND = 100;
  /**
   * Checks if the user has already guessed correctly.
   * 
   * <p>Checks for the current round if the user has already guessed correctly and returns true or false
   */
  public static boolean hasUserPreviouslyGuessedCorrect(Entity user, Entity game) {
    String userId = (String) user.getProperty("userId");
    EmbeddedEntity currentRound = (EmbeddedEntity) game.getProperty("currentRound");
    EmbeddedEntity userGuessStatuses =
        (EmbeddedEntity) currentRound.getProperty("userGuessStatuses");
    boolean userGuessStatus = (Boolean) userGuessStatuses.getProperty(userId);
    return userGuessStatus;
  }

  /**
   * Checks if the given guess is correct.
   * 
   * <p>Checks if the guess is correct and matches the video title of the current round, ignoring case
   */
  public static boolean isCorrectGuess(String guess, Entity game) {
    EmbeddedEntity currentRound = (EmbeddedEntity) game.getProperty("currentRound");
    EmbeddedEntity currentVideo = (EmbeddedEntity) currentRound.getProperty("video");
    String videoTitle = (String) currentVideo.getProperty("title");
    guess = guess.toLowerCase();
    return guess.equals(videoTitle);
  }

  /**
   * Updates the game state to indicate the user has correctly guessed
   * 
   * <p>Updates for the current round that the user has guessed correctly. Updates in the userGuessStatuses map stored in the currentRound entity of the game.
   */
  public static Entity markUserGuessedCorrectly(Entity user, Entity game) {
    String userId = (String) user.getProperty("userId");
    EmbeddedEntity currentRound = (EmbeddedEntity) game.getProperty("currentRound");
    EmbeddedEntity userGuessStatuses =
        (EmbeddedEntity) currentRound.getProperty("userGuessStatuses");
    userGuessStatuses.setProperty(userId, true);
    game.setProperty("currentRound", currentRound);
    return game;
  }
  
  /**
   * Updates the game state by increasing the users points
   * 
   * <p>Updates for the given user the point total. Updates in the userPoints map.
   */
  public static Entity assignUserPoints(Entity user, Entity game) {
    String userId = (String) user.getProperty("userId");
    EmbeddedEntity userPoints = (EmbeddedEntity) game.getProperty("userPoints");
    long currentUserPoints = (Long) userPoints.getProperty(userId);
    // TODO: @salilnadkarni update to change points given depending on time answered
    long updatedUserPoints = currentUserPoints + POINTS_PER_ROUND;
    userPoints.setProperty(userId, updatedUserPoints);
    game.setProperty("userPoints", userPoints);
    return game;
  }
}
