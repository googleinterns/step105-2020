package com.google.songgame.data;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;

public final class GuessChecker {
  private final static int POINTS_PER_ROUND = 100;

  public static boolean hasUserPreviouslyGuessed(Entity user, Entity game) {
    String userId = (String) user.getProperty("userId");
    EmbeddedEntity currentRound = (EmbeddedEntity) game.getProperty("currentRound");
    EmbeddedEntity userGuessStatuses = (EmbeddedEntity) currentRound.getProperty("userGuessStatuses");
    boolean userGuessStatus = (Boolean) userGuessStatuses.getProperty(userId);
    return userGuessStatus;
  }

  public static boolean isCorrectGuess(String guess, Entity game) {
    EmbeddedEntity currentRound = (EmbeddedEntity) game.getProperty("currentRound");
    EmbeddedEntity currentVideo = (EmbeddedEntity) currentRound.getProperty("video");
    String videoTitle = (String) currentVideo.getProperty("title");
    String guess = message.toLowerCase();
    return guess.equals(videoTitle);
  }

  public static Entity markUserGuessedCorrectly(Entity user, Entity game) {
    String userId = (String) user.getProperty("userId");
    EmbeddedEntity currentRound = (EmbeddedEntity) game.getProperty("currentROund");
    EmbeddedEntity userGuessStatuses = (EmbeddedEntity) currentRound.getProperty("userGuessStatuses");
    userGuessStatuses.setProperty(userId, true);
    game.setProperty("currentRound", currentRound);
    return game;
  }

  public static Entity assignUserPoint(Entity user, Entity game) {
    String userId = (String) user.getProperty("userId");
    EmbeddedEntity userPoints = (EmbeddedEntity) currentGame.getProperty("userPoints");
    long currentUserPoints = (Long) userPoints.getProperty(userId);
    long updatedUserPoints = currentUserPoints + POINTS_PER_ROUND;
    userPoints.setProperty(userId, updatedUserPoints);
    game.setProperty("userPoints", userPoints);
    return game;
  }

}