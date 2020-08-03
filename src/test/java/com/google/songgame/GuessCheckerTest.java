package com.google.songgame.data;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class GuessCheckerTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void checkHasUserPreviouslyGuessed() {
    Entity notGuessedUser = createBill();
    Entity alreadyGuessedUser = createJane();
    Entity currentGame = createGame();

    Assert.assertEquals(
        false, GuessChecker.hasUserPreviouslyGuessedCorrect(notGuessedUser, currentGame));
    Assert.assertEquals(
        true, GuessChecker.hasUserPreviouslyGuessedCorrect(alreadyGuessedUser, currentGame));
  }

  @Test
  public void checkMarkUserGuessedCorrectly() {
    Entity notGuessedUser = createBill();
    Entity willGuessUser = createBob();
    Entity currentGame = createGame();

    Assert.assertEquals(
        false, GuessChecker.hasUserPreviouslyGuessedCorrect(notGuessedUser, currentGame));
    Assert.assertEquals(
        false, GuessChecker.hasUserPreviouslyGuessedCorrect(willGuessUser, currentGame));

    currentGame = GuessChecker.markUserGuessedCorrectly(willGuessUser, currentGame);

    Assert.assertEquals(
        false, GuessChecker.hasUserPreviouslyGuessedCorrect(notGuessedUser, currentGame));
    Assert.assertEquals(
        true, GuessChecker.hasUserPreviouslyGuessedCorrect(willGuessUser, currentGame));
  }

  @Test
  public void checkAssignUserPoints() {
    Entity notGuessedUser = createBill();
    Entity willGuessUser = createBob();
    Entity currentGame = createGame();

    long billPoint =
        (Long) ((EmbeddedEntity) currentGame.getProperty("userPoints")).getProperty("123");
    long bobPoint =
        (Long) ((EmbeddedEntity) currentGame.getProperty("userPoints")).getProperty("789");

    Assert.assertEquals(0, billPoint);
    Assert.assertEquals(0, bobPoint);

    currentGame = GuessChecker.assignUserPoints(willGuessUser, currentGame);

    billPoint = (Long) ((EmbeddedEntity) currentGame.getProperty("userPoints")).getProperty("123");
    bobPoint = (Long) ((EmbeddedEntity) currentGame.getProperty("userPoints")).getProperty("789");

    Assert.assertEquals(0L, billPoint);
    Assert.assertEquals(100L, bobPoint);
  }

  @Test
  public void checkIsCorrectGuessCasing() {
    Entity currentGame = createGame();
    Assert.assertEquals(true, GuessChecker.isCorrectGuess("hey jude", currentGame));
    Assert.assertEquals(true, GuessChecker.isCorrectGuess("Hey Jude", currentGame));
    Assert.assertEquals(true, GuessChecker.isCorrectGuess("hEy JUde", currentGame));
    Assert.assertEquals(false, GuessChecker.isCorrectGuess("hey jade", currentGame));
  }

  private Entity createBill() {
    Entity bill = new Entity("User");
    bill.setProperty("userId", "123");
    bill.setProperty("username", "Bill");
    return bill;
  }

  private Entity createJane() {
    Entity bill = new Entity("User");
    bill.setProperty("userId", "567");
    bill.setProperty("username", "Jane");
    return bill;
  }

  private Entity createBob() {
    Entity bob = new Entity("User");
    bob.setProperty("userId", "789");
    bob.setProperty("username", "Bob");
    return bob;
  }

  private Entity createGame() {
    Entity currentGame = new Entity("Game");

    EmbeddedEntity userPoints = new EmbeddedEntity();
    userPoints.setProperty("123", 0L);
    userPoints.setProperty("567", 100L);
    userPoints.setProperty("789", 0L);
    currentGame.setProperty("userPoints", userPoints);

    EmbeddedEntity currentRound = new EmbeddedEntity();

    EmbeddedEntity currentVideo = new EmbeddedEntity();
    currentVideo.setProperty("title", "hey jude");
    currentVideo.setProperty("videoId", "abc");
    currentRound.setProperty("video", currentVideo);

    EmbeddedEntity userGuessStatuses = new EmbeddedEntity();
    userGuessStatuses.setProperty("123", false);
    userGuessStatuses.setProperty("567", true);
    userGuessStatuses.setProperty("789", false);
    currentRound.setProperty("userGuessStatuses", userGuessStatuses);

    currentGame.setProperty("currentRound", currentRound);

    return currentGame;
  }
}
