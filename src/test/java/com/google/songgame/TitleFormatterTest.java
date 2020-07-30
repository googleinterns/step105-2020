package com.google.songgame.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/*
  Tests taken from the following playlist
  https://music.youtube.com/playlist?list=RDCLAK5uy_kmPRjHDECIcuVwnKsx2Ng7fyNgFKWNJFs
*/

@RunWith(JUnit4.class)
public final class TitleFormatterTest {

  @Test
  public void simpleCapitalization() {
    String testString = "Marvins Room";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "marvins room");

    testString = "BLACK PARADE";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "black parade");
  }

  @Test
  public void removeOneGroup() {
    String testString = "Sativa (ft. Swae Lee)";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "sativa");

    testString = "Get You (feat. Kali Uchis)";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "get you");

    testString = "Promises (feat. Namiko, Miyagi & Miyagi & Namiko)";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "promises");

    testString = "Tattoo (Remix)";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "tattoo");
  }

  @Test
  public void removeMultipleGroups() {
    String testString = "Savage (Remix) (feat. Beyoncé)";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "savage");

    testString = "Life is Good (Official Music Video) (feat. Drake)";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "life is good");
  }

  @Test
  public void removeMultipleGroupsSquareAndParenthesis() {
    String testString = "WHATS POPPIN [Remix] (feat. DaBaby, Tory Lanez & Lil Wayne)";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "whats poppin");

    testString = "Djadja [Remix] (feat. Afro B)";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "djadja");

    testString = "Roses (Imanbek Remix) [Latino Gang]";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "roses");
  }

  @Test
  public void removeOnlyInvalidParenthesisGroups() {
    String testString = "ily (i love you baby) (feat. Emilee)";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "ily (i love you baby)");

    testString = "death bed (coffee for your head)";
    Assert.assertEquals(
        TitleFormatter.formatVideoTitle(testString), "death bed (coffee for your head)");
  }

  @Test
  public void removeArtistPrefix() {
    String testString = "NBA YoungBoy - ALL IN";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "all in");
  }

  @Test
  public void removeArtistPrefixAndGroup() {
    String testString = "Juice WRLD ft. Marshmello - Come & Go (Official Audio)";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "come & go");

    testString = "Rod Wave - Girl Of My Dreams (Official Music Video)";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "girl of my dreams");

    testString = "YG - Swag (Official Music Video)";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "swag");

    testString = "Chase B & Don Toliver - Cafeteria (feat. Gunna) (Official Video)";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "cafeteria");

    testString = "King Von - Why He Told (Official Video)";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "why he told");
  }

  // TODO: @salilnadkarni add error checking for titles like this
  @Test
  @Ignore("TitleFormatter unable to handle words not in brackets/parens")
  public void removeUnenclosedKeywords() {
    String testString = "La Jeepeta Remix (Lyric Video)";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "la jeepeta");

    testString = "Gucci Mane - Both Sides feat. Lil Baby";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "both sides");

    testString = "ENEE - Supalonely (Audio) ft. Gus Dapperton";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "supalonely");

    testString = "BLACKPINK - 'How You Like That' M/V";
    Assert.assertEquals(TitleFormatter.formatVideoTitle(testString), "how you like that");
  }
}
